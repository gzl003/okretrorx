package com.lzg.okretrorx.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lzg.okretrorx.CacheManager;
import com.lzg.okretrorx.R;
import com.lzg.okretrorx.http.ApiHelper;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity {

    @BindView(R.id.phone)
    EditText phone;
    @BindView(R.id.password)
    EditText password;
    @BindView(R.id.login)
    Button login;
    private String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @OnClick(R.id.login)
    public void login(View view) {
//        if(phone.getText().toString().trim().isEmpty()){
//            return;
//        }
//        if(password.getText().toString().trim().isEmpty()){
//            return;
//        }
//        ApiHelper.api().systemLogin(phone.getText().toString().trim(),password.getText().toString().trim())
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Subscriber<UserInfo>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//                    }
//
//                    @Override
//                    public void onNext(UserInfo result) {
//
//                    }
//                });
        ApiHelper.api().getList().enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                CacheManager.getInstance(mContext).addCache(response.body(), call.request().url().url().toString());
                url = call.request().url().url().toString();
                CacheManager.getInstance(mContext).getCacheByUrl(url);
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {

            }
        });

        Toast.makeText(mContext, "login", Toast.LENGTH_SHORT).show();

    }
}
