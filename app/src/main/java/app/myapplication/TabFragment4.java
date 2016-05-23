package app.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.io.IOException;
import java.util.Arrays;

public class TabFragment4 extends Fragment {
    private static final String LOG_TAG = "AudioRecordTest";
    private boolean playing = false;
    private String filename = null;
    private String path = null;
    private MediaPlayer mPlayer = null;
    private Button b_Rec = null;
    private Button b_Play = null;
    private EditText t_Filename = null;
    private View mMain;

    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private AudioRecord recorder = null;
    private int N = 8192;
//    private int it = 0;
    private int buffer = N;
    private int bufferSizeInBytes = buffer * 2;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private Fft audio_fft = new Fft(N, 44100);
    private double[] real = new double[N];
    private double[] imag = new double[N];
    private double[] mag = new double[N];
    private double[] shifted = new double[N];
    private double[] omega = audio_fft.getOmega();
    LineGraphSeries<DataPoint> series = null;
    GraphView graph;
    public Handler mHandler;

    private void stopRecording() {
        recorder.stop();
        recorder.release();
        recorder = null;
        isRecording = false;
        recordingThread = null;
        b_Rec.setText(getString(R.string.b_startR));
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        playing = false;
        b_Play.setText(getString(R.string.b_startP));
    }

    private void startRecording() {
/*        String f_name_box = t_Filename.getText().toString();
        if (f_name_box.length() > 0)
            filename = path + "/" + f_name_box + ".3gp";
        else
            filename = path + "/audiotest.3gp";
*/
        b_Rec.setText(getString(R.string.b_stopR));

        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSizeInBytes);
        recorder.startRecording();
        isRecording = true;
        recordingThread = new recThread();
        recordingThread.start();
    }

    // write audio data to file thread
    /*class writeThread extends Thread {
        @Override
        public void run() {
            writeAudioFile();
        }
    }

    private void writeAudioFile(){

    }
    */

    class recThread extends Thread {
        @Override
        public void run() {
            getMICdata();
        }
    }

    private void getMICdata() {
        short[] buff = new short[buffer];
        //double[] mic_data;
        while (isRecording) {
            recorder.read(buff, 0, buffer);
            real = short2double(buff);/*
            mic_data = short2double(buff);
            for (int i = 0; i < buffer; ++i) {
                real[it++] = mic_data[i];
                if (it == N)
                    it = 0;
            }*/
            Arrays.fill(imag, 0);
            audio_fft.transform(real, imag);
            mag = audio_fft.getMagnitudeDB(real, imag);
            shifted = audio_fft.shift(mag);
            Message done = mHandler.obtainMessage(1, shifted);
            mHandler.sendMessage(done);
        }
    }

    //Conversion from short to double
    private double[] short2double(short [] audioData){
        double[] micBufferData = new double[buffer];//size may need to change
        for (int i = 0; i < buffer; ++i)
            micBufferData[i] = audioData[i] / 8192.0;
        return micBufferData;
    }


    private void startPlaying() {
        String f_name_box = t_Filename.getText().toString();
        if (f_name_box.length() > 0)
            filename = path + "/" + f_name_box + ".3gp";
        else
            filename = path + "/audiotest.3gp";
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(filename);
            mPlayer.prepare();
            mPlayer.start();
            mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    mPlayer.release();
                    mPlayer = null;
                    b_Play.setText(getString(R.string.b_startP));
                    playing = false;
                }
            });
            playing = true;
            b_Play.setText(getString(R.string.b_stopP));
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (recorder != null) {
            stopRecording();
        }

        if (mPlayer != null) {
            stopPlaying();
        }
        graph.removeAllSeries();
    }

    @Override
    public void onResume() {
        super.onResume();
        series = new LineGraphSeries<>();
        series.setOnDataPointTapListener(new OnDataPointTapListener() {
            @Override
            public void onTap(Series series, DataPointInterface dataPoint) {
                Toast.makeText(mMain.getContext(), "Data Point: " + dataPoint, Toast.LENGTH_SHORT).show();
            }
        });
        graph.addSeries(series);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_4, container, false);
        mHandler = new Handler(Looper.getMainLooper()) {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    double[] result = (double[]) msg.obj;
                    DataPoint[] dps = new DataPoint[N/2];
                    int j = 0;
                    for (int i = N/2; i < N; ++i) {
                        dps[j++] = new DataPoint(omega[i], result[i]);
                    }
                    series.resetData(dps);
                }
            }
        };
        b_Rec = (Button)mMain.findViewById(R.id.bRec);
        b_Play = (Button)mMain.findViewById(R.id.bPlay);
        t_Filename = (EditText)mMain.findViewById(R.id.tFilename);
        path = Environment.getExternalStorageDirectory().getAbsolutePath();
        b_Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (playing) {
                    stopPlaying();
                } else {
                    startPlaying();
                }
            }
        });

        b_Rec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRecording) {
                    stopRecording();
                }
                else {
                    startRecording();
                }
            }
        });

        graph = (GraphView) mMain.findViewById(R.id.audio_graph);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.BOTTOM);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(4000); //22050 eventually?

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-60);
        graph.getViewport().setMaxY(20);

        return mMain;
    }
}
