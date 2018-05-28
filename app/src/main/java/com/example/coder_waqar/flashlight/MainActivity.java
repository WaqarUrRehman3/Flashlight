package com.example.coder_waqar.flashlight;


import android.app.Activity;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;
import android.os.Bundle;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends Activity implements SensorEventListener {

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private static final int SHAKE_THRESHOLD = 2000;
    int i = 0;
    Camera mCam;
    SurfaceTexture mPreviewTexture;
    Camera.Parameters p;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        senSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);


        mCam = Camera.open();
        p = mCam.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        mCam.setParameters(p);
        mPreviewTexture = new SurfaceTexture(0);
        try {
            mCam.setPreviewTexture(mPreviewTexture);
        } catch (IOException ex) {
            // Ignore
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        {
            Sensor mySensor = event.sensor;


            if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                long curTime = System.currentTimeMillis();

                if ((curTime - lastUpdate) > 100) {

                    long diffTime = (curTime - lastUpdate);
                    lastUpdate = curTime;

                    float speed = Math.abs(x + y + z - last_x - last_y - last_z) / diffTime * 10000;

                    if (speed > SHAKE_THRESHOLD) {




                        Toast.makeText(getBaseContext(), "shaking", Toast.LENGTH_SHORT).show();

                        Vibrator v = (Vibrator) getApplicationContext().getSystemService(VIBRATOR_SERVICE);
                        v.vibrate(1000);




                        if (i == 1) {
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                            mCam.setParameters(p);
                            mCam.stopPreview();

                            i = 0;
                        } else if (i == 0) {
                            p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                            mCam.setParameters(p);


                            mCam.startPreview();
                            i = 1;
                        }
                    }

                    last_x = x;
                    last_y = y;
                    last_z = z;
                }

            }



        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}