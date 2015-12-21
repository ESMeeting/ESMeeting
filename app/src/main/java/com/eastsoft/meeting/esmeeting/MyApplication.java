package com.eastsoft.meeting.esmeeting;

import android.app.Application;
import android.content.Context;

/**
 * Created by sofa on 2015/12/1.
 */
public class MyApplication extends Application {
    private Context context;

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        setContext(this);
    }
}
