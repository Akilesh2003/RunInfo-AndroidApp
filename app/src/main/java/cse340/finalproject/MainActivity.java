package cse340.finalproject;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class MainActivity extends AppCompatActivity {
    public static final FrameLayout.LayoutParams PARAMS = new FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Button leftButton = findViewById(R.id.left);
        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Button midButton = findViewById(R.id.listMode);
        midButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TimerActivity.class);
                startActivity(intent);
            }
        });

        Button rightButton = findViewById(R.id.right);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HistoryActivity.class);
                startActivity(intent);
            }
        });





//        ViewGroup contents = findViewById(R.id.tab_contents); // Our primary view container
//        TabView nav = findViewById(R.id.bottom_nav); // Tabs at the bottom of screen.
//        contents.setBackgroundColor(Color.WHITE);
//
//        // Get the image margin from resources, in pixels (converted from dp)
//        int imageMargin = getResources().getDimensionPixelSize(R.dimen.image_margin);
//
//        // Register callback on item selected. This is called each time a tab is pressed.
//        nav.setOnNavigationItemSelectedListener(item -> {
//            contents.removeAllViews(); // Clear the currently displayed view.
//
//            // Set tab contents based on selected item.
//            switch (item.getItemId()) {
//                case R.id.screen1:
//                    contents.addView(getLayoutInflater().inflate(R.layout.activity_main, null), PARAMS);
//                    return true;
//                case R.id.screen2:
//                    contents.addView(getLayoutInflater().inflate(R.layout.screen2, null), PARAMS);
//                    return true;
//                case R.id.screen3:
//                    contents.addView(getLayoutInflater().inflate(R.layout.screen2, null), PARAMS);
//                    return true;
//                default:
//                    Log.e("CSE340","Unrecognized nav item selected: " + item.getTitle());
//            }
//            return false;
//        });
//
//        nav.setSelectedItemId(R.id.screen1);

    }
}