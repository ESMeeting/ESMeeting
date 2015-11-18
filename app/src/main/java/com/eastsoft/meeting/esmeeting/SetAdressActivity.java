package com.eastsoft.meeting.esmeeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

/**
 * Created by Admin on 2015/11/17.
 */
public class SetAdressActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_adress_input);
        Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("东软载波智能会议室");

        final TextInputLayout adress= (TextInputLayout) findViewById(R.id.adress);
        adress.getEditText().addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {

                String text=adress.getEditText().getText().toString().trim();
                if (text!=null&&!text.equals("")){
                    adress.setErrorEnabled(false);
                }
            }
        });

        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ad=adress.getEditText().getText().toString().trim();
                if (ad==null||ad.equals("")){
                    adress.setErrorEnabled(true);
                    adress.setError("请输入会议地址");
                    return;
                }
                Intent intent=new Intent(SetAdressActivity.this,MainActivity.class);
                intent.putExtra(Util.ADDRESS,ad);
                startActivity(intent);
                finish();
            }
        });
    }
}
