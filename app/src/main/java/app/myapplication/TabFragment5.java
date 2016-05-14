package app.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

public class TabFragment5 extends Fragment {
    private View mMain = null;
    private int n = 8192;
    private float Fs = 44100;
    private Fft myFft = new Fft(n, Fs);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_5, container, false);

        float f = 200;
        float[] y_real = new float[n];
        float[] y_imag = new float[n];
        float[] Y_mag;
        float[] Y_shifted;
        float[] omega;

        GraphView graph = (GraphView) mMain.findViewById(R.id.graph);
        DataPoint[] dps = new DataPoint[n];
        for (int i = 0; i < n; ++i) {
            y_real[i] = 2 * (float)Math.cos(2 * Math.PI * f * i / Fs);
            y_imag[i] = 0;
        }

        myFft.transform(y_real, y_imag);
        Y_mag = myFft.getMagnitudeDB(y_real, y_imag);
        Y_shifted = myFft.shift(Y_mag);
        omega = myFft.getOmega();

        for (int i = 0; i < n; ++i){
            dps[i] = new DataPoint(omega[i], Y_shifted[i]);
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<>(dps);
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mMain.getContext(), "Data Point: "+dataPoint, Toast.LENGTH_SHORT).show();
            }
        });
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-400);
        graph.getViewport().setMaxX(400);
        graph.addSeries(series);

        return mMain;
    }
}
