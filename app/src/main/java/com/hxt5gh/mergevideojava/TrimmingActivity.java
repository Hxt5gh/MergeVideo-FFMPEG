package com.hxt5gh.mergevideojava;

import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaMetadataRetriever;
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

import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.hxt5gh.mergevideojava.databinding.ActivityTrimmingBinding;
import com.innovattic.rangeseekbar.RangeSeekBar;

import java.io.File;

public class TrimmingActivity extends AppCompatActivity {

    private ActivityTrimmingBinding binding;
    private VideoView videoView;
    private static Uri videoUri;
    private MediaController mediaController;

    private static Long startTrimMillis;
    private static Long endTrimMillis;

    private Long checkEnd;

    private StringBuilder dynamicPath = new StringBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTrimmingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
        onClick();

    }

    void init() {//setting up video
        videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.raw);
        videoView = new VideoView(this);
        mediaController = new MediaController(this);
        binding.videoView.setVideoURI(videoUri);
        binding.videoView.start();
        setupRangeSeekBar();//set seek bar min or max value


        binding.videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mediaPlayer) {
                binding.videoView.seekTo(0);
                binding.videoView.start();
            }
        });

    }

    void onClick() {
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
                if (startTrimMillis == null && endTrimMillis == null) {
                    Toast.makeText(TrimmingActivity.this, "Slide Trimmer", Toast.LENGTH_SHORT).show();
                    return;
                } else if (startTrimMillis.equals(0)) {
                    Toast.makeText(TrimmingActivity.this, "Slide Trimmer", Toast.LENGTH_SHORT).show();
                    return;
                } else if (endTrimMillis.equals(checkEnd)) {
                    Toast.makeText(TrimmingActivity.this, "Slide Trimmer", Toast.LENGTH_SHORT).show();
                    return;
                }
                Toast.makeText(TrimmingActivity.this, "Trimming from " + millisecondsToTime(startTrimMillis) + "-" + millisecondsToTime(endTrimMillis), Toast.LENGTH_SHORT).show();
                Log.d("debug", "onClick: " + millisecondsToTime(startTrimMillis) + " " + millisecondsToTime(endTrimMillis));
                endTrimMillis = endTrimMillis - startTrimMillis;
                trimVideo(startTrimMillis, startTrimMillis + endTrimMillis);
            }
        });
    }

    String openDrawable() {

        return "video uri";
    }

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
            String path = RealPathUtil.getRealPath(getApplicationContext(), uri);
            Log.d("debug", "onActivityResult: path " + path);
            videoUri = uri;
            binding.videoView.setVideoURI(uri);
            binding.videoView.start();

            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(TrimmingActivity.this, uri);
            String durationStr = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long videoDuration = Long.parseLong(durationStr);
            Log.d("debug", "onActivityResult: " + videoDuration);
            checkEnd = videoDuration;
            binding.rangeBarId.setMax((int) videoDuration);
            binding.rangeBarId.setMinRange(0);
        }
    });

    void setupRangeSeekBar() {
        binding.rangeBarId.setSeekBarChangeListener(new RangeSeekBar.SeekBarChangeListener() {
            @Override
            public void onStartedSeeking() {
                Log.d("debug", "onStartedSeeking: ");
            }

            @Override
            public void onStoppedSeeking() {
                Log.d("debug", "onStoppedSeeking: ");
                //startVideo From here
            }

            @Override
            public void onValueChanged(int i, int i1) {
                startTrimMillis = Long.parseLong(i + "");
                endTrimMillis = Long.parseLong(i1 + "");
                Log.d("debug", "start end trim: " + startTrimMillis + " " + endTrimMillis);
                //seek to current time
                binding.videoView.seekTo(i);
                binding.videoView.start();
            }
        });
    }

    void trimVideo(Long startTrimMillis, Long endTrimMillis) {
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
            ActivityCompat.requestPermissions(TrimmingActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 111);

        // ffmpeg -i input.mp4 -ss 00:05:20 -t 00:10:00 -c:v copy -c:a copy output1.mp4  //cut from 5:20 to plus add 10min
        // $ ffmpeg -i input.mp4 -ss 00:05:10 -to 00:15:30 -c:v copy -c:a copy output2.mp4 //cut for specific time

        dynamicPath.append(" -i ");
        dynamicPath.append(RealPathUtil.getRealPath(getApplicationContext(), videoUri));
        dynamicPath.append(" -ss ");
        dynamicPath.append(millisecondsToTime(startTrimMillis));
        dynamicPath.append(" -t");
        dynamicPath.append(millisecondsToTime(endTrimMillis));
        dynamicPath.append(" -c:v copy ");
        dynamicPath.append(" -c:a copy ");
        dynamicPath.append(" -y ");
        String path = destLocation(System.currentTimeMillis()); //save this trimmed path to database
        dynamicPath.append(path);

        Log.d("debug", "onClick: " + dynamicPath);

        runFfmpeg(dynamicPath.toString());

    }


    private void runFfmpeg(String command) {
        Log.d("debug", "runFfmpeg: Command " + command);
        long executionId = FFmpeg.executeAsync(command, new ExecuteCallback() {
            @Override
            public void apply(final long executionId, final int returnCode) {
                if (returnCode == RETURN_CODE_SUCCESS) {
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(TrimmingActivity.this, "Trimmed Successfully", Toast.LENGTH_SHORT).show();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    Log.i(Config.TAG, String.format("Async command execution failed with returnCode=%d.", returnCode));
                }
            }
        });
        Config.enableStatisticsCallback(new StatisticsCallback() {
            public void apply(Statistics newStatistics) {
                Log.d("status", String.format("frame: %d, time: %d", newStatistics.getVideoFrameNumber(), newStatistics.getTime()));
            }
        });

        Config.enableLogCallback(new LogCallback() {
            public void apply(LogMessage message) {
                Log.d("status2", message.getText());
            }
        });
    }

    private String destLocation(Long fileName) {

        File file = new File(Environment.getExternalStorageDirectory() + "/Trimmed");
        if (!file.exists()) {
            file.mkdir();
        }
        String filePath = file.getAbsolutePath() + "/" + fileName + ".mp4";
        return new File(filePath).getAbsolutePath();
    }

    public static String millisecondsToTime(long milliseconds) {
        long hours = milliseconds / 3600000;
        milliseconds = milliseconds % 3600000;

        long minutes = milliseconds / 60000;
        milliseconds = milliseconds % 60000;

        long seconds = milliseconds / 1000;

        return String.format(" %02d:%02d:%02d ", hours, minutes, seconds);
    }
}