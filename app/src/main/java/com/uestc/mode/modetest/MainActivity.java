package com.uestc.mode.modetest;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText = findViewById(R.id.edit_et);
        button = findViewById(R.id.search_btn);
//        textView = findViewById(R.id.result);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String keyword = editText.getText().toString();
                        if(keyword.isEmpty())return;
                        String url = "https://search.sina.com.cn/?q="+keyword+"&range=all&c=news&sort=time&col=&source=&from=&country=&size=&time=&a=&page=1";
                        titleBeans = sendGet(url);

                    }
                }).start();
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
                        String[] titles = lines[1].split("</a> <span class=\"fgray_time\">");
                        if(titles.length > 1){
                            String titlessda = titles[0];
                            String[] timesasd = titles[1].split("</span>");
                            if(timesasd.length>1){
                                String tea = timesasd[0];
                                TitleBean titleBean = new TitleBean();
                                titleBean.setTitle(titlessda);
                                titleBean.setUrl(murl1);
                                titleBean.setSubContent(tea);
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
}
