package com.oyf.codecollection;


import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;


import com.oyf.codecollection.ui.activity.BehaviorActivity;
import com.oyf.codecollection.ui.activity.VLayoutActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void gotoVlayout(View view){
        startActivity(new Intent(this, VLayoutActivity.class));
    }
    public void gotoBehavior(View view){
        startActivity(new Intent(this, BehaviorActivity.class));
    }
}
