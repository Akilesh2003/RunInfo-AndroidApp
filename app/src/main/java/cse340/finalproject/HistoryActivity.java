package cse340.finalproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Map;

public class HistoryActivity extends AppCompatActivity {

    private LinearLayout runHistoryLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen3);

        runHistoryLayout = findViewById(R.id.runHistoryLayout);

        Button leftButton = findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button midButton = findViewById(R.id.listMode);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        Button rightButton = findViewById(R.id.right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });

        Button deleteAllButton = findViewById(R.id.deleteAllHistory);
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                SharedPreferences sharedPreferences = getSharedPreferences("RunHistory", MODE_PRIVATE);

                // Clear the shared preferences
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.apply();

                // Remove all history blocks from the layout
                runHistoryLayout.removeAllViews();
            }
        });

        // Load and display the run history
        loadRunHistory();
    }

    private void loadRunHistory() {
        // Get the shared preferences instance
        SharedPreferences sharedPreferences = getSharedPreferences("RunHistory", MODE_PRIVATE);

        // Get all the saved timestamps (keys) in the shared preferences
        Map<String, ?> allEntries = sharedPreferences.getAll();

        // Iterate over the saved timestamps
        for (Map.Entry<String, ?> entry : allEntries.entrySet()) {
            String timestamp = entry.getKey();
            String runDetails = entry.getValue().toString();

            // Create a TextView to display the run details
            TextView textView = new TextView(this);
            textView.setText("Timestamp: " + timestamp + "\n" + runDetails);
            textView.setTextSize(16);



            // Set margins to create space between each history block
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int margin = getResources().getDimensionPixelSize(R.dimen.history_block_margin);
            layoutParams.setMargins(0, 0, 0, margin);
            textView.setLayoutParams(layoutParams);


            // Add the TextView to the runHistoryLayout
            runHistoryLayout.addView(textView);
        }
    }
}