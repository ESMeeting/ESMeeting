package com.eastsoft.meeting.esmeeting;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;
import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

public class MainActivity extends AppCompatActivity {

    public String APPID = "526ce376e1eaa62f903a7ea16b108922";
    private List<MeetInfo> meetInfoList = new LinkedList<>();
    private PullToRefreshListView mPullToRefreshView;
    private ILoadingLayout loadingLayout;
    private ListView listView;
    final List roomInfos  =new ArrayList();
    private DeviceListAdapter adapter;
    private String day;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 30;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private TextView tips, textViewFive;
    private Button textViewFour;
    private String adress;
    private Handler backHandler;
    private Runnable runnable;
    private static Spinner spinner;
    private BmobQuery<MeetInfo> bmobQuery;
    private Handler mainHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                queryData(0, STATE_REFRESH, day, adress);
                initTime();
            }

        }
    };
    private HandlerThread handlerThread;
    private TextView textYear;
    private TextView textWeek;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        //  Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //   setSupportActionBar(toolbar);

        Bmob.initialize(getApplicationContext(), APPID);
        initListView();
        adress = getIntent().getStringExtra(Util.ADDRESS);
        textViewFour = (Button) this.findViewById(R.id.textView4);
        //textViewFive = (TextView) this.findViewById(R.id.textView5);
        spinner = (Spinner) this.findViewById(R.id.spinner);
        textYear = (TextView) findViewById(R.id.textView);
        textWeek = (TextView) findViewById(R.id.textweek);
        initTime();
        initRoonNum();
        initRoomInfo();
        handlerThread = new HandlerThread("back_thread");
        handlerThread.start();
        backHandler = new Handler(handlerThread.getLooper());
        runnable = new Runnable() {
            @Override
            public void run() {
                mainHandler.sendEmptyMessage(1);
                backHandler.postDelayed(this, 30 * 1000);
            }
        };


    }

    private void initRoomInfo() {
        bmobQuery = new BmobQuery<MeetInfo>();
        bmobQuery.addQueryKeys("address");
        //List<BmobQuery<MeetInfo>> list=new ArrayList<BmobQuery<MeetInfo>>();
        //list.add(bmobQuery);
        bmobQuery.findObjects(this, new FindListener<MeetInfo>() {
            @Override
            public void onSuccess(List<MeetInfo> list) {
                String[] roomInfo = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    roomInfo[i] = list.get(i).getAddress();
                }
                Set<String> roomInfoSet = new TreeSet<String>();
                for (String roomInfos : roomInfo) {
                    roomInfoSet.add(roomInfos);
                }
                String[] roomInfoss=roomInfoSet.toArray(new String[0]);
                for (int i=0;i<roomInfoss.length;i++){
                    roomInfos.add(roomInfoss[i]);
                }
                final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, R.layout.room_info, roomInfos);
                arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
                spinner.setAdapter(arrayAdapter);
                Intent intent = MainActivity.this.getIntent();
                spinner.setSelection(arrayAdapter.getPosition(intent.getStringExtra(Util.ADDRESS)));
                spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                        refreshAddress();

                        adress = (String) spinner.getSelectedItem();
                        if (!roomInfos.contains(adress)){
                            Toast.makeText(MainActivity.this,"该房间已经删除,请点击重新登陆或点击别的房间刷新",Toast.LENGTH_SHORT).show();
                        }
                        queryData(0, STATE_REFRESH, day, adress);





                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> adapterView) {

                    }
                });


            }

            @Override
            public void onError(int i, String s) {
                Toast.makeText(MainActivity.this, "获取数据失败！", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void refreshAddress(){
        bmobQuery = new BmobQuery<MeetInfo>();
        bmobQuery.addQueryKeys("address");
        bmobQuery.findObjects(this, new FindListener<MeetInfo>() {
            @Override
            public void onSuccess(List<MeetInfo> list) {
                if (list.size()>0){
                    roomInfos.clear();
                }
                String[] roomInfo = new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    roomInfo[i] = list.get(i).getAddress();
                }
                Set<String> roomInfoSet = new TreeSet<String>();
                for (String roomInfos : roomInfo) {
                    roomInfoSet.add(roomInfos);
                }
                String[] roomInfoss=roomInfoSet.toArray(new String[0]);
                for (int i=0;i<roomInfoss.length;i++){
                    roomInfos.add(roomInfoss[i]);
                }

            }

            @Override
            public void onError(int i, String s) {

            }
        });
    }

    private void initRoonNum(){
        Intent intent=this.getIntent();
        //textViewFive.setText(intent.getStringExtra(Util.ADDRESS));
        textViewFour.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, QueryActivity.class);
                      intent.putExtra(Util.ADDRESS, adress);
                      startActivity(intent);
            }
        });
    }
    private void initTime() {
        final SimpleDateFormat myFmt2 = new SimpleDateFormat("yy-MM-dd");
        Date date = new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        day = myFmt2.format(date);
        textYear.setText(new SimpleDateFormat("yyyy/MM/dd").format(new Date()));
        textWeek.setText(new SimpleDateFormat("E").format(new Date()));
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData(0, STATE_REFRESH, day, adress);
        backHandler.postDelayed(runnable, 60 * 1000);
    }

   // @Override
   // public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
     //   getMenuInflater().inflate(R.menu.menu_main, menu);
     //   return true;
  //  }

  //  @Override
 //   public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
    //    int id = item.getItemId();

        //noinspection SimplifiableIfStatement
    //    if (id == R.id.action_settings) {
    ////        Intent intent = new Intent(MainActivity.this, QueryActivity.class);
     //       intent.putExtra(Util.ADDRESS, adress);
    //        startActivity(intent);
    ////        return true;
    //    }

    //    return super.onOptionsItemSelected(item);
   // }

    private void initListView() {
        mPullToRefreshView = (PullToRefreshListView) findViewById(R.id.list);
        loadingLayout = mPullToRefreshView.getLoadingLayoutProxy();
        loadingLayout.setLastUpdatedLabel("");
        loadingLayout
                .setPullLabel(getString(R.string.pull_to_refresh_bottom_pull));
        loadingLayout
                .setRefreshingLabel(getString(R.string.pull_to_refresh_bottom_refreshing));
        loadingLayout
                .setReleaseLabel(getString(R.string.pull_to_refresh_bottom_release));
        // //滑动监听
        mPullToRefreshView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {

                if (firstVisibleItem == 0) {
                    loadingLayout.setLastUpdatedLabel("");
                    loadingLayout
                            .setPullLabel(getString(R.string.pull_to_refresh_top_pull));
                    loadingLayout
                            .setRefreshingLabel(getString(R.string.pull_to_refresh_top_refreshing));
                    loadingLayout
                            .setReleaseLabel(getString(R.string.pull_to_refresh_top_release));
                } else if (firstVisibleItem + visibleItemCount + 1 == totalItemCount) {
                    loadingLayout.setLastUpdatedLabel("");
                    loadingLayout
                            .setPullLabel(getString(R.string.pull_to_refresh_bottom_pull));
                    loadingLayout
                            .setRefreshingLabel(getString(R.string.pull_to_refresh_bottom_refreshing));
                    loadingLayout
                            .setReleaseLabel(getString(R.string.pull_to_refresh_bottom_release));
                }
            }
        });

        // 下拉刷新监听
        mPullToRefreshView
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 下拉刷新(从第一页开始装载数据)
                        queryData(0, STATE_REFRESH, day, adress);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 上拉加载更多(加载下一页数据)
                        queryData(curPage, STATE_MORE, day, adress);
                    }
                });

        listView = mPullToRefreshView.getRefreshableView();
        adapter = new DeviceListAdapter(this);
        // 再设置adapter
        listView.setAdapter(adapter);
    }


    /**
     * 分页获取数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData(final int page, final int actionType, final String day, String adress) {
        Log.i("bmob", "pageN:" + page + " limit:" + limit + " actionType:" + actionType);

        final SimpleDateFormat myFmt2 = new SimpleDateFormat("HH:mm");
        Date date = new Date();
        final String currentTime = myFmt2.format(date);
        final String[] meetingDay;
        BmobQuery<MeetInfo> query = new BmobQuery<MeetInfo>();
        BmobQuery<MeetInfo> query2 = new BmobQuery<MeetInfo>();
        BmobQuery<MeetInfo> query3 = new BmobQuery<MeetInfo>();
        query.addWhereGreaterThanOrEqualTo("day", day);
        query2.addWhereEqualTo("address", adress);
        List<BmobQuery<MeetInfo>> querieList=new LinkedList<>();
        querieList.add(query);
        querieList.add(query2);
       BmobQuery<MeetInfo> andQuery=new BmobQuery<>();
        andQuery.and(querieList);
        //andQuery.order("startTime");
        andQuery.setLimit(limit);            // 设置每页多少条数据
//        query.setSkip(page*limit);		// 从第几条数据开始，
        andQuery.findObjects(this, new FindListener<MeetInfo>() {

            @Override
            public void onSuccess(List<MeetInfo> arg0) {
                // TODO Auto-generated method stub
                //如果查询到的数据不是同一天的，先按开会时间排序，再按开会时间排序。
                //如果查询到的数据是同一天的，就按照开会时间排序。
                meetInfoList.clear();
                Set<MeetInfo> set=new HashSet<MeetInfo>(arg0);
                BmobQuery<MeetInfo> andQuery=new BmobQuery<>();
                //set的值为1说明查询到的数据的日期是同一天
                if (set.size()==1){
                    andQuery.order("startTime");
                    if (arg0.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                        }
                        // 将本次查询的数据添加到bankCards中
                        for (MeetInfo td : arg0) {

                            if (td.getEndTime().compareTo(currentTime) > 0) {

                                meetInfoList.add(td);
                            }
                        }
                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
//                    showToast("数据加载完成");
                    } else if (actionType == STATE_MORE) {
                        showToast("没有更多数据了");

                    } else if (actionType == STATE_REFRESH) {
                        showToast("没有数据");
                        adapter.notifyDataSetChanged();
                    }
                    //set的值大于1说明查询到的数据的日期不是同一天
                }else if(set.size()>1){
                    andQuery.order("day,startTime");
                    if (arg0.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                        }
                        // 将本次查询的数据添加到bankCards中
                        for (MeetInfo td : arg0) {
                            if(td.getDay().compareTo(day)==0){
                                if (td.getEndTime().compareTo(currentTime) > 0) {

                                    meetInfoList.add(td);
                                }
                            }else if (td.getDay().compareTo(day)>0){
                                meetInfoList.add(td);
                            }

                        }
                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
//                    showToast("数据加载完成");
                    } else if (actionType == STATE_MORE) {
                        showToast("没有更多数据了");

                    } else if (actionType == STATE_REFRESH) {
                        showToast("没有数据");
                        adapter.notifyDataSetChanged();
                    }
                }



                mPullToRefreshView.onRefreshComplete();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("查询失败:" + arg1);
                mPullToRefreshView.onRefreshComplete();
            }
        });
    }

    private class DeviceListAdapter extends BaseAdapter {

        Context context;

        public DeviceListAdapter(Context context) {
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_bankcard_copy, null);
                holder = new ViewHolder();
                holder.image=(ImageView)convertView.findViewById(R.id.image);
                holder.peopleTitle = (TextView) convertView.findViewById(R.id.people_title);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                holder.meetingName = (TextView) convertView.findViewById(R.id.meeting_name);
                holder.people = (TextView) convertView.findViewById(R.id.people);
                holder.item_ly = convertView.findViewById(R.id.item_ly);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            final SimpleDateFormat myFmt2 = new SimpleDateFormat("HH:mm");
            Date date = new Date();
            final String currentTime = myFmt2.format(date);

            MeetInfo meetInfo = (MeetInfo) getItem(position);
            if (meetInfo.getStartTime().compareTo(currentTime) <= 0 && currentTime.compareTo(meetInfo.getEndTime()) <= 0) {
                LinearLayout.LayoutParams lp=new LinearLayout.LayoutParams(100,100);
                lp.setMargins(90,14,0,14);
                holder.image.setLayoutParams(lp);
                holder.image.setImageResource(R.drawable.first_logo);
                holder.meetingName.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                //holder.time.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 31);
                holder.name.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                holder.peopleTitle.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                holder.people.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 24);
                holder.meetingName.setTextColor(MainActivity.this.getResources().getColor(R.color.blue));
                holder.time.setTextColor(MainActivity.this.getResources().getColor(R.color.blue));
                holder.name.setTextColor(MainActivity.this.getResources().getColor(R.color.blue));
                holder.peopleTitle.setTextColor(MainActivity.this.getResources().getColor(R.color.blue));
                holder.people.setTextColor(MainActivity.this.getResources().getColor(R.color.blue));
          // holder.item_ly.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.colorAccent));
          } else {
                holder.item_ly.setBackgroundColor(MainActivity.this.getResources().getColor(R.color.white));
                holder.image.setImageResource(R.drawable.second_logo);
           }
       //    holder.num.setText(position + 1 + "");
       //     if (position>=0&&position<1){


          //  }else{
         //       holder.image.setImageResource(R.drawable.second_logo);
          //  }

            holder.time.setText(changeDateStyle(meetInfo)+meetInfo.getStartTime() + "--" + meetInfo.getEndTime());
            holder.name.setText(meetInfo.getName());
//            holder.adress.setText(meetInfo.getAddress());
          //  holder.people.setText("    参会人员:" + meetInfo.getParticipant());
            holder.people.setText(meetInfo.getParticipant());
            return convertView;
        }


        class ViewHolder {
            TextView meetingName;
            TextView time;
            TextView peopleTitle ;
            TextView name;
            TextView people;
            View item_ly;
            ImageView image;
        }
        //改变日期格式(将日期2015-10-11改为2015/10/11格式)
        private String changeDateStyle(MeetInfo meetInfo){
            String s=meetInfo.getDay();
            String[] meetInfoDay=s.split("-");
            String newDate="";
            for (int i=0;i<meetInfoDay.length;i++){
                newDate=newDate+meetInfoDay[i]+"/";
            }
           // newDate=newDate.substring(0,newDate.length()-1);
            return newDate;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return meetInfoList.size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return meetInfoList.get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        backHandler.removeCallbacks(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        backHandler.removeCallbacks(runnable);
        handlerThread.quit();

    }

    private void showToast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}
