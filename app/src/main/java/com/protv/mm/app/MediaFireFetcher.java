package com.protv.mm.app;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediaFireFetcher extends Thread {


    String url;
    Response response;

    public interface Response
    {
        public void onResponse(String response);
        public void onError(String msg);
    }


    public MediaFireFetcher(String url, Response response){
        this.url=url;
        this.response=response;
    }

    @Override
    public void run() {
        super.run();
        download(url);
    }

    public  void  download(String url){

        try
        {
            Document doc = Jsoup.connect(url).get();
            String result=process(doc);
            result=result.substring(0,result.lastIndexOf(".")+4);
            response.onResponse(result);

        }
        catch (IOException e)
        {
            e.printStackTrace();
            response.onError(e.toString());
        }

    }

    public String process(Document doc) throws IOException {

        Element downloadButton=doc.getElementById("downloadButton");
        if(downloadButton!=null){

            Document dom = Jsoup.connect("https:"+downloadButton.attr("href")).get();
//            String regex;
//            String result = "";
//            regex = "https://download(.*?)\"";
//
//            String strPattern = "\"[^\']*\"";

            Elements elements=dom.getElementsByTag("script");
            String[] arrOfStr =elements.first().toString().split("'");
            return arrOfStr[1];
        }
        return null;
    }

}
