package com.example.quizapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.Navigation;

import android.os.Bundle;

import com.example.quizapp.Views.ListFragment;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Check if the 'list' fragment extra is present
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("fragment")) {
            String fragmentName = extras.getString("fragment");
            // Navigate to the 'list' fragment using the navigation controller
            if (fragmentName != null && fragmentName.equals("list")) {
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.listFragment);
            }
        }
    }
}