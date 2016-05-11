package app.myapplication;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
    TextView X;
    TextView Y;
    TextView Z;
    LineGraphSeries<DataPoint> seriesX = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
    LineGraphSeries<DataPoint> seriesY = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
    LineGraphSeries<DataPoint> seriesZ = new LineGraphSeries<>(new DataPoint[] {new DataPoint(0, 0)});
    GraphView graph;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMain = inflater.inflate(R.layout.tab_fragment_2, container, false);
        sm = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_UI); //Change onSensorChange speed here

        X = (TextView)mMain.findViewById(R.id.Xaccel);
        Y = (TextView)mMain.findViewById(R.id.Yaccel);
        Z = (TextView)mMain.findViewById(R.id.Zaccel);

        graph = (GraphView) mMain.findViewById(R.id.graph);

        seriesX.setTitle("X");
        seriesY.setTitle("Y");
        seriesZ.setTitle("Z");

        seriesX.setColor(Color.BLUE);
        seriesY.setColor(Color.GREEN);
        seriesZ.setColor(Color.RED);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        Viewport graphView = graph.getViewport();
        graphView.setScrollable(true);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(100);

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-15);
        graph.getViewport().setMaxY(15);

        graph.addSeries(seriesX);
        graph.addSeries(seriesY);
        graph.addSeries(seriesZ);

        return mMain;
    }

    int i = 0;

    @Override
    public void onSensorChanged(SensorEvent event) {
        X.setText(String.valueOf(event.values[0])); // update textView values
        Y.setText(String.valueOf(event.values[1]));
        Z.setText(String.valueOf(event.values[2]));

        updateGraph(i, event.values[0], event.values[1], event.values[2]); // update graphView values
        i++;
    }

    void updateGraph (int time, final float x, final float y, final float z)
    {
        seriesX.appendData(new DataPoint(time, x), true, 100);
        seriesY.appendData(new DataPoint(time, y), true, 100);
        seriesZ.appendData(new DataPoint(time, z), true, 100);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
