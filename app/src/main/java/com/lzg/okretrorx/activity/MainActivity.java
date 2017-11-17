package com.lzg.okretrorx.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.lzg.okretrorx.R;
import com.lzg.okretrorx.bean.UserInfo;
import com.lzg.okretrorx.http.ApiHelper;

import butterknife.BindView;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @OnClick(R.id.login)
    public void login(){
        if(phone.getText().toString().trim().isEmpty()){
            return;
        }
        if(password.getText().toString().trim().isEmpty()){
            return;
        }
        ApiHelper.api().systemLogin(phone.getText().toString().trim(),password.getText().toString().trim())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<UserInfo>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onNext(UserInfo result) {

                    }
                });

    }
}
