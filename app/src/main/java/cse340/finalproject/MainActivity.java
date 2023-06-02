package cse340.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

public class MainActivity extends AppCompatActivity implements SensorActivity.SensorListener {

    // TextView to display the current acceleration
    private TextView accelerationTextView;

    // TextView to display the resultant acceleration
    private TextView resultantAccelerationTextView;

    // Sensor object to manage sensor
    private SensorActivity sensorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        accelerationTextView = findViewById(R.id.accelerationTextView);
        resultantAccelerationTextView = findViewById(R.id.resultantAccTxt);

        sensorActivity = new SensorActivity(this, this);

        // Check if the linear acceleration sensor is available
        if (sensorActivity.linearAccelerationSensor == null) {
            accelerationTextView.setText(R.string.sensorUnavailable);
            resultantAccelerationTextView.setText(R.string.sensorUnavailable);
        }


        // Initialize the home button
        Button leftButton = findViewById(R.id.homeButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the timer screen button
        Button midButton = findViewById(R.id.timerScreenButton);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the history screen button
        Button rightButton = findViewById(R.id.historyButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorActivity.start();
    }

    @Override
    protected void onPause() {
        super.onPause();

        sensorActivity.stop();
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        // Handle acceleration changes
        // Update the TextViews with the acceleration values
        String accelerationText = getResources().getString(R.string.accInfo, x, y, z);
        accelerationTextView.setText(accelerationText);

        resultantAccelerationTextView.setText(getResources().getString(
                R.string.resultantAcc, String.format("%.4f",
                        sensorActivity.resultantAcceleration(x, y, z))));
    }
}