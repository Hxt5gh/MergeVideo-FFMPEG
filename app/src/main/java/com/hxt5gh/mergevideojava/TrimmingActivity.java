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
import com.gowtham.library.utils.TrimVideo;

import java.io.IOException;

public class TrimmingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trimming);

        //This is for getting the length of video that has to be trimmed.
        String videoPath = "/path/to/your/video.mp4"; // Path to your video file
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(videoPath);

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
                long startMillis = (long) (startPercent * videoDuration);
                long endMillis = (long) (endPercent * videoDuration);

                // Use start and end values for trimming
            }
        });
    }

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


}