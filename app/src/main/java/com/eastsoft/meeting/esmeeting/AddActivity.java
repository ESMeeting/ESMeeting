package com.eastsoft.meeting.esmeeting;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by Admin on 2015/11/12.
 */
public class AddActivity extends AppCompatActivity {
    private TextInputLayout name, participant, adress;
    private TextView startTime, endTime;
    boolean isUpdata = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_meet);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        startTime = (TextView) findViewById(R.id.et_starttime);
        endTime = (TextView) findViewById(R.id.et_endtime);
        name = (TextInputLayout) findViewById(R.id.et_name);
        participant = (TextInputLayout) findViewById(R.id.et_participant);
        adress = (TextInputLayout) findViewById(R.id.et_adress);
        final View timeLy = findViewById(R.id.data_ly);
        final DatePicker datePicker = (DatePicker) findViewById(R.id.datePicker);
        datePicker.setCalendarViewShown(false);
        final TimePicker timePicker = (TimePicker) findViewById(R.id.timePicker);

        if (getIntent().getStringExtra("meet") != null && !getIntent().getStringExtra("meet").equals("")) {

            MeetInfo meetInfo = new Gson().fromJson(getIntent().getStringExtra("meet"), MeetInfo.class);
            startTime.setText(meetInfo.getStartTime());
            endTime.setText(meetInfo.getEndTime());

            name.getEditText().setText(meetInfo.getName());
            adress.getEditText().setText(meetInfo.getAddress());
            participant.getEditText().setText(meetInfo.getParticipant());
            isUpdata = true;

        } else {
            isUpdata = false;
            Date date = new Date();
            SimpleDateFormat dateFormat = new SimpleDateFormat("yy-MM-dd HH:mm");
           // SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
            startTime.setText(dateFormat.format(date));
            endTime.setText(dateFormat.format(date));
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("GMT"));
        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        datePicker.init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker datePicker, int y, int m, int d) {

//                startTime.getEditText().setText();
//                startT=y+"年"+m+"月"+d
            }
        });
        findViewById(R.id.ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMddHHmm");
                SimpleDateFormat myFmt2 = new SimpleDateFormat("yy-MM-dd HH:mm");
                int month = datePicker.getMonth() + 1;
                if (datePicker.getTag().toString().equals("start")) {
                    try {
                        startTime.setText(myFmt2.format(myFmt.parse(String.valueOf(datePicker.getYear()) + String.format("%02d", month) + String.format("%02d", datePicker.getDayOfMonth())
                                + String.format("%02d", timePicker.getCurrentHour()) + String.format("%02d", timePicker.getCurrentMinute()))).toString());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        endTime.setText(myFmt2.format(myFmt.parse(String.valueOf(datePicker.getYear()) + String.format("%02d", month) + String.format("%02d", datePicker.getDayOfMonth())
                                + String.format("%02d", timePicker.getCurrentHour()) + String.format("%02d", timePicker.getCurrentMinute()))).toString());

                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                }
                timeLy.setVisibility(View.GONE);

            }
        });
        findViewById(R.id.cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                timeLy.setVisibility(View.INVISIBLE);

            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setTag("start");
                timeLy.setVisibility(View.VISIBLE);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePicker.setTag("end");
                timeLy.setVisibility(View.VISIBLE);
            }
        });

        name.getEditText().addTextChangedListener(new TextWatcherMy(name));
        adress.getEditText().addTextChangedListener(new TextWatcherMy(adress));
        participant.getEditText().addTextChangedListener(new TextWatcherMy(participant));

    }

    class TextWatcherMy implements TextWatcher {
        TextInputLayout textInputLayout;

        public TextWatcherMy(TextInputLayout textInputLayout) {
            this.textInputLayout = textInputLayout;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {

            if (textInputLayout.getEditText().getText().toString().trim().length() != 0) {
                textInputLayout.setErrorEnabled(false);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_meet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_ok) {
            if (checkInput()) {
                if (isUpdata) {
                    MeetInfo meetInfo1 = new Gson().fromJson(getIntent().getStringExtra("meet"), MeetInfo.class);
                    meetInfo1.setValue("startTime",startTime.getText().toString());
                    meetInfo1.setValue("endTime", endTime.getText().toString());
                    meetInfo1.setValue("name", name.getEditText().getText().toString());
                    meetInfo1.setValue("participant", participant.getEditText().getText().toString());
                    meetInfo1.setValue("day", startTime.getText().toString().substring(0, 8));
                    meetInfo1.setValue("address", adress.getEditText().getText().toString());

                    meetInfo1.update(AddActivity.this, meetInfo1.getObjectId(), new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AddActivity.this, "成功", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(AddActivity.this, "失败,请重试", Toast.LENGTH_LONG).show();

                        }
                    });

                } else {
                    MeetInfo meetInfo = new MeetInfo();
                    meetInfo.setStartTime(startTime.getText().toString());
                    meetInfo.setEndTime(endTime.getText().toString());
                    meetInfo.setName(name.getEditText().getText().toString());
                    meetInfo.setParticipant(participant.getEditText().getText().toString());
                    meetInfo.setDay(startTime.getText().toString().substring(0, 8));
                    meetInfo.setAddress(adress.getEditText().getText().toString());
                    meetInfo.save(AddActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            Toast.makeText(AddActivity.this, "成功", Toast.LENGTH_LONG).show();
                            finish();
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            Toast.makeText(AddActivity.this, "失败,请重试", Toast.LENGTH_LONG).show();

                        }
                    });
                }
                return true;
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkInput() {

        if (name.getEditText().getText().toString() == null || name.getEditText().getText().toString().equals("")) {
            name.setError("请输入会议名称");
            return false;
        }
        if (adress.getEditText().getText().toString() == null || adress.getEditText().getText().toString().equals("")) {
            adress.setError("请输入会议地址");
            return false;
        }
        if (participant.getEditText().getText().toString() == null || participant.getEditText().getText().toString().equals("")) {
            participant.setError("请输入参会人员");
            return false;
        }
        return true;
    }
}
