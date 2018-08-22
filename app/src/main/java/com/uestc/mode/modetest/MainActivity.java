package com.uestc.mode.modetest;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * created by mode
 */
public class MainActivity extends AppCompatActivity {

    EditText editText;
    Button button;
    TextView textView;
    Handler handler = new Handler();
    List<TitleBean> titleBeans = new ArrayList<>();
    ListView listView;
    MainAdapter mainAdapter;
    View footer;
    int page = 0;
    String currentKeyword = "";
    boolean isRequest = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_et);
        button = findViewById(R.id.search_btn);
        listView = findViewById(R.id.result);
        footer = LayoutInflater.from(this).inflate(R.layout.layout_footer,null);
        listView.addFooterView(footer);
        footer.setVisibility(View.GONE);
        mainAdapter = new MainAdapter(this,titleBeans);
        listView.setAdapter(mainAdapter);
//        titleTv = findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String keyword = editText.getText().toString();
                        if(keyword.isEmpty())return;
                        currentKeyword = keyword;
                        titleBeans.clear();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"开始搜索",Toast.LENGTH_SHORT).show();
                                notifys();
                            }
                        });
                        requestUrl(0,keyword);
                    }
                }).start();
            }
        });

        footer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "加载下一页中...", Toast.LENGTH_SHORT).show();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        requestUrl(page,currentKeyword);
                    }
                }).start();
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                TitleBean titleBean = titleBeans.get(i);
                Intent intent = new Intent(MainActivity.this,DetailActivity.class);
                intent.putExtra("titlebean",titleBean);
                startActivity(intent);
            }
        });
    }

    public void requestUrl(int temppage,String keyword){
        if(isRequest)return;
        this.page = temppage;

        String url = "https://search.sina.com.cn/?q="+keyword+"&range=all&c=news&sort=time&col=&source=&from=&country=&size=&time=&a=&page="+page;
        List<TitleBean> tempTitleBeans = sendGet(url);
        isRequest = false;
        if(tempTitleBeans.size() == 0){
            if(page == 0){
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        notifys();
                    }
                });
            }
            return;
        }
        else {
            String keywordList = "";
            for(int i=0;i<titleBeans.size();i++){
                keywordList += ","+titleBeans.get(i).getUrl();
            }
            for(int i=0;i<tempTitleBeans.size();i++){
                if(keywordList.contains(tempTitleBeans.get(i).getUrl())){
                    continue;
                }else {
                    titleBeans.add(tempTitleBeans.get(i));
                }
            }
        }
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d("modelog",titleBeans.size()+"");
                page++;
                notifys();
            }
        });
    }
    /**
     * 向指定URL发送GET方法的请求
     *
     * @param url
     *      发送请求的URL
     * @return URL 所代表远程资源的响应结果
     */
    public static List<TitleBean> sendGet(String url) {
        String result = "";
        BufferedReader in = null;
        List<TitleBean> titleBeans = new ArrayList<>();
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection connection = realUrl.openConnection();
            // 设置通用的请求属性

            connection.setReadTimeout(10*1000);
            connection.setConnectTimeout(10*1000);
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            connection.setRequestProperty("Accept-Language", "en-US,en;q=0.9");
            connection.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/");
            connection.setRequestProperty("user-agent",
                    "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/68.0.3440.106 Safari/537.36");
            // 建立实际的连接
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            in = new BufferedReader(new InputStreamReader(inputStream, "gb2312"));

            String line;

            while ((line = in.readLine()) != null) {
//                Matcher m = pattern.matcher(line);
                if(line.contains("http://finance.sina.com.cn") && !line.contains("频道")){
                    String[] lines = line.split(" target=\"_blank\">");
                    if(lines.length > 1){
                        String murl1 = lines[0].replaceAll("<h2><a href=","").replaceAll("\"","").replaceAll(" ","");
//                      <span style="color:#C03">万科</span>确立租赁为核心业务，半年新增117个项目仅两个在一线
                        String[] titles = lines[1].split("</a> <span class=\"fgray_time\">");
                        if(titles.length > 1){
                            String titlessda = titles[0].replaceAll("<span style=\"color:#C03\">","").replaceAll("</span>","");
                            String[] timesasd = titles[1].split("</span>");
                            if(timesasd.length>1){
                                String tea = timesasd[0];
                                TitleBean titleBean = new TitleBean();
                                titleBean.setTitle(titlessda);
                                titleBean.setUrl(murl1);
                                titleBean.setSubContent(tea);
                                titleBeans.add(titleBean);
//                                result += "url: "+murl1 +"\n"+"title: "+titlessda+"\n"+"time: "+tea+"\n\n";
                            }

                        }

                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
        return titleBeans;
    }

    public void notifys(){
        mainAdapter.setTitleBeans(titleBeans);
        mainAdapter.notifyDataSetChanged();
        if(titleBeans.size() == 0){
            footer.setVisibility(View.GONE);
        }else {
            footer.setVisibility(View.VISIBLE);
        }
        if(titleBeans.size()!=0)
        Toast.makeText(this, "加载完成", Toast.LENGTH_SHORT).show();
    }
}
