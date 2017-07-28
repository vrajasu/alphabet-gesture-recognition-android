package com.mc.examcode;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestingActivity extends AppCompatActivity implements SensorEventListener{
    TextView tv_predicted;
    Button btn_prediction;
    ProgressBar pbar;
    String fileLocation= Environment.getExternalStorageDirectory() + File.separator +"AlphabetTraining"+File.separator+"TrainingFile";
    ArrayList<ArrayList<float[]>> trainingData= new ArrayList<>();

    SensorManager sensorManager;
    Sensor sensor;
    float acc_x=0.0f,acc_y=0.0f,acc_z=0.0f;
    float[] gravity={0.0f,0.0f,0.0f};
    ArrayList<float[]> inputDataPoints=new ArrayList<>();
    Handler mHandler;
    boolean buttonDown=false;
    float thresholdDistance=120;
    String[] alphabets={"A","B","C","D"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testing);

        tv_predicted=(TextView)findViewById(R.id.tv_predicted);
        pbar=(ProgressBar)findViewById(R.id.pbar_predicting);
        btn_prediction=(Button)findViewById(R.id.btn_test);
        sensorManager  = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);

        try{
            FileInputStream fis = new FileInputStream(fileLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);

            trainingData = (ArrayList<ArrayList<float[]>>)ois.readObject();
            Log.d("File input",trainingData.size()+"");
            ois.close();

            Toast.makeText(TestingActivity.this,"Training Data Retrieved",Toast.LENGTH_LONG).show();
        }catch (Exception e)
        {
            Toast.makeText(TestingActivity.this,"Training Data Retrieval Failed",Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
        mHandler=new Handler();
        btn_prediction.setOnTouchListener(new View.OnTouchListener()  {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {
                    inputDataPoints.clear();
                    tv_predicted.setVisibility(View.INVISIBLE);
                    buttonDown=true;
//                    mHandler.post(runnable);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
//                    inputDataPoints.clear();
                    buttonDown=false;
//                    mHandler.removeCallbacks(runnable);
                    predict();
                }

                return false;
            }
        });

    }
    Runnable runnable=new Runnable() {
        @Override
        public void run() {
            buttonDown=true;
            mHandler.post(runnable);
        }
    };

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor sense=sensorEvent.sensor;
        if(sense.getType()==Sensor.TYPE_ACCELEROMETER)
        {
            final float alpha = 0.8f;

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];

            acc_x=sensorEvent.values[0]-gravity[0];
            acc_y=sensorEvent.values[1]-gravity[1];
            acc_z= sensorEvent.values[2]-gravity[2];
            if(buttonDown)
            {
                float[] point=new float[2];
                point[0]=acc_x;
                point[1]=acc_x;
                inputDataPoints.add(point);
            }
        }
        if(sense.getType()==Sensor.TYPE_GYROSCOPE)
        {
            if(buttonDown)
            {
                float[] point=new float[2];
                point[0]=sensorEvent.values[0];
                point[1]=sensorEvent.values[0];
                inputDataPoints.add(point);
            }
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    public void predict()
    {
        int trainingDataSize=trainingData.size();
        float[] dtwValues=new float[trainingDataSize];
        DTW dtw;
        for(int i=0;i<trainingDataSize;i++)
        {
            dtw=new DTW(inputDataPoints,trainingData.get(i));
            dtwValues[i]=dtw.calculateDTW();
        }
        for(int i=0;i<trainingDataSize;i++)
        {
            Log.d("DTW Distance",""+dtwValues[i]);
        }
        float vote[]={0,0,0,0};
        int trainingSamples=trainingDataSize/4;
        for(int i=0;i<trainingDataSize;i++)
        {
            vote[i/trainingSamples]=vote[i/trainingSamples]+dtwValues[i];
        }
        for(int i=0;i<4;i++)
        {
            Log.d("Vote Distance",""+vote[i]);
        }
        float min=vote[0];
        int index=0;
        for(int i=0;i<4;i++)
        {
            if(vote[i]<min)
            {
                min=vote[i];
                index=i;
            }
        }
        tv_predicted.setText(alphabets[index]);
        tv_predicted.setVisibility(View.VISIBLE);
    }
}
