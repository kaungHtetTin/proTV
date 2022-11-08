package com.protv.mm.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JSONGetter extends Thread
{

    String url;
    String result;
    Response response;

    public interface Response
    {
        public void onResponse(String response);
        public void onError(String msg);
    }

    public JSONGetter(String url,Response response){
        this.url=url;
        this.response=response;
    }


    @Override
    public void run() {
        super.run();
        download(url);
    }

    public  void  download(String url){
        StringBuffer result=new StringBuffer();
        try
        {
            HttpURLConnection httpConn = ((HttpURLConnection)new URL(url).openConnection());
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(httpConn.getInputStream(),"UTF-8"));
            String line = null;
            while ((line = reader.readLine()) != null)
                result.append(line).append("\n");

            reader.close();
            httpConn.disconnect();
            response.onResponse(result.toString());

        }
        catch (IOException e)
        {
            e.printStackTrace();
            response.onError(e.toString());
        }


    }
}
