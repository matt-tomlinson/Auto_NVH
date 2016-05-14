package app.myapplication;

/**
 * Created by Joseph on 5/7/2016.
 * Modified for to have static cosine and sine tables and to expect 8192 sample size
 */

/*
 * Free FFT and convolution (Java)
 *
 * Copyright (c) 2014 Project Nayuki
 * https://www.nayuki.io/page/free-small-fft-in-multiple-languages
 *
 * (MIT License)
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of
 * the Software, and to permit persons to whom the Software is furnished to do so,
 * subject to the following conditions:
 * - The above copyright notice and this permission notice shall be included in
 *   all copies or substantial portions of the Software.
 * - The Software is provided "as is", without warranty of any kind, express or
 *   implied, including but not limited to the warranties of merchantability,
 *   fitness for a particular purpose and noninfringement. In no event shall the
 *   authors or copyright holders be liable for any claim, damages or other
 *   liability, whether in an action of contract, tort or otherwise, arising from,
 *   out of or in connection with the Software or the use or other dealings in the
 *   Software.
 */

public class Fft {
    private float[] cosTable;
    private float[] sinTable;
    private int N;
    public float Fs;
    public float df;
    private int levels;

    public Fft(){
        this(8192, 44100);
    }
    public Fft(int samples, float sampling){
        N = samples;
        Fs = sampling;
        df = Fs / N;
        cosTable = new float[N / 2];
        sinTable = new float[N / 2];
        levels = 31 - Integer.numberOfLeadingZeros(N);  // Equal to floor(log2(n))
        for (int i = 0; i < N / 2 ; ++i) {
            cosTable[i] = (float)Math.cos(2 * Math.PI * i / N);
            sinTable[i] = (float)Math.sin(2 * Math.PI * i / N);
        }
    }

    public float[] getOmega() {
        float[] omega = new float[N];
        for (int i = 0; i < N; ++i) {
            omega[i] = (i - N / 2) * df;
        }
        return omega;
    }

    // return normalized amplitude in dB
    public float[] getMagnitudeDB(float[] real, float[] imag){
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");

        float [] mag = new float[real.length];
        for (int i = 0; i < real.length; ++i){
            mag[i] = 20 * (float)Math.log10(Math.hypot(real[i], imag[i]) / real.length);
        }
        return mag;
    }

    public float[] shift(float[] mag) {
        float[] shifted = new float[mag.length];
        for (int i = 0; i < N; ++i)
            shifted[i] = mag[(N / 2 + i) % N];
        return shifted;
    }
    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This is a wrapper function.
     */
    public void transform(float[] real, float[] imag) {
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        if (real.length != N)
            throw new IllegalArgumentException("Invalid length. Construct new object.");

        if (N == 0)
            return;
        else if ((N & (N - 1)) == 0)  // Is power of 2
            transformRadix2(real, imag);
        else  // More complicated algorithm for arbitrary sizes
            transformBluestein(real, imag);
    }

    /*
     * Computes the inverse discrete Fourier transform (IDFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This is a wrapper function. This transform does not perform scaling, so the inverse is not a true inverse.
     */
    public void inverseTransform(float[] real, float[] imag) {
        transform(imag, real);
    }


    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector's length must be a power of 2. Uses the Cooley-Tukey decimation-in-time radix-2 algorithm.
     */
    public void transformRadix2(float[] real, float[] imag) {
        // Initialization
        if (real.length != N)
            throw new IllegalArgumentException("Invalid length. Construct new object.");
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        int N = real.length;
        if (1 << levels != N)
            throw new IllegalArgumentException("Length is not a power of 2");

        // Bit-reversed addressing permutation
        for (int i = 0; i < N; i++) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                float temp = real[i];
                real[i] = real[j];
                real[j] = temp;
                temp = imag[i];
                imag[i] = imag[j];
                imag[j] = temp;
            }
        }

        // Cooley-Tukey decimation-in-time radix-2 FFT
        for (int size = 2; size <= N; size *= 2) {
            int halfsize = size / 2;
            int tablestep = N / size;
            for (int i = 0; i < N; i += size) {
                for (int j = i, k = 0; j < i + halfsize; j++, k += tablestep) {
                    float tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    float tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == N)  // Prevent overflow in 'size *= 2'
                break;
        }
    }

    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     * The vector can have any length. This requires the convolution function, which in turn requires the radix-2 FFT function.
     * Uses Bluestein's chirp z-transform algorithm.
     */
    public void transformBluestein(float[] real, float[] imag) {
        // Find a power-of-2 convolution length m such that m >= n * 2 + 1
        if (real.length != N)
            throw new IllegalArgumentException("Invalid length. Construct new object.");
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        if (N >= 0x20000000)
            throw new IllegalArgumentException("Array too large");
        int m = Integer.highestOneBit(N * 2 + 1) << 1;

        // Temporary vectors and preprocessing
        float[] areal = new float[m];
        float[] aimag = new float[m];
        for (int i = 0; i < N; i++) {
            areal[i] =  real[i] * cosTable[i] + imag[i] * sinTable[i];
            aimag[i] = -real[i] * sinTable[i] + imag[i] * cosTable[i];
        }
        float[] breal = new float[m];
        float[] bimag = new float[m];
        breal[0] = cosTable[0];
        bimag[0] = sinTable[0];
        for (int i = 1; i < N; i++) {
            breal[i] = breal[m - i] = cosTable[i];
            bimag[i] = bimag[m - i] = sinTable[i];
        }

        // Convolution
        float[] creal = new float[m];
        float[] cimag = new float[m];
        convolve(areal, aimag, breal, bimag, creal, cimag);

        // Postprocessing
        for (int i = 0; i < N; i++) {
            real[i] =  creal[i] * cosTable[i] + cimag[i] * sinTable[i];
            imag[i] = -creal[i] * sinTable[i] + cimag[i] * cosTable[i];
        }
    }

    /*
     * Computes the circular convolution of the given real vectors. Each vector's length must be the same.
     */
    public void convolve(float[] x, float[] y, float[] out) {
        if (x.length != y.length || x.length != out.length)
            throw new IllegalArgumentException("Mismatched lengths");
        int n = x.length;
        convolve(x, new float[n], y, new float[n], out, new float[n]);
    }

    /*
     * Computes the circular convolution of the given complex vectors. Each vector's length must be the same.
     */
    public void convolve(float[] xreal, float[] ximag, float[] yreal, float[] yimag, float[] outreal, float[] outimag) {
        if (xreal.length != ximag.length || xreal.length != yreal.length || yreal.length != yimag.length || xreal.length != outreal.length || outreal.length != outimag.length)
            throw new IllegalArgumentException("Mismatched lengths");

        int n = xreal.length;
        xreal = xreal.clone();
        ximag = ximag.clone();
        yreal = yreal.clone();
        yimag = yimag.clone();

        transform(xreal, ximag);
        transform(yreal, yimag);
        for (int i = 0; i < n; i++) {
            float temp = xreal[i] * yreal[i] - ximag[i] * yimag[i];
            ximag[i] = ximag[i] * yreal[i] + xreal[i] * yimag[i];
            xreal[i] = temp;
        }
        inverseTransform(xreal, ximag);
        for (int i = 0; i < n; i++) {  // Scaling (because this FFT implementation omits it)
            outreal[i] = xreal[i] / n;
            outimag[i] = ximag[i] / n;
        }
    }
}


