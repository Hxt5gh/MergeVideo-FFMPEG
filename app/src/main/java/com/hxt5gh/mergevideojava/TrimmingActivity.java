package com.hxt5gh.mergevideojava;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding =  ActivityTrimmingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        onClick();
    }


    void init()
    {//setting up video
        videoView = new VideoView(this);
        mediaController = new MediaController(this);

        Uri videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
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