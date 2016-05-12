package app.myapplication;

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
import com.jjoe64.graphview.Viewport;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.IOException;

//***********  Marlon's Tab ************************//

public class TabFragment3 extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.tab_fragment_3, container, false);
       /* try {
            new EchoOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new LineFeedOffCommand().run(socket.getInputStream(), socket.getOutputStream());
            new TimeoutCommand(125).run(socket.getInputStream(), socket.getOutputStream());
            new SelectProtocolCommand(ObdProtocols.AUTO).run(socket.getInputStream(), socket.getOutputStream());
            // new AmbientAirTemperatureCommand().run(socket.getInputStream(), socket.getOutputStream());

            final RPMCommand engineRpmCommand = new RPMCommand();
            final SpeedCommand speedCommand = new SpeedCommand();



            new CountDownTimer(18000000, 500) {

                LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                        new DataPoint(0, 0)
                });

                int i = 0;
                GraphView graph = (GraphView) findViewById(R.id.graph);
                public void onTick(long millisUntilFinished) {

                    if (!Thread.currentThread().isInterrupted())
                    {
                        try {
                            engineRpmCommand.run(socket.getInputStream(), socket.getOutputStream());
                            speedCommand.run(socket.getInputStream(), socket.getOutputStream());
                            // TODO handle commands result
                            String rpmstring = engineRpmCommand.getFormattedResult();

                            TextView rpmT = (TextView) findViewById(R.id.RPM);
                            rpmT.setText("RPM: " + rpmstring);
                            //final Integer timerDuration = 1800000;

                            int rpmi = engineRpmCommand.getRPM();
                            i++;
                            Viewport graphView = graph.getViewport();
                            graphView.setScrollable(true);
                            series.appendData(new DataPoint(i,rpmi),true,100);
                            graph.addSeries(series);



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
                    TextView timerT = (TextView) findViewById(R.id.RPM);
                    timerT.setText("Time!");

                }
            }.start();


        } catch (Exception e) {
            TextView rpmT2 = (TextView) findViewById(R.id.RPM);
            rpmT2.setText("We messed up in the while loop");

        }*/





    }
}
