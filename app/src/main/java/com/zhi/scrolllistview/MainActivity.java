package com.zhi.scrolllistview;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.zhi.scrolllistview.service.NewsService;

import java.util.ArrayList;
import java.util.List;

/**
 * 分批加载ListView
 */

public class MainActivity extends Activity {
    private final static int MAX_PAGE = 5; // 最多加载5页
    private final static int RESULT = 20; // 每次加载20条
    private final static int MESSAGE_NEWS = 0x1;

    private final NewsHandler mHandler = new NewsHandler();
    private View footerView;
    private ListView mListView;
    private BaseAdapter mAdapter;
    private List<String> news = new ArrayList<String>();
    private boolean isLoadFinish = true;  // 标记是否加载完成

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView) findViewById(R.id.listView);
        footerView = LayoutInflater.from(MainActivity.this).inflate(R.layout.footer_news, null);

        news = NewsService.getData();
        mAdapter = new ArrayAdapter<String>(MainActivity.this,
                R.layout.adapter_item_news, R.id.textView, news);

        mListView.addFooterView(footerView);  // footerView 必须在setAdapter之前,不然不会显示
        mListView.setAdapter(mAdapter);
        mListView.removeFooterView(footerView);  //  第一次加载不需要 footerView
        mListView.setOnScrollListener(new NewsScrollListener());
    }

    private final class NewsScrollListener implements AbsListView.OnScrollListener {

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            /*
            * scrollState 有三种状态
            * SCROLL_STATE_IDLE  停止滚动
            * SCROLL_STATE_TOUCH_SCROLL  正在滚动
            * SCROLL_STATE_FLING  开始滚动
            * */
        }

        @Override
        public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
            int lastVisibleItem = mListView.getLastVisiblePosition();
            if (lastVisibleItem + 1 == totalItemCount) {  // 最后一个可见的条目等于总数
                if (totalItemCount <= 0) {
                    return;
                }
                int currentPage = totalItemCount % RESULT == 0 ? totalItemCount/RESULT: totalItemCount/RESULT+1;
                if(currentPage < MAX_PAGE && isLoadFinish) {  // 上一次加载加载完成后才重新加载
                    mListView.addFooterView(footerView);
                    isLoadFinish = false;
                    new NewsThread().start();  // 加载数据，可能用在网络加载，因此使用异步方式
                }
            }
        }
    }

    private class NewsThread extends Thread {
        @Override
        public void run() {
            try {
                Thread.sleep(3000);  //  模拟网络加载耗时
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            List<String> newsLoads = NewsService.getData();
            for (String newsLoad : newsLoads) {
                news.add(newsLoad);   // 新加载的数据
            }
            mHandler.sendMessage(mHandler.obtainMessage(MESSAGE_NEWS, news));  //  更新UI
        }
    }

    private final class NewsHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_NEWS:
                    mAdapter.notifyDataSetChanged();
                    mListView.removeFooterView(footerView);
                    isLoadFinish = true;
                    break;
            }
        }
    }
}