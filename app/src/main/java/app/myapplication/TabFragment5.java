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
    private Fft myFft = new Fft();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_5, container, false);
        int n = 8192;
        double Fs = 44100;
        double f = 200;
        double[] y_real = new double[n];
        double[] y_imag = new double[n];
        double[] Y_mag = new double[n];
        double[] Y_shifted = new double[n];
        double[] omega = new double[n];

        GraphView graph = (GraphView) mMain.findViewById(R.id.graph);
        DataPoint[] dps = new DataPoint[n];
        for (int i = 0; i < n; ++i) {
            y_real[i] = 2 * Math.cos(2 * Math.PI * f * i / Fs);
            y_imag[i] = 0;
        }

        myFft.transform(y_real, y_imag);
        myFft.getMagnitudeDB(y_real, y_imag, Y_mag);
        myFft.shift(Y_mag, Y_shifted);
        myFft.getOmega(omega);

        for (int i = 0; i < n; ++i){
            dps[i] = new DataPoint(omega[i], Y_shifted[i]);
        }

        PointsGraphSeries<DataPoint> series = new PointsGraphSeries<DataPoint>(dps);
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
