package cse340.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TimerActivity extends AppCompatActivity implements SensorActivity.SensorListener {

    // Stores elapsed time in milliseconds
    public long timeMs;

    // TextView to display the current acceleration
    private TextView accelerationTextView;

    // Sum of acceleration values over the timer period
    private double sumAcceleration;

    // Number of times acceleration is recorded
    private int accelerationCount;

    // Sensor object to manage sensor
    private SensorActivity sensorActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);

        accelerationTextView = findViewById(R.id.accDetails);

        sensorActivity = new SensorActivity(this,this);

        // Check if the linear acceleration sensor is available
        if (sensorActivity.linearAccelerationSensor == null) {
            accelerationTextView.setText(R.string.sensorUnavailable);
        }

        Button timerButton = findViewById(R.id.timerButton);
        timerButton.setOnClickListener(new View.OnClickListener() {
            private boolean isTimerRunning = false;
            private long startTime;
            private long elapsedTime;

            double averageAcceleration;
            double averageVelocity;
            double distanceRuninMiles;

            @Override
            public void onClick(View v) {
                if (!isTimerRunning) {
                    // Start the timer
                    isTimerRunning = true;
                    startTime = System.currentTimeMillis();
                    timerButton.setText(R.string.stopTimer); // Update button text

                    sumAcceleration = 0;
                    accelerationCount = 0;

                    // Create a new thread to update the timer display
                    Thread timerThread = new Thread(new Runnable() {
                        @Override
                        public void run() {
                            while (isTimerRunning) {
                                // Calculate elapsed time
                                elapsedTime = System.currentTimeMillis() - startTime;

                                averageAcceleration = sumAcceleration/accelerationCount;
                                averageVelocity = 0.5 * averageAcceleration * elapsedTime/1000.0;
                                distanceRuninMiles = 0.5 * averageAcceleration *
                                        Math.pow(elapsedTime/1000.0, 2) * 0.000621371;

                                // Update the timer display on the UI thread
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        // Update the timer display
                                        TextView timerTextView = findViewById(R.id.timerTextView);
                                        timeMs = elapsedTime;
                                        timerTextView.setText(formatTime(elapsedTime));

                                        TextView runInfo = findViewById(
                                                R.id.accDetails);

                                        runInfo.setText(getResources().getString(R.string.runInfo,
                                                String.format("%.4f", averageAcceleration),
                                                String.format("%.4f", averageVelocity),
                                                String.format("%.4f", distanceRuninMiles)));

                                    }
                                });

                                try {
                                    Thread.sleep(10); // Delay between updates
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
                    timerThread.start();
                } else {
                    // Stop the timer
                    isTimerRunning = false;
                    timerButton.setText(R.string.startTimer); // Update button text
                    saveRunHistory(timeMs, averageAcceleration, averageVelocity, distanceRuninMiles);

                }
            }
        });

        // Initialize the home button
        Button leftButton = findViewById(R.id.homeButton);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the timer screen button
        Button midButton = findViewById(R.id.timerScreenButton);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        // Initialize the history screen button
        Button rightButton = findViewById(R.id.historyButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Register the sensor listener
        sensorActivity.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener
        sensorActivity.stop();
    }

    @Override
    public void onAccelerationChanged(float x, float y, float z) {
        // Handle acceleration changes
        sumAcceleration += sensorActivity.resultantAcceleration(x, y, z);
        accelerationCount++;
    }

    // Helper method to format the elapsed time as HH:MM:SS
    private String formatTime(long elapsedTime) {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    private void saveRunHistory(long timeMs, double averageAcceleration,
                                double averageVelocity, double distanceRuninMiles) {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("RunHistory",
                MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current timestamp
        String timestamp = new SimpleDateFormat(getString(R.string.dateFormat),
                Locale.getDefault()).format(new Date());

        // Create a string representing the run details
        String runDetails = getResources().getString(R.string.time, formatTime(timeMs)) +
                getResources().getString(R.string.runInfo,
                        String.format("%.4f", averageAcceleration),
                        String.format("%.4f", averageVelocity),
                        String.format("%.4f", distanceRuninMiles));

        // Save the run details in shared preferences with the timestamp as the key
        editor.putString(timestamp, runDetails);
        editor.apply();
    }

}