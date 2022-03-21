package shandiankulishe.kleebot.web;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import shandiankulishe.kleebot.KleeBot;
import shandiankulishe.kleebot.async.AsyncTaskQueue;
import shandiankulishe.kleebot.utils.FileUtils;
import shandiankulishe.kleebot.utils.ZipUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpServer {
    private int port;
    private boolean flag=true;
    private AsyncTaskQueue queue;
    private String resourcePackName;
    private Logger logger= LogManager.getLogger(HttpServer.class);
    public HttpServer(AsyncTaskQueue queue,int port){
        this.port=port;
        this.queue=queue;
    }
    private HashMap<String,WebService> services=new HashMap<>();
    public void registerService(String name,WebService service){
        services.put(name,service);
    }
    public void setResourcePack(String path){
        this.resourcePackName=path.substring(0,path.length()-4);
        ZipUtils.extractZipFile(path,"web"+File.separatorChar+resourcePackName);
    }
    public Set makeHeader(String buffer){
        StringBuilder left= new StringBuilder();
        StringBuilder right= new StringBuilder();
        boolean in_left=true;
        boolean have_word=false;
        for (char c:buffer.toCharArray()){
            if (in_left){
                if (c==':'){
                    in_left=false;
                } else{
                    left.append(c);
                }
            } else{
                if (c!=' '&&!have_word){
                    have_word=true;
                    right.append(c);
                } else{
                    if (have_word){
                        right.append(c);
                    }
                }
            }
        }
        return new Set(left.toString(),right.toString());
    }
    public void start(){
            try {
                ServerSocket server=new ServerSocket(port);
                while (KleeBot.getRunningFlag()&&flag){
                    Socket clientSocket=server.accept();
                    InputStream in=clientSocket.getInputStream();
                    OutputStream out=clientSocket.getOutputStream();
                    BufferedReader reader=new BufferedReader(new InputStreamReader(in));
                    BufferedWriter writer=new BufferedWriter(new OutputStreamWriter(out));
                    String buffer;
                    HashMap<String,String> requestHeaders=new HashMap<>();
                    String service=null;
                    String method=null;
                    while ((buffer=reader.readLine())!=null&&!buffer.isEmpty()){
                        if (buffer.startsWith("GET")){
                            method="GET";
                            service=buffer.substring(buffer.indexOf("/"),buffer.indexOf("HTTP")-1);
                            logger.info("Request Method: GET.Request Service: %s".formatted(service));
                        } else{
                            Set header=makeHeader(buffer);
                            requestHeaders.put(header.getK(),header.getV());
                        }
                    }
                    if (method != null&&method.equals("GET")){
                        if (!services.containsKey(service)){
                            if (resourcePackName != null){
                                if (service.endsWith("/")) {
                                    service+="index.html";
                                }
                                File staticFile=new File("web"+File.separatorChar+service);
                                if (staticFile.exists()&&staticFile.isFile()){
                                    ByteArrayOutputStream bOut=new ByteArrayOutputStream();
                                    byte[] content= FileUtils.readFile(staticFile.getAbsolutePath());
                                    bOut.write("HTTP/1.1 200 OK\n".getBytes(StandardCharsets.UTF_8));
                                    if (service.endsWith(".html")){
                                        bOut.write("Content-Type: text/html".getBytes(StandardCharsets.UTF_8));
                                    } else if (service.endsWith(".css")) {
                                        bOut.write("Content-Type: text/css".getBytes(StandardCharsets.UTF_8));
                                    } else if (service.endsWith(".js")){
                                        bOut.write("Content-Type: application/javascript".getBytes(StandardCharsets.UTF_8));
                                    } else if (service.endsWith(".jpg")||service.endsWith(".png")||service.endsWith(".gif")){
                                        bOut.write(("Content-Type: image/"+service.substring(0,service.length()-3)).getBytes(StandardCharsets.UTF_8));
                                    } else if (service.endsWith(".json")){
                                        bOut.write("Content-Type: application/json".getBytes(StandardCharsets.UTF_8));
                                    } else if (service.endsWith(".xml")){
                                        bOut.write("Content-Type: application/xml".getBytes(StandardCharsets.UTF_8));
                                    } else {
                                        bOut.write("Content-Type: octet-stream".getBytes(StandardCharsets.UTF_8));
                                    }
                                    bOut.write(("\nAccess-Control-Allow-Origin: *\n\n").getBytes(StandardCharsets.UTF_8));
                                    bOut.write(content);
                                    bOut.flush();
                                    bOut.close();
                                    out.write(bOut.toByteArray());
                                    out.flush();
                                    out.close();
                                    in.close();
                                    clientSocket.close();
                                    continue;
                                }
                            }
                            String resp =
                                    """
                                            HTTP/1.1 404 Not Found
                                            Server: KleeBot Http Services
                                            Content-Type: text/html
                                                                            
                                            <html><head><title>404 Not Found</title><head><body><h1>Request Resource Not Found</h1><p>KleeBot Http Services(%s@%s)</p></body></html>
                                            """.formatted("dev", "@dev-000000 build at 2022-2-27");
                            writer.write(resp);
                            writer.flush();
                            in.close();
                            out.close();
                            clientSocket.close();
                        } else{
                            HttpClient client=new HttpClient(in, requestHeaders, method, out);
                            WebService webService=services.get(service);
                            queue.addTask(()->{ webService.response(client); },this.getClass().getName());
                        }
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
                logger.error(e);
            }

    }
    public void stop(){
        flag=false;
    }
}
