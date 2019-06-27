package com.sss.fills;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

public class ViewPhotoActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Button btnPause, btnQuit;
    private TextView txtState;

    private int stateMediaPlayer;
    private final int STATE_NOTSTARTER = 0;
    private final int STATE_PLAYING = 1;
    private final int STATE_PAUSING = 2;
    private final int STATEMP_ERROR = 3;
    private ImageView photo;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_photo);

        photo = (ImageView) findViewById(R.id.imageView2);
        try {
            Uri uri = Uri.parse("file:///" + Environment.getExternalStorageDirectory() + "/572/내그림/image_sample.jpg");
            photo.setImageURI(uri);
        }catch (Exception e){
            e.printStackTrace();
        }


        btnPause=(Button) findViewById(R.id.play);

        btnPause.setOnClickListener(buttonPlayPauseOnClickListener);

        initMediaPlayer();
    }
    private void initMediaPlayer() {
        String PATH_TO_FILE = Environment.getExternalStorageDirectory()
                + "/PCMRECORDER/record.aac";

        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(PATH_TO_FILE);
            mediaPlayer.prepare();
            Toast.makeText(this, PATH_TO_FILE, Toast.LENGTH_LONG).show();
            stateMediaPlayer = STATE_NOTSTARTER;
          //  txtState.setText("- IDLE -");
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = STATEMP_ERROR;
          //  txtState.setText("- 에러!!! -");
        } catch (IllegalStateException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = STATEMP_ERROR;
            //txtState.setText("- 에러!!! -");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            stateMediaPlayer = STATEMP_ERROR;
           // txtState.setText("- 에러!!! -");
        }
    }
    Button.OnClickListener buttonPlayPauseOnClickListener = new Button.OnClickListener() {

        public void onClick(View v) {
            switch (stateMediaPlayer) {
                case STATE_NOTSTARTER:
                    mediaPlayer.start();
                    btnPause.setText("Pause");
                   // txtState.setText("- 실행 -");
                    stateMediaPlayer = STATE_PLAYING;
                    break;
                case STATE_PLAYING:
                    mediaPlayer.pause();
                    btnPause.setText("Play");
                 //   txtState.setText("- 일시중지 -");
                    stateMediaPlayer = STATE_PAUSING;
                    break;
                case STATE_PAUSING:
                    mediaPlayer.start();
                    btnPause.setText("Pause");
                 //   txtState.setText("- 실행중 -");
                    stateMediaPlayer = STATE_PLAYING;
                    break;
            }
        }
    };
}
