package com.mc.examcode;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    TextView tv_title;
    Button btn_long;
    Handler mHandler;
    int count=0;
    SensorManager sensorManager;
    Sensor sensor;
    File file;
    //file data
    String fileLocation= Environment.getExternalStorageDirectory() + File.separator +"AlphabetTraining"+File.separator+"TrainingFile";
    ArrayList<ArrayList<float[]>> trainingMap=new ArrayList<>();
    ArrayList<float[]> inputDataPoints;
    String alphabets[]={"A","A","A","A","A","B","B","B","B","B","C","C","C","C","C","D","D","D","D","D"};
    String alphabets1[]={"A","B","C","D"};
    String currentAlphabet="";
    float gravity[]={0.0f,0.0f,0.0f};
    float acc_x=0.0f,acc_y=0.0f,acc_z=0.0f;
    boolean buttonDown=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_long=(Button)findViewById(R.id.btn_long);
        tv_title=(TextView)findViewById(R.id.tv_training);
        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/AlphabetTraining");
        if (!folder.exists()) {
            folder.mkdir();
        }
        file=new File(folder,"TrainingFile");
        if(file.exists())
        {
            createDialog();
        }
        mHandler=new Handler();
        sensorManager  = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        sensor=sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this,sensor,SensorManager.SENSOR_DELAY_FASTEST);

        btn_long.setOnTouchListener(new View.OnTouchListener()  {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.d("Motion Event",motionEvent.toString());
                if(motionEvent.getAction()==MotionEvent.ACTION_DOWN)
                {
                    inputDataPoints=new ArrayList<>();
                    mHandler.post(runnable);
                }
                if(motionEvent.getAction()==MotionEvent.ACTION_UP)
                {
                    buttonDown=false;
                    mHandler.removeCallbacks(runnable);

                    Log.d("input data",""+inputDataPoints.size());
                    ArrayList<float[]> temp=new ArrayList<float[]>();
                    temp=inputDataPoints;
                    trainingMap.add(count,temp);
                    Log.d("Heaven",""+trainingMap.get(0).size());

                    if(count!=19)
                        tv_title.setText(alphabets[count+1]);

                    count++;
                    Log.d("Heaven",""+trainingMap.get(0).size());

                    if(count==20)
                    {
                        try {
                            FileOutputStream fos =
                                    new FileOutputStream(file);
                            ObjectOutputStream os = new ObjectOutputStream(fos);
                            os.writeObject(trainingMap);
                            os.close();
                        } catch(Exception ex) {
                            ex.printStackTrace();
                        }
                        //Log.d("Heaven",""+trainingMap.get(0).size());
                        Intent testActivityIntent=new Intent(MainActivity.this,TestingActivity.class);
                        startActivity(testActivityIntent);
                        Toast.makeText(MainActivity.this,"Data Collected "+trainingMap.size(),Toast.LENGTH_LONG).show();

                    }
                }

                return false;
            }
        });
        try{
            FileInputStream fis = new FileInputStream(fileLocation);
            ObjectInputStream ois = new ObjectInputStream(fis);

            List <List<float[]>> ds;

            ds = (List <List<float[]>>)ois.readObject();
            Log.d("File input",ds.size()+"");

            ois.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }

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

        Sensor mySensor=sensorEvent.sensor;
        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {

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
        if (mySensor.getType() == Sensor.TYPE_GYROSCOPE) {

            final float alpha = 0.8f;
            // Isolate the force of gravity with the low-pass filter.
//            gravity[0] = alpha * gravity[0] + (1 - alpha) * sensorEvent.values[0];
//            gravity[1] = alpha * gravity[1] + (1 - alpha) * sensorEvent.values[1];
//            gravity[2] = alpha * gravity[2] + (1 - alpha) * sensorEvent.values[2];
//
//            acc_x=sensorEvent.values[0]-gravity[0];
//            acc_y=sensorEvent.values[1]-gravity[1];
//            acc_z= sensorEvent.values[2]-gravity[2];
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
    public void createDialog()
    {
        AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
        dialog.setCancelable(false);
        dialog.setTitle("Training File found!");
        dialog.setMessage("There exists a training file on the device. Do you want ot continue with the file or take new training samples?" );
        dialog.setPositiveButton("Continue", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                Intent testActivityIntent=new Intent(MainActivity.this,TestingActivity.class);
                startActivity(testActivityIntent);
                dialog.cancel();
            }
        })
                .setNegativeButton("New Samples ", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert = dialog.create();
        alert.show();
    }
}
