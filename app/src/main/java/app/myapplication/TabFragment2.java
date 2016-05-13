package app.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class TabFragment2 extends Fragment implements SensorEventListener{
    Sensor accelerometer;
    SensorManager sm;
    View mMain = null;
    //TextView X;
    //TextView Y;
    //TextView Z;
    double xc = 0;
    double yc = 0;
    double zc = 0;
    private static int N = 256;
    public double[] xAcc = new double[N];
    public double[] yAcc = new double[N];
    public double[] zAcc = new double[N];
    public double[] imag = new double[N];
    public double[] mag = new double[N];
    public double[] shifted = new double[N];
    public double[] omega = new double[N];
    private int val = 0;

    private CountDownTimer chrono = null;
    private double timer = 0;
    LineGraphSeries<DataPoint> seriesX = null;
    LineGraphSeries<DataPoint> seriesY = null;
    LineGraphSeries<DataPoint> seriesZ = null;
    GraphView graph;
    private Fft myfft = new Fft(N,1000);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMain = inflater.inflate(R.layout.tab_fragment_2, container, false);
        sm = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_FASTEST); //Change onSensorChange speed here

        graph = (GraphView) mMain.findViewById(R.id.graph);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        Viewport graphView = graph.getViewport();
        graphView.setScrollable(true);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-500);
        graph.getViewport().setMaxX(500);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-60);
        graph.getViewport().setMaxY(20);

        return mMain;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xc = event.values[0];
        yc = event.values[1];
        zc = event.values[2];
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onResume() {
        super.onResume();
        timer = 0;
        seriesX = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
        seriesY = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
        seriesZ = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
        seriesX.setTitle("X");
        seriesY.setTitle("Y");
        seriesZ.setTitle("Z");
        seriesX.setColor(Color.BLUE);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.RED);

        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);

        chrono = new CountDownTimer(60000, 1) {
            @Override
            public void onTick(long millisUntilFinished) {
                xAcc[val] = xc;
                yAcc[val] = yc;
                zAcc[val] = zc;
                imag[val] = 0;
                ++val;
                if (val == N) {
                    myfft.transform(xAcc, imag);
                    myfft.getMagnitudeDB(xAcc, imag, mag);
                    myfft.shift(mag, shifted);
                    myfft.getOmega(omega);
                    DataPoint[] dps = new DataPoint[N];
                    for (int i = 0; i < N; ++i){
                        dps[i] = new DataPoint(omega[i], shifted[i]);
                        imag[i] = 0;
                    }
                    seriesX.resetData(dps);

                    myfft.transform(yAcc, imag);
                    myfft.getMagnitudeDB(yAcc, imag, mag);
                    myfft.shift(mag, shifted);
                    myfft.getOmega(omega);
                    for (int i = 0; i < N; ++i){
                        dps[i] = new DataPoint(omega[i], shifted[i]);
                        imag[i] = 0;
                    }
                    seriesY.resetData(dps);

                    myfft.transform(zAcc, imag);
                    myfft.getMagnitudeDB(xAcc, imag, mag);
                    myfft.shift(mag, shifted);
                    myfft.getOmega(omega);
                    for (int i = 0; i < N; ++i){
                        dps[i] = new DataPoint(omega[i], shifted[i]);
                    }
                    seriesZ.resetData(dps);
                    timer++;
                    val = 0;
                }
            }

            @Override
            public void onFinish() {

            }
        };
        chrono.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        chrono.cancel();
        graph.removeAllSeries();
    }


}
