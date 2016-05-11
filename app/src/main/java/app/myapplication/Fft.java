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
    private final static double[] cosTable = new double[4096];
    private final static double[] sinTable = new double[4096];
    private final static int N = 8192;
    private final static double df = 44100 / 8192;
    private final static int levels = 13;
    public double[] w = new double[8192];

    public Fft(){
        for (int i = 0; i < N / 2 ; ++i) {
            cosTable[i] = Math.cos(2 * Math.PI * i / N);
            sinTable[i] = Math.sin(2 * Math.PI * i / N);
        }
        for (int i = 0; i < N; ++i) {
            w[i] = (i - N / 2) * df;
        }
    }

    /*
     * Computes the discrete Fourier transform (DFT) of the given complex vector, storing the result back into the vector.
     */
    public static void transform(double[] real, double[] imag, double[] fft) {
        // Initialization
        if (real.length != imag.length)
            throw new IllegalArgumentException("Mismatched lengths");
        if (real.length != 8192)
            throw new IllegalArgumentException("Invalid input container length");
        if (fft.length != 8192)
            throw new IllegalArgumentException("Invalid output container length");

        // Bit-reversed addressing permutation
        for (int i = 0; i < N; ++i) {
            int j = Integer.reverse(i) >>> (32 - levels);
            if (j > i) {
                double temp = real[i];
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
                    double tpre =  real[j+halfsize] * cosTable[k] + imag[j+halfsize] * sinTable[k];
                    double tpim = -real[j+halfsize] * sinTable[k] + imag[j+halfsize] * cosTable[k];
                    real[j + halfsize] = real[j] - tpre;
                    imag[j + halfsize] = imag[j] - tpim;
                    real[j] += tpre;
                    imag[j] += tpim;
                }
            }
            if (size == N)  // Prevent overflow in 'size *= 2'
                break;
        }
        for (int i = 0; i < N; ++i){
            fft[i] = 20 * Math.log10(Math.hypot(real[i], imag[i]) / N);
        }
    }
}


