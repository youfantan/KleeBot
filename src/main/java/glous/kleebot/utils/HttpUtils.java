package glous.kleebot.utils;

import glous.kleebot.utils.builtin.HttpResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.util.Map;

public class HttpUtils {
    public static final String CONTENT_TYPE_JSON="application/json";
    public static final String CONTENT_TYPE_TEXT="text/plain";
    public HttpResponse post(@NotNull String _url, @Nullable Proxy proxy, @Nullable Map<String,String> headers, @Nullable byte[] body) throws IOException {
        HttpResponse response=new HttpResponse();
        HttpURLConnection connection;
        URL url=new URL(_url);
        if (proxy!=null){
            connection=(HttpURLConnection) url.openConnection(proxy);
        } else{
            connection=(HttpURLConnection) url.openConnection();
        }
        if (headers!=null){
            for (Map.Entry<String, String> header :
                    headers.entrySet()) {
                connection.setRequestProperty(header.getKey(), header.getValue());
            }
        }
        connection.setRequestMethod("POST");
        connection.setDoInput(true);
        connection.setDoOutput(true);
        BufferedOutputStream out = null;
        if (body!=null){
            out=new BufferedOutputStream(connection.getOutputStream());
            out.write(body);
            out.flush();
        }
        response.setResponseCode(connection.getResponseCode());
        BufferedInputStream in=new BufferedInputStream(connection.getInputStream());
        byte[] bytes=new byte[1024];
        int bytesRead;
        ByteArrayOutputStream bOut=new ByteArrayOutputStream();
        while ((bytesRead=in.read(bytes))!=-1){
            bOut.write(bytes,0,bytesRead);
        }
        response.setBody(bOut.toByteArray());
        in.close();
        bOut.close();
        if (out!=null){
            out.close();
        }
        return response;
    }
}
