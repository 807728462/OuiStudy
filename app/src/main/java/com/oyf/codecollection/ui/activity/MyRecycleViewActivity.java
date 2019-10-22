package com.oyf.codecollection.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.oyf.basemodule.weight.recycleview.MyRecycleView;
import com.oyf.codecollection.R;

public class MyRecycleViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_recycle_view);
        MyRecycleView recycleView = findViewById(R.id.mrcv);
        recycleView.setAdapter(new MyRecycleView.Adapter() {
            @Override
            public View onCreateViewHodler(int position, View convertView, ViewGroup parent) {
                View inflate = LayoutInflater.from(MyRecycleViewActivity.this).inflate(R.layout.item_mrcv, null);

                return inflate;
            }

            @Override
            public View onBinderViewHodler(int position, View convertView, ViewGroup parent) {
                TextView textView = convertView.findViewById(R.id.tv_item);
                textView.setText("item="+position);
                return convertView;
            }

            @Override
            public int getItemViewType(int row) {
                return 0;
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public int getCount() {
                return 30;
            }

            @Override
            public int getHeight(int index) {
                return 100;
            }
        });
    }
}
