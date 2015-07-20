package ger.location;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.widget.Button;

/**
 * Created by Ger on 11/10/14.
 */
public class Alarma extends Activity {

    Vibrator v;
    Handler mHandler = new Handler();
    MediaPlayer mMediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.destino);
        boolean vibrar = false, sonar = false;

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            vibrar = extras.getBoolean("vibrar");
            sonar = extras.getBoolean("sonar");
        }

        if (vibrar){

            new Thread(new Runnable() {
                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    while (true) {
                        try {
                            Thread.sleep(1500);
                            mHandler.post(new Runnable() {

                                @Override
                                public void run() {
                                    // TODO Auto-generated method stub
                                    // Write your code here to update the UI.
                                    v = (Vibrator) getSystemService(getApplicationContext().VIBRATOR_SERVICE);
                                    v.vibrate(1000);
                                }
                            });
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }
                }
            }).start();
        }
        if (sonar){
            mMediaPlayer = new MediaPlayer();
            mMediaPlayer = MediaPlayer.create(Alarma.this, R.raw.alarm);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setLooping(true);
            mMediaPlayer.start();

        }
        final boolean vi = vibrar;
        Button cancelar = (Button) findViewById(R.id.cancelar);
        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
                if (vi){
                    v.cancel();
                }
                if(mMediaPlayer != null){
                    mMediaPlayer.stop();
                }
                mHandler = null;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
