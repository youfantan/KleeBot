package glous.kleebot.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpClient {
    public HttpClient(String requestPath, String body, OutputStream output) {
        this.requestPath = requestPath;
        this.body = body;
        this.output = output;
    }

    public String getRequestPath() {
        return requestPath;
    }

    public String getBody() {
        return body;
    }

    private final String requestPath;
    private final String body;
    private final OutputStream output;
    private final ByteArrayOutputStream responseBody=new ByteArrayOutputStream();
    private String responseCode="200 OK";
    private final HashMap<String,String> responseHeader=new HashMap<>();
    public void setHeader(String k,String v){
        responseHeader.put(k,v);
    }
    public void setResponseCode(String responseCode){
        this.responseCode=responseCode;
    }
    public void writeResponseBody(byte[] body){
        try {
            responseBody.write(body);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void writeResponseBody(String body){
        writeResponseBody(body.getBytes(StandardCharsets.UTF_8));
    }
    public void finish() throws IOException {
        output.write(("HTTP/1.1 "+responseCode+"\n").getBytes(StandardCharsets.UTF_8));
        responseHeader.put("Content-Length",String.valueOf(responseBody.size()));
        for (String s:responseHeader.keySet()){
            output.write((s+": "+responseHeader.get(s)+"\n").getBytes(StandardCharsets.UTF_8));
        }
        output.write("\n".getBytes(StandardCharsets.UTF_8));
        responseBody.close();
        output.write(responseBody.toByteArray());
        output.flush();
    }
}
