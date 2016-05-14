package app.myapplication;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import java.io.IOException;

public class TabFragment4 extends Fragment {
    private static final String LOG_TAG = "AudioRecordTest";
    private boolean recording = false;
    private boolean playing = false;
    private String filename = null;
    private String path = null;
    private MediaRecorder mRecorder = null;
    private MediaPlayer mPlayer = null;
    private Button b_Rec = null;
    private Button b_Play = null;
    private EditText t_Filename = null;
    private View mMain;

    private static final int RECORDER_BPP = 16;
    private static final int RECORDER_SAMPLERATE = 44100;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_FLOAT;
    private AudioRecord recorder = null;
    private int N = 4096;
    private int bufferSize = N * 4;
    private Thread recordingThread = null;
    private boolean isRecording = false;

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
        recording = false;
        b_Rec.setText(getString(R.string.b_startR));
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
        playing = false;
        b_Play.setText(getString(R.string.b_startP));
    }

    private void startRecording() {
        String f_name_box = t_Filename.getText().toString();
        if (f_name_box.length() > 0)
            filename = path + "/" + f_name_box + ".3gp";
        else
            filename = path + "/audiotest.3gp";
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(filename);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        try {
            mRecorder.prepare();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }
        mRecorder.start();
        recording = true;
        b_Rec.setText(getString(R.string.b_stopR));
/*
        recorder = new AudioRecord(MediaRecorder.AudioSource.MIC, RECORDER_SAMPLERATE,
                RECORDER_CHANNELS, RECORDER_AUDIO_ENCODING, bufferSize);
        recorder.startRecording();
        isRecording = true;
        recordingThread = new Thread(new Runnable() {
            public void run() {
                writeAudioDataToFile();
            }
        }, "AudioRecorder Thread");
        recordingThread.start();
    }

    private void writeAudioDataToFile() {
        float[] mic_data = new float[N];
        short[] buff = new short[N];
        while (isRecording) {
            recorder.read(buff, 0, N);
        }
*/
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
        if (mRecorder != null) {
            stopRecording();
        }

        if (mPlayer != null) {
            stopPlaying();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mMain = inflater.inflate(R.layout.tab_fragment_4, container, false);
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
                if (recording) {
                    stopRecording();
                }
                else {
                    startRecording();
                }
            }
        });
        //AudioRecord.getMinBufferSize()


        return mMain;
    }
}
