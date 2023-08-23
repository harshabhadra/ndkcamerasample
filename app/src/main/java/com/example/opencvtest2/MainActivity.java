package com.example.opencvtest2;

import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.opencv.core.Mat;

import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.os.Bundle;
import android.util.Log;
import android.util.Size;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.example.opencvtest2.databinding.ActivityMainBinding;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    static final String TAG = "MainActivity";

    private static final int PERMISSION_REQUEST_CAMERA = 1;

    public static native void startPreview(Surface surface);

    public static native void stopPreview();

    public static native void startExtraView(Surface surface);

    public static native void stopExtraView();

    private boolean isCameraOn = true;

    LayoutInflater extraViewLayoutInflater = null;

    boolean isBurstModeOn = false;


    static {
        System.loadLibrary("native-lib");
    }

    SurfaceView surfaceView;
    SurfaceHolder surfaceHolder;

    SurfaceView extraView;
    SurfaceHolder extraViewHolder;

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{android.Manifest.permission.CAMERA},
                    PERMISSION_REQUEST_CAMERA);
            return;
        }

        surfaceView = (SurfaceView) findViewById(R.id.surfaceview);
        surfaceHolder = surfaceView.getHolder();

        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : cameraManager.getCameraIdList()) {
                CameraCharacteristics characteristics = cameraManager.getCameraCharacteristics(
                        cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                float[] yourMinFocus = characteristics.get(
                        CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS);
                Objects.requireNonNull(yourMinFocus);

                Float yourMaxFocus = characteristics.get(
                        CameraCharacteristics.LENS_INFO_HYPERFOCAL_DISTANCE);
                Objects.requireNonNull(yourMaxFocus);

                Log.d(TAG, "onCreate: yourMaxFocus " + yourMaxFocus);

                for (float focus : yourMinFocus) {
                    Log.d(TAG, "onCreate: focus " + focus);
                }

                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }
                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                Objects.requireNonNull(map);

                Log.d(TAG, "bindPreview: " +
                        characteristics.get(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL));
                Size[] sizes = map.getOutputSizes(SurfaceTexture.class);
                for (Size size : sizes) {
                    Log.e(TAG, "onCreate: size " + size.getWidth() + "," + size.getHeight());
                }
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {

                Log.v(TAG, "surface created.");
                startPreview(holder.getSurface());
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                stopPreview();
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Log.v(TAG, "format=" + format + " w/h : (" + width + ", " + height + ")");
            }
        });


        extraViewLayoutInflater = LayoutInflater.from(getBaseContext());

        View view = extraViewLayoutInflater.inflate(R.layout.extraviewlayout, null);
        ViewGroup.LayoutParams layoutParamsControl
                = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        this.addContentView(view, layoutParamsControl);

//        extraView = (SurfaceView) findViewById(R.id.extraview);
//        extraView.setVisibility(View.INVISIBLE);
//
//        extraViewHolder = extraView.getHolder();
//        extraViewHolder.addCallback(new SurfaceHolder.Callback() {
//            @Override
//            public void surfaceCreated(SurfaceHolder holder) {
//                startExtraView(extraViewHolder.getSurface());
//
//            }
//
//            @Override
//            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//
//            }
//
//            @Override
//            public void surfaceDestroyed(SurfaceHolder holder) {
//                stopExtraView();
//            }
//        });

        surfaceView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isBurstModeOn = !isBurstModeOn;

                if (isBurstModeOn) {
                    extraView.setVisibility(View.VISIBLE);
                } else {
                    extraView.setVisibility(View.INVISIBLE);
                }
            }
        });
        binding.startStopCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isCameraOn = !isCameraOn;
                if(isCameraOn){
                    stopPreview();
                }
                else startPreview(surfaceHolder.getSurface());
            }
        });

    }
}