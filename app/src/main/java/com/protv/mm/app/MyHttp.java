package com.protv.mm.app;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MyHttp
{

    public static enum RequesMethod
    {
        GET("GET"),POST("POST"),PUT("PUT"),DELETE("DELETE");
        private final String stringValue;
        RequesMethod(final String s) {
            stringValue = s;
        }
        public String toString() {
            return stringValue;
        }
    }

    private final Response response;
    private HttpURLConnection httpConn;
    private String url,errorMsg;
    boolean error=false;
    private final Map<String,String> fields=new HashMap<>();
    private final Map<String,String> files=new HashMap<>();
    private final List<String> fileArray=new ArrayList<>();
    private final Map<String,String> headers=new HashMap<>();
    private final RequesMethod requestMethod;
    private String boundary;
    private String charset="UTF-8";
    private OutputStream outputStream;
    private PrintWriter writer;

    public static interface Response
    {
        public void onResponse(String response);
        public void onError(String msg);
    }

    public MyHttp(RequesMethod requestMethod, Response response)
    {
        this.requestMethod = requestMethod;
        this.response=response;
    }

    public MyHttp url(String url)
    {
        this.url = url;
        return this;
    }

    public MyHttp field(String name, String val)
    {
        fields.put(name, val);
        return this;
    }

    public MyHttp file(String name, String path)
    {
        files.put(name, path);
        return this;
    }

    public MyHttp fileArray(String path)
    {
        fileArray.add(path);
        return this;
    }

    public MyHttp header(String name, String val)
    {
        headers.put(name, val);
        return this;
    }

    public MyHttp auth(String token)
    {
        headers.put("Authentication", "Bearer " + token);
        return this;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public MyHttp auth(String username, String password)
    {
        try
        {
            String encoded = Base64.getEncoder().encodeToString((username + ":" + password).getBytes("UTF-8"));
            headers.put("Authentication", "Basic " + encoded);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return this;
    }

    public MyHttp charset(String val)
    {
        this.charset = val;
        return this;
    }

    private void addFormField(String name, String value)
    {
        this.writer.append("--" + this.boundary).append("\r\n");
        this.writer.append("Content-Disposition: form-data; name=\"" + name + "\"").append
                ("\r\n");
        this.writer.append("Content-Type: text/plain; charset=" + this.charset).append(
                "\r\n");
        this.writer.append("\r\n");
        this.writer.append(value).append("\r\n");
        this.writer.flush();
    }

    private void addFilePart(String fieldName, File uploadFile)
    {
        try
        {
            String fileName = uploadFile.getName();
            this.writer.append("--" + this.boundary).append("\r\n");
            this.writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName +
                            "\"; filename=\"" + fileName + "\"").append
                    ("\r\n");
            this.writer.append(
                    "Content-Type: " +
                            URLConnection.guessContentTypeFromName(fileName)).append
                    ("\r\n");
            this.writer.append("Content-Transfer-Encoding: binary").append("\r\n");
            this.writer.append("\r\n");
            this.writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1)
                this.outputStream.write(buffer, 0, bytesRead);

            this.outputStream.flush();
            inputStream.close();
            this.writer.append("\r\n");
            this.writer.flush();
        }
        catch (Exception e)
        {
          //  e.printStackTrace();
        }
    }

    public void runTask() {

        StringBuffer result=new StringBuffer();
        this.boundary = "===" + System.currentTimeMillis() + "===";
        try
        {
            this.httpConn = ((HttpURLConnection)new URL(url).openConnection());
            this.httpConn.setUseCaches(false);
            this.httpConn.setRequestMethod(this.requestMethod.toString());

            //Add header fields
            Iterator itr = headers.entrySet().iterator();
            while (itr.hasNext())
            {
                Map.Entry entry = (Map.Entry)itr.next();
                httpConn.setRequestProperty(entry.getKey().toString(), entry.getValue().toString());
            }

            if (requestMethod.toString().equals("GET"))
            {
                for (Map.Entry < String, String > item: fields.entrySet())
                {
                    try
                    {
                        String key = URLEncoder.encode(item.getKey(), charset);
                        String value = URLEncoder.encode(item.getValue(), charset);
                        if (!url.contains("?"))
                        {
                            url += "?" + key + "=" + value;
                        }
                        else
                        {
                            url += "&" + key + "=" + value;
                        }
                    }
                    catch (UnsupportedEncodingException ignored)
                    {}
                }
            }
            else
            {
                this.httpConn.setDoOutput(true);
                this.httpConn.setDoInput(true);
                this.httpConn.setRequestProperty("Content-Type",
                        "multipart/form-data; boundary=" + this.boundary);
                this.outputStream = this.httpConn.getOutputStream();
                this.writer = new PrintWriter(new OutputStreamWriter(this.outputStream, charset), true);

                //========

                itr = fields.entrySet().iterator();
                while (itr.hasNext())
                {
                    Map.Entry entry = (Map.Entry)itr.next();
                    addFormField(entry.getKey().toString(), entry.getValue().toString());
                }

                if (fileArray.size() > 0)
                {
                    for (String path:fileArray)
                    {
                        addFilePart("files[]", new File(path));
                    }
                    this.writer.append("\r\n").flush();
                    this.writer.append("--" + this.boundary + "--").append("\r\n");
                    this.writer.close();
                }

                itr = files.entrySet().iterator();
                while (itr.hasNext())
                {
                    Map.Entry entry = (Map.Entry)itr.next();
                    addFilePart(entry.getKey().toString(), new File(entry.getValue().toString()));

                    //========

                    this.writer.append("\r\n").flush();
                    this.writer.append("--" + this.boundary + "--").append("\r\n");
                    this.writer.close();
                }
            }
            int status = this.httpConn.getResponseCode();
            if (status == 200)
            {
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(this.httpConn.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null)
                    result.append(line).append("\n");

                reader.close();
                this.httpConn.disconnect();
            }
            else
            {
                errorMsg = "Server returned non-OK status: " + status;
                error = true;

            }

            //========

        }
        catch (Exception e)
        {
           // e.printStackTrace();
            errorMsg = e.toString();
            error = true;

        }

        if (error)
        {
            response.onError(errorMsg);
        }
        else
        {
            response.onResponse(result.toString());
        }

    }
}
