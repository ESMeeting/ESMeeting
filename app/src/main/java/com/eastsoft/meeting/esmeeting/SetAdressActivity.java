package com.eastsoft.meeting.esmeeting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Admin on 2015/11/17.
 */
public class SetAdressActivity extends AppCompatActivity {
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_input_copy);
      //  Toolbar toolbar= (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(toolbar);
      //  toolbar.setTitle("东软载波智能会议室");

      //  final TextInputLayout adress= (TextInputLayout) findViewById(R.id.adress);
      //  adress.getEditText().addTextChangedListener(new TextWatcher() {
       //     @Override
       //     public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

       //     }

        //    @Override
       //     public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

         //   }

        //    @Override
         //   public void afterTextChanged(Editable editable) {

          //      String text=adress.getEditText().getText().toString().trim();
         //       if (text!=null&&!text.equals("")){
          //          adress.setErrorEnabled(false);
          //      }
         //   }
       // });

        editText=(EditText)findViewById(R.id.editText);
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ad=editText.getText().toString().trim();
                if (ad==null||ad.equals("")){
                    Toast.makeText(SetAdressActivity.this, "请输入会议室地址", Toast.LENGTH_SHORT).show();
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
