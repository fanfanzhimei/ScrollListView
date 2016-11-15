package com.zhi.scrolllistview.service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/15.
 */
public class NewsService {
    public static List<String> getData() {
        List<String> news = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            news.add("天大地大：何处是我家" + i);
        }
        return news;
    }
}