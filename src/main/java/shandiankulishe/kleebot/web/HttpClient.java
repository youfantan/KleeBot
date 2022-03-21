package shandiankulishe.kleebot.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpClient {
    public HttpClient(InputStream in,HashMap<String,String> headers,String requestMethod,OutputStream out){
        this.in=in;
        this.headers=headers;
        this.requestMethod=requestMethod;
        this.out=out;
    }
    //Set
    private OutputStream out;
    private String responseCode;
    private ByteArrayOutputStream finalOut=new ByteArrayOutputStream();
    private StringBuffer finalHeader=new StringBuffer();
    public void write(byte[] bytes) throws IOException {
        finalOut.write(bytes);
    }
    public void write(String str) throws IOException {
        finalOut.write((str+"\n").getBytes(StandardCharsets.UTF_8));
    }
    public void setHeader(String K,String V){
        finalHeader.append(K).append(": ").append(V).append("\n");
    }
    public void setResponseCode(String code){
        responseCode=code+"\n";
    }
    public void finish(){
        try {
            if (responseCode==null){
                setResponseCode("200 OK");
            }
            setHeader("Server","KleeBot Http Services");
            if (finalOut.size()>0){
                setHeader("Content-Length",String.valueOf(finalOut.size()));
            }
            ByteArrayOutputStream bytesOut=new ByteArrayOutputStream();
            bytesOut.write("HTTP/1.1 ".getBytes(StandardCharsets.UTF_8));
            bytesOut.write(responseCode.getBytes(StandardCharsets.UTF_8));
            bytesOut.write(finalHeader.toString().getBytes(StandardCharsets.UTF_8));
            bytesOut.write("\n".getBytes(StandardCharsets.UTF_8));
            bytesOut.write(finalOut.toByteArray());
            bytesOut.close();
            String s= bytesOut.toString(StandardCharsets.UTF_8);
            out.write(bytesOut.toByteArray());
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public InputStream getIn() {
        return in;
    }

    public HashMap<String, String> getHeaders() {
        return headers;
    }

    public String getRequestMethod() {
        return requestMethod;
    }

    //Get
    private InputStream in;
    private HashMap<String,String> headers;
    private String requestMethod;
}
