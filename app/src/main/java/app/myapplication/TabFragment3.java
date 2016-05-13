package app.myapplication;

import android.bluetooth.BluetoothSocket;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.pires.obd.commands.SpeedCommand;
import com.github.pires.obd.commands.engine.RPMCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.commands.protocol.TimeoutCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;
import java.util.Timer;

//***********  Marlon's Tab ************************//

public class TabFragment3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.tab_fragment_3, container, false);

        try {
           /* final BluetoothSocket socket = ((MyApplication) getActivity().getApplication()).getGlobalBluetoothSocket();
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            // new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());

            final RPMCommand engineRpmCommand = new RPMCommand();
            final SpeedCommand speedCommand = new SpeedCommand();*/

            final LineGraphSeries<DataPoint> rpmSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, 0)});

            final LineGraphSeries<DataPoint> speedSeries = new LineGraphSeries<DataPoint>(new DataPoint[] {
                    new DataPoint(0, 0)});


            final GraphView graphRPM = (GraphView) view.findViewById(R.id.graphRPM);
            final GraphView graphSpeed = (GraphView) view.findViewById(R.id.graphSpeed);

            Viewport graphViewRPM = graphRPM.getViewport();
            Viewport graphViewSpeed = graphSpeed.getViewport();

            graphRPM.setBackgroundColor(Color.rgb(55,55,55));
            graphRPM.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
            graphRPM.getGridLabelRenderer().setGridColor(Color.rgb(204,229,255));
            graphRPM.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(204,229,255));
            graphRPM.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(204,229,255));
            graphRPM.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.rgb(204,229,255));
            graphRPM.getGridLabelRenderer().reloadStyles();
            rpmSeries.setColor(Color.rgb(204,0,0));

            graphSpeed.setBackgroundColor(Color.rgb(55,55,55));
            graphSpeed.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.BOTH);
            graphSpeed.getGridLabelRenderer().setGridColor(Color.rgb(204,229,255));
            graphSpeed.getGridLabelRenderer().setHorizontalLabelsColor(Color.rgb(204,229,255));
            graphSpeed.getGridLabelRenderer().setVerticalLabelsColor(Color.rgb(204,229,255));
            graphSpeed.getGridLabelRenderer().setVerticalLabelsSecondScaleColor(Color.rgb(204,229,255));
            graphSpeed.getGridLabelRenderer().reloadStyles();
            speedSeries.setColor(Color.rgb(204,0,0));

            //set graphs to be scrollable
            graphViewRPM.setScrollable(true);
            graphViewSpeed.setScrollable(true);

            //subject to change
            //for now RPM bounds will be x-axis: 0 to 100 y-axis: 0 - 3000
            graphViewRPM.setXAxisBoundsManual(true);
            graphViewRPM.setMinX(0);
            graphViewRPM.setMaxX(100);

            graphViewRPM.setYAxisBoundsManual(true);
            graphViewRPM.setMinY(0);
            graphViewRPM.setMaxY(3000);


            //subject to change
            //for now Speed bounds will be x-axis: 0 to 100 y-axis: 0 - 100
            graphViewSpeed.setXAxisBoundsManual(true);
            graphViewSpeed.setMinX(0);
            graphViewSpeed.setMaxX(100);

            graphViewSpeed.setYAxisBoundsManual(true);
            graphViewSpeed.setMinY(0);
            graphViewSpeed.setMaxY(100);


            graphRPM.addSeries(rpmSeries);
            graphSpeed.addSeries(speedSeries);


            final TextView textRPM = (TextView) view.findViewById(R.id.textRPM);
            final TextView textSpeed = (TextView) view.findViewById(R.id.textSpeed);
            final TextView textRPMFrequency = (TextView) view.findViewById(R.id.textRPMFrequency);
            final TextView textSpeedFrequency = (TextView) view.findViewById(R.id.textSpeedFrequency);

            new CountDownTimer(18000000, 500) {

                int i = 0;
                public void onTick(long millisUntilFinished) {

                    if (!Thread.currentThread().isInterrupted())
                    {
                        try {
                           /* engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                            speedCommand.run(socket.getInputStream(), socket.getOutputStream());

                            // TODO handle commands result
                            String rpmString = engineRpmCommand.getFormattedResult();
                            String speedString = speedCommand.getFormattedResult();

                            textRPM.setText("RPM: " + rpmString);
                            textSpeed.setText("Speed: " + speedString);


                            int rpmNum = engineRpmCommand.getRPM();
                            int speedNum = speedCommand.getMetricSpeed();*/
                            i++;


                            rpmSeries.appendData(new DataPoint(i,i),true,100);
                            //graphRPM.addSeries(rpmSeries);

                            speedSeries.appendData(new DataPoint(i,i),true,100);
                            //graphSpeed.addSeries(speedSeries);

                            //*****--- TEMPORARY BLOCK OF CODE ----*********
                            textRPM.setText("RPM: " + i);
                            textSpeed.setText("Speed: " + i);
                            float rpmFrequency = (float)i/60;
                            textRPMFrequency.setText("Frequency: " + String.format("%.3f",rpmFrequency) + "Hz");
                            textSpeedFrequency.setText("Frequency: " + String.format("%.3f",rpmFrequency) + "Hz");



                        }
                        catch (Exception e){
                            //
                        }



                        //Log.d(TAG, "RPM: " + engineRpmCommand.getFormattedResult());
                        // Log.d(TAG, "Speed: " + speedCommand.getFormattedResult());

                        //Thread.sleep(500);
                    }
                }

                public void onFinish() {
                    TextView timerT = (TextView) view.findViewById(R.id.textRPM);
                    timerT.setText("Time!");

                }
            }.start();


        } catch (Exception e) {
            TextView rpmT2 = (TextView) view.findViewById(R.id.textRPM);
            rpmT2.setText("We messed up in the while loop");

        }



        return view;

    }
}
