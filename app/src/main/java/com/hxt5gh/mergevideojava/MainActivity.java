package com.hxt5gh.mergevideojava;

import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_CANCEL;
import static com.arthenica.mobileffmpeg.Config.RETURN_CODE_SUCCESS;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.arthenica.mobileffmpeg.BuildConfig;
import com.arthenica.mobileffmpeg.Config;
import com.arthenica.mobileffmpeg.ExecuteCallback;
import com.arthenica.mobileffmpeg.FFmpeg;
import com.arthenica.mobileffmpeg.LogCallback;
import com.arthenica.mobileffmpeg.LogMessage;
import com.arthenica.mobileffmpeg.Statistics;
import com.arthenica.mobileffmpeg.StatisticsCallback;
import com.hxt5gh.mergevideojava.databinding.ActivityMainBinding;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private static final int REQUEST_READ_WRITE_STORAGE = 123;
    private ArrayList<String>  videoPath;
    private StringBuilder dynamicPath;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        videoPath = new ArrayList<String>();
        dynamicPath = new StringBuilder();
        binding.textView.setText("Select Video");


        binding.idgetMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    getPermissions();
//                    ActivityCompat.requestPermissions(MainActivity.this,new  String[] {Manifest.permission.READ_EXTERNAL_STORAGE} , 111);

            }
        });

        binding.idMergeMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (videoPath.isEmpty() || videoPath.size() < 2)
                {
                    Toast.makeText(MainActivity.this, "Select Video", Toast.LENGTH_SHORT).show();
                    return;
                }

                    if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q)
                        ActivityCompat.requestPermissions(MainActivity.this,new  String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE} , 111);

                    for (String s : videoPath) {
                        dynamicPath.append(" -i ");
                        dynamicPath.append(s);
                    }
                    dynamicPath.append(" -filter_complex ");
                    dynamicPath.append(" \"[0:v][1:v]concat=");
                    dynamicPath.append("n=");
                    dynamicPath.append(videoPath.size());
                    dynamicPath.append(":v=1:a=0[outv]\" ");
                    dynamicPath.append(" -map ");
                    dynamicPath.append(" \"[outv]\" -y ");
                    String random = " /storage/emulated/0/Download/" + generateNonce();
                    String path = destLocation(generateNonce() + "");
                    dynamicPath.append(path);


                    Log.d("path", "onClick: " + dynamicPath);
                    // String cmd = "-i /storage/emulated/0/Download/one.mp4 -i /storage/emulated/0/Download/two.mp4 -filter_complex \"[0:v][1:v]concat=n=2:v=1:a=0[outv]\" -map \"[outv]\" /storage/emulated/0/Download/outputNew.mp4";
                    runFfmpeg(dynamicPath.toString());





            }
        });
    }

    private void getPermissions() {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
            if(ContextCompat.checkSelfPermission(MainActivity.this, READ_MEDIA_VIDEO) == PackageManager.PERMISSION_GRANTED){
                openMediaForVideo();
                Log.d("TAGD", "Permission granted for higher version than 13");
            }else{
                ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                        READ_MEDIA_VIDEO
                }, 111);
            }

        }else{
            if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
                openMediaForVideo();
                Log.d("TAGD", "Permission granted for lower version than 13");
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 111);

            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R ) {
            if (!Environment.isExternalStorageManager()){
                try {
                        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                        intent.addCategory("android.intent.category.DEFAULT");
                        intent.setData(Uri.parse(String.format("package:%s",getApplicationContext().getPackageName())));
                        startActivityIfNeeded(intent , 112);
                }catch (Exception e){
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    startActivityIfNeeded(intent , 112);
                }
            }

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 111) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openMediaForVideo();
            } else {
               getPermissions();
            }
        }
    }

    void showToast(String v) {
        Toast.makeText(this, v, Toast.LENGTH_SHORT).show();
    }


    private ActivityResultLauncher<String> getMedia = registerForActivityResult(new ActivityResultContracts.GetMultipleContents(), new ActivityResultCallback<List<Uri>>() {
        @Override
        public void onActivityResult(List<Uri> uriList) {
            String uriString = "";
            for (Uri uri : uriList) {
                String path = RealPathUtil.getRealPath(getApplicationContext() , uri);
                videoPath.add(path);
                uriString  +=path +" , \n ";
            }
            binding.textView.setText(uriString);
        }
    });

    private void openMediaForVideo() {
        getMedia.launch("video/*");
    }

    private  void runFfmpeg(String command) {
        binding.idLogs.setText("Processing...");
        Log.d("debug", "runFfmpeg: Command "+command);
        long executionId = FFmpeg.executeAsync(command, new ExecuteCallback() {

            @Override
            public void apply(final long executionId, final int returnCode) {
                binding.idLogs.setText("Don't Exit The APP");
                if (returnCode == RETURN_CODE_SUCCESS) {
                    binding.idLogs.setText("Merged");
                    Log.i(Config.TAG, "Async command execution completed successfully.");
                    Toast.makeText(MainActivity.this, "Video Merge Successfully", Toast.LENGTH_SHORT).show();
                } else if (returnCode == RETURN_CODE_CANCEL) {
                    binding.idLogs.setText("Cancel");
                    Log.i(Config.TAG, "Async command execution cancelled by user.");
                } else {
                    binding.idLogs.setText("Execution Failed");
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


    private String destLocation(String fileName)
    {

        File file = new File(Environment.getExternalStorageDirectory() +"/Trimmed");
        if (!file.exists()){
            file.mkdir();
        }
        String filePath = file.getAbsolutePath() + "/" + fileName + ".mp4";
        return new File(filePath).getAbsolutePath();
    }



    public  long generateNonce() {
        SecureRandom random = new SecureRandom();
        long nonce = random.nextLong();

        // Ensure the nonce is a 24-digit long positive integer
        nonce = Math.abs(nonce);
        nonce = (long) (nonce % (Math.pow(10, 24)));

        // If the nonce is less than 24 digits, pad with zeros
        String nonceStr = String.format("%024d", nonce);
        nonce = Long.parseLong(nonceStr);

        return nonce;
    }
}





