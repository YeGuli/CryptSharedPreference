package com.ye_guli.cryptsharedpreference;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.ye_guli.cryptsplib.SharedPreferencesHandler;

import java.io.IOException;

/**
 * Created by Ye_Guli on 2017/11/08.
 * <p>
 * MainActivity
 */
public class MainActivity extends AppCompatActivity {
    private SharedPreferencesHandler handler;

    private EditText etUsr;
    private EditText etPsw;

    private Button btSave;
    private Button btGet;

    private TextView tvDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initData();
    }

    private void initData() {
        handler = SharedPreferencesHandler.getInstance();
        handler.initSecretKey(getApplicationContext(), "", false);
    }

    private void initView() {
        etUsr = findViewById(R.id.et_usr);
        etPsw = findViewById(R.id.et_psw);
        btSave = findViewById(R.id.bt_save);
        btGet = findViewById(R.id.bt_get);
        tvDisplay = findViewById(R.id.tv_display);

        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usr = etUsr.getText().toString();
                if (usr.trim().isEmpty()) {
                    etUsr.setError(getString(R.string.ac_main_et_empty_error));
                    etUsr.requestFocus();
                    return;
                }
                String psw = etPsw.getText().toString();
                if (psw.trim().isEmpty()) {
                    etPsw.setError(getString(R.string.ac_main_et_empty_error));
                    etPsw.requestFocus();
                    return;
                }
                if (handler != null) {
                    try {
                        handler.saveSetting(getApplicationContext(), new AccountSet(usr, psw));
                    } catch (IOException e) {
                        makeToast(e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });

        btGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (handler != null) {
                    try {
                        AccountSet set = handler.getSettings(getApplicationContext(), AccountSet.class);
                        tvDisplay.setText(set.toString());
                    } catch (InstantiationException e) {
                        makeToast(e.toString());
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        makeToast(e.toString());
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        makeToast(e.toString());
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void makeToast(String str) {
        Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
    }
}
