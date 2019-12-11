package com.oyf.codecollection.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.Observable;

import com.oyf.codecollection.R;
import com.oyf.codecollection.bean.UserBean;
import com.oyf.codecollection.databinding.LayoutActivityDatabindingBindingImpl;

/**
 * @创建者 oyf
 * @创建时间 2019/12/2 10:08
 * @描述
 **/
public class DataBindingActivity extends AppCompatActivity {
    UserBean user;

    TextView tips;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutActivityDatabindingBindingImpl databindingBinding = DataBindingUtil.setContentView(this, R.layout.layout_activity_databinding);
        tips = findViewById(R.id.tv_tips);
        user = new UserBean("oyf", "账号", "123", "密码");

        databindingBinding.setUser(user);
        user.addOnPropertyChangedCallback(new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                Log.d("test", "-------------------------sender-" + sender.toString() + "-propertyId=" + propertyId);
                tips.setText("name=" + user.getUsername() + ",pwd=" + user.getPwd());
            }
        });
        databindingBinding.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "-------------------------username-" + user.getUsername() + "-pwd=" + user.getPwd());
                user.setUsername("更改后username");
                user.setPwd("更改后pwd");
                Log.d("test", "-------------------------username-" + user.getUsername() + "-pwd=" + user.getPwd());
            }
        });
    }
}
