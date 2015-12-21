package com.eastsoft.meeting.esmeeting;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by sofa on 2015/11/30.
 */
public class RoomQuery {
    BmobQuery<MeetInfo> bmobQuery;
    public Context context;
    private static List<MeetInfo> roomInfo;

    public RoomQuery(Context context) {
        this.context = context;
        roomInfo = new ArrayList<MeetInfo>();
    }

    private void queryRoomInfo() {

        bmobQuery = new BmobQuery<MeetInfo>();
        bmobQuery.addQueryKeys("address");

        bmobQuery.findObjects(context, new FindListener<MeetInfo>() {
            @Override
            public void onSuccess(List<MeetInfo> list) {
                roomInfo = list;
            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(context, "查询失败", Toast.LENGTH_SHORT).show();
            }
        });


    }



}
