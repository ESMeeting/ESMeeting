package com.eastsoft.meeting.esmeeting;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.CalendarView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.ILoadingLayout;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimeZone;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.listener.FindListener;

/**
 * Created by ll on 2015/11/17.
 */
public class QueryActivity extends AppCompatActivity {

//    public String APPID="526ce376e1eaa62f903a7ea16b108922";
    private List<MeetInfo> meetInfoList=new LinkedList<>();
    private PullToRefreshListView mPullToRefreshView;
    private ILoadingLayout loadingLayout;
    private ListView mMsgListView;
    private DeviceListAdapter adapter;
    private String day;
    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int limit = 30;		// 每页的数据是10条
    private int curPage = 0;		// 当前页的编号，从0开始
    private TextView tips;
    private String adress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_query);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        adress=getIntent().getStringExtra(Util.ADDRESS);
        initListView();
        final SimpleDateFormat myFmt2=new SimpleDateFormat("yy-MM-dd");
        Date date=new Date();
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Shanghai"));
        day=myFmt2.format(date);
        //tips= (TextView) findViewById(R.id.text_day);
        //tips.setText(day);
        CalendarView calendarView= (CalendarView) findViewById(R.id.calendarView);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(CalendarView calendarView, int i, int i1, int i2) {
                SimpleDateFormat myFmt = new SimpleDateFormat("yyyyMMdd");
                try {
                    day= myFmt2.format(myFmt.parse(String.valueOf(i) + String.format("%02d", (i1+1)) + String.format("%02d", i2))).toString();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                //tips.setText(day);
                queryData(0, STATE_REFRESH,day);

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        queryData(0, STATE_REFRESH,day);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
////        if (id == R.id.action_settings) {
////            return true;
////        }
//
//        return super.onOptionsItemSelected(item);
//    }
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
                        queryData(0, STATE_REFRESH,day);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 上拉加载更多(加载下一页数据)
                        queryData(curPage, STATE_MORE,day);
                    }
                });

        mMsgListView = mPullToRefreshView.getRefreshableView();
        adapter= new DeviceListAdapter(this);
        // 再设置adapter
        mMsgListView.setAdapter(adapter);
    }


    /**
     * 分页获取数据
     * @param page	页码
     * @param actionType	ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData(final int page, final int actionType,String day){
        Log.i("bmob", "pageN:" + page + " limit:" + limit + " actionType:" + actionType);


        BmobQuery<MeetInfo> query = new BmobQuery<MeetInfo>();
        BmobQuery<MeetInfo> query2 = new BmobQuery<MeetInfo>();
        query.addWhereEqualTo("day", day);
        query2.addWhereEqualTo("address", adress);

        List<BmobQuery<MeetInfo>> querieList=new LinkedList<>();
        querieList.add(query);
        querieList.add(query2);
        BmobQuery<MeetInfo> andQuery=new BmobQuery<>();
        andQuery.and(querieList);

        andQuery.order("startTime");
        andQuery.setLimit(limit);            // 设置每页多少条数据


//        query.setSkip(page*limit);		// 从第几条数据开始，
        andQuery.findObjects(this, new FindListener<MeetInfo>() {

            @Override
            public void onSuccess(List<MeetInfo> arg0) {
                // TODO Auto-generated method stub
                meetInfoList.clear();
                if(arg0.size()>0){
                    if(actionType == STATE_REFRESH){
                        // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                        curPage = 0;
                    }
                    // 将本次查询的数据添加到bankCards中
                    for (MeetInfo td : arg0) {
                        meetInfoList.add(td);
                    }
                    // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                    curPage++;
//                    showToast("数据加载完成");
                }else if(actionType == STATE_MORE){
                    showToast("没有更多数据了");

                }else if(actionType == STATE_REFRESH){
                    showToast("没有数据");
                    adapter.notifyDataSetChanged();
                }
                mPullToRefreshView.onRefreshComplete();
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                showToast("查询失败:"+arg1);
                mPullToRefreshView.onRefreshComplete();
            }
        });
    }

    private class DeviceListAdapter extends BaseAdapter {

        Context context;

        public DeviceListAdapter(Context context){
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {

                convertView = LayoutInflater.from(context)
                        .inflate(R.layout.list_item_bankcard_two, null);
                holder = new ViewHolder();
                holder.image=(ImageView)convertView.findViewById(R.id.image);
                holder.time = (TextView) convertView .findViewById(R.id.time);
                holder.name = (TextView) convertView .findViewById(R.id.name);
                holder.adress = (TextView) convertView .findViewById(R.id.adress);
                holder.people = (TextView) convertView .findViewById(R.id.people);
                holder.item_ly = convertView.findViewById(R.id.item_ly);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            MeetInfo meetInfo = (MeetInfo) getItem(position);
            holder.image.setImageResource(R.drawable.logo);
            holder.item_ly.setBackgroundColor(QueryActivity.this.getResources().getColor(R.color.white));
           // holder.time.setText(meetInfo.getStartTime().substring(9)+"--"+meetInfo.getEndTime().substring(9));
            holder.time.setText(meetInfo.getStartTime() + "--" + meetInfo.getEndTime());
            holder.name.setText(meetInfo.getName());
//            holder.adress.setText(meetInfo.getAddress());
            holder.people.setText(meetInfo.getParticipant());
            return convertView;
        }
        class ViewHolder {
            TextView num;
            TextView time;
            TextView adress;
            TextView name;
            TextView people;
            View item_ly;
            ImageView image;
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
    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}