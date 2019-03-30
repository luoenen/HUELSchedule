package com.luosenen.huelschedule;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Button info;
    private String information;
    private TextView text;
    private String account,password;
    private ConnectHUEL huel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        account = intent.getStringExtra("account");
        password = intent.getStringExtra("passwd");
        init();
        try {
            connection();
            login();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void init(){
        text = findViewById(R.id.show);
        info = findViewById(R.id.information);
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    new Thread(new Runnable(){
                        @Override
                        public void run() {
                            try {
                                information = huel.getStudentInformaction();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }).start();

                text.setText(information);
            }
        });
    }

    public void connection() throws Exception {
        huel = new ConnectHUEL(account,password);
        huel.init();
    }

    public boolean login() throws Exception {
        return huel.beginLogin();
    }
}
