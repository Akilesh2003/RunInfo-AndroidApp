package cse340.finalproject;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class SensorActivity implements SensorEventListener {
    public SensorManager sensorManager;
    public Sensor linearAccelerationSensor;
    public SensorListener listener;

    public SensorActivity(Context context, SensorListener listener) {
        this.listener = listener;

        // Initialize the sensor manager
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);

        // Check if the linear acceleration sensor is available
        if (sensorManager != null && sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION) != null) {
            // Get a reference to the linear acceleration sensor
            linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
        }
    }

    public void start() {
        if (linearAccelerationSensor != null) {
            sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    public void stop() {
        if (linearAccelerationSensor != null) {
            sensorManager.unregisterListener(this);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            listener.onAccelerationChanged(event.values[0], event.values[1], event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Not implemented in this example
    }

    public interface SensorListener {
        void onAccelerationChanged(float x, float y, float z);
    }

    public double resultantAcceleration(float x, float y, float z) {
        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2) + Math.pow(z, 2));
    }
}

