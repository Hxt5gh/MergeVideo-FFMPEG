package com.hxt5gh.mergevideojava;


import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.google.android.material.slider.RangeSlider;

import java.io.IOException;

import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;


import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.hxt5gh.mergevideojava.databinding.ActivityMainBinding;
import com.hxt5gh.mergevideojava.databinding.ActivityTrimmingBinding;


public class TrimmingActivity extends AppCompatActivity {

    private ActivityTrimmingBinding binding;
    private VideoView videoView;
    private Uri videoUri = Uri.parse("android.resource://com.hxt5gh.mergevideojava/raw/raw");
    private MediaController mediaController;

    private static long startMillis;
    private static long endMillis;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityTrimmingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        onClick();
    }


    /*
    //This using library of com.gowtham.library.utils.TrimVideo
    public ActivityResultLauncher<Intent> startForResult = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK &&
                        result.getData() != null) {
                    Uri uri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
                    Log.d("TAG", "Trimmed path:: " + uri);

                } else {
//                    LogMessage.v("videoTrimResultLauncher data is null");
                }
            });

     */


    void init()
    {//setting up video

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
        videoView = new VideoView(this);
        mediaController = new MediaController(this);

        /*
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(this ,videoUri);

        String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
        long videoDuration = Long.parseLong(durationStr);

        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //this is for getting the starting and ending time form the slider to trim the video.
        RangeSlider rangeSlider = findViewById(R.id.rangeSlider);
        rangeSlider.addOnChangeListener(new RangeSlider.OnChangeListener() {
            @Override
            public void onValueChange(RangeSlider slider, float value, boolean fromUser) {
                float startPercent = slider.getValues().get(0);
                float endPercent = slider.getValues().get(1);

                //we can use this timing for trimming the video by FFMPEG library.
                startMillis = (long) (startPercent * videoDuration);
                endMillis = (long) (endPercent * videoDuration);
                Log.d("debug", "onValueChange: start -> "+startMillis  +" end -> " +endMillis);
            }
        });
         */


        binding.videoView.setVideoURI(videoUri);
        binding.videoView.start();


    }
    void onClick()
    {
        binding.cameraId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TrimmingActivity.this, "Camera Open", Toast.LENGTH_SHORT).show();
            }
        });
        binding.addItemId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TrimmingActivity.this, "Studio Open", Toast.LENGTH_SHORT).show();
                //openDrawable
                openDrawable();
            }
        });

        binding.trimId.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(TrimmingActivity.this, "Trimmed Clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    String openDrawable()
    {

        return "video uri";
    }

}