package app.myapplication;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class TabFragment2 extends Fragment implements SensorEventListener{
    Sensor accelerometer;
    SensorManager sm;
    View mMain = null;
    TextView X;
    TextView Y;
    TextView Z;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        mMain = inflater.inflate(R.layout.tab_fragment_2, container, false);

        sm = (SensorManager) this.getActivity().getSystemService(Activity.SENSOR_SERVICE);

        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);

        X = (TextView)mMain.findViewById(R.id.Xaccel);
        Y = (TextView)mMain.findViewById(R.id.Yaccel);
        Z = (TextView)mMain.findViewById(R.id.Zaccel);

        return mMain;
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        X.setText(String.valueOf(event.values[0]));
        Y.setText(String.valueOf(event.values[1]));
        Z.setText(String.valueOf(event.values[2]));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }



}
