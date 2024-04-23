package com.hxt5gh.mergevideojava;

import static android.Manifest.permission.READ_MEDIA_VIDEO;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.RawRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.hxt5gh.mergevideojava.databinding.ActivityMainBinding;
import com.hxt5gh.mergevideojava.databinding.ActivityTrimmingBinding;

import java.util.List;

public class TrimmingActivity extends AppCompatActivity {

    private ActivityTrimmingBinding binding;
    private VideoView videoView;
    private static Uri videoUri;
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
        videoUri =   Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
        videoView = new VideoView(this);
        mediaController = new MediaController(this);


        binding.videoView.setVideoURI(videoUri);
        binding.videoView.start();

        binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.videoView.seekTo(0);
                binding.videoView.start();
            }
        });

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
                // openDrawable();

                //for now take video from gallery
                getPermissions();

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

    //permission

    private void getPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(TrimmingActivity.this, READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED) {
                openMediaForVideo();
                Log.d("TAGD", "Permission granted for higher version than 13");
            } else {
                ActivityCompat.requestPermissions(TrimmingActivity.this, new String[]{
                        READ_MEDIA_VIDEO
                }, 111);
            }

        } else {
            if (ContextCompat.checkSelfPermission(TrimmingActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                openMediaForVideo();
                Log.d("TAGD", "Permission granted for lower version than 13");
            } else {
                ActivityCompat.requestPermissions(TrimmingActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (!Environment.isExternalStorageManager()) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", getApplicationContext().getPackageName())));
                    startActivityIfNeeded(intent, 112);
                } catch (Exception e) {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent, 112);
                }
            }

        }

}


    private void openMediaForVideo() {
        getMedia.launch("video/*");
    }

    private ActivityResultLauncher<String> getMedia = registerForActivityResult(new ActivityResultContracts.GetContent(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri uri) {
            String path = RealPathUtil.getRealPath(getApplicationContext() , uri);
            Log.d("debug", "onActivityResult: pathj " +path);
            videoUri = uri;
            binding.videoView.setVideoURI(uri);
            binding.videoView.start();
        }
    });



}