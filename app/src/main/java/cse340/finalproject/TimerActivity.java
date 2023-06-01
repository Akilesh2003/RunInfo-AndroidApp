package cse340.finalproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class TimerActivity extends AppCompatActivity implements SensorEventListener {

    public long timeMs;

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;
    private TextView accelerationTextView;

    private double sumAcceleration;
    private int accelerationCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen2);

        accelerationTextView = findViewById(R.id.accDetails);

        // Initialize the sensor manager
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Check if the linear acceleration sensor is available
        if (sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // Get a reference to the linear acceleration sensor
            linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        } else {
            // Linear acceleration sensor is not available on this device
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
                                        // Update the timer display (assuming you have a TextView with id timerTextView)
                                        TextView timerTextView = findViewById(R.id.timerTextView);
                                        timeMs = elapsedTime;
                                        timerTextView.setText(formatTime(elapsedTime));

                                        TextView accelerationTextView = findViewById(R.id.accDetails);
                                        accelerationTextView.setText("Average Acceleration: " +
                                                String.format("%.4f", averageAcceleration) + " m/s^2");

                                        TextView averageVelocityTextView = findViewById(R.id.avgVelTxt);
                                        averageVelocityTextView.setText("Average Velocity: " +
                                                String.format("%.4f", averageVelocity) +
                                                " m/s");

                                        TextView distanceTextView = findViewById(R.id.totalDistanceTxt);
                                        distanceTextView.setText("Distance Equivalent: " +
                                                String.format("%.4f", distanceRuninMiles) +
                                                " miles");

                                    }
                                });

                                try {
                                    Thread.sleep(10); // Delay between updates (adjust as needed)
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
                    timerButton.setText("Start Timer"); // Update button text
                    saveRunHistory(timeMs, averageAcceleration, averageVelocity, distanceRuninMiles);

                }
            }
        });

        Button leftButton = findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button midButton = findViewById(R.id.listMode);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TimerActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        Button rightButton = findViewById(R.id.right);
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
        if (linearAccelerationSensor != null) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the sensor listener to save battery
        if (linearAccelerationSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // Get the acceleration values
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];

            double resultantAcc = resultantAcceleration(x, y, z);

            // Update the TextView with the acceleration values
//            String accelerationText = "X: " + x + "\nY: " + y + "\nZ: " + z;
//            accelerationTextView.setText(accelerationText);
            sumAcceleration += resultantAcc;
            accelerationCount++;
        }
    }

    private double resultantAcceleration(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not implemented in this assignment.
    }

    // Helper method to format the elapsed time as HH:MM:SS
    private String formatTime(long elapsedTime) {
        long seconds = elapsedTime / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        return String.format("%02d:%02d:%02d", hours, minutes % 60, seconds % 60);
    }

    private void saveRunHistory(long timeMs, double averageAcceleration, double averageVelocity, double distanceRuninMiles) {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("RunHistory", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // Get the current timestamp
        String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date());

        // Create a string representing the run details
        String runDetails = "Time: " + formatTime(timeMs) +
                "\nAverage Acceleration: " + String.format("%.4f", averageAcceleration) + " m/s^2" +
                "\nAverage Velocity: " + String.format("%.4f", averageVelocity) + " m/s" +
                "\nDistance Equivalent: " + String.format("%.4f", distanceRuninMiles) + " miles";

        // Save the run details in shared preferences with the timestamp as the key
        editor.putString(timestamp, runDetails);
        editor.apply();
    }

}