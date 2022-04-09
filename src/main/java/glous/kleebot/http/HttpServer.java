package glous.kleebot.http;

import glous.kleebot.utils.FileUtils;
import glous.kleebot.utils.ZipUtils;
import glous.kleebot.KleeBot;
import glous.kleebot.log.Logger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class HttpServer {
    private int port;
    public boolean RUNNING_FLAG;
    public HttpServer(int port){
        this.port=port;
    }
    private HashMap<String,IWebService> registeredServices=new HashMap<>();
    private Logger logger= Logger.getLogger(HttpServer.class);
    public void register(String resourcePackPath){
        ZipUtils.extractZipFile(resourcePackPath,KleeBot.config.getResourcePackDir());
    }
    public void register(String path,IWebService service){
        registeredServices.put(path,service);
    }
    public void start(){
        try {
            RUNNING_FLAG=true;
            ServerSocket server=new ServerSocket(port);
            Socket client;
            HttpTaskQueue queue=new HttpTaskQueue(KleeBot.config.getQueueSize(),this);
            for (IWebService service
                    : registeredServices.values()
                 ) {
                service.init();
            }
            queue.start();
            logger.info("KleeBot Http Server running on port "+port);
            while (RUNNING_FLAG){
                client=server.accept();
                //post task into queue
                Socket finalClient= client;
                queue.addTask(() -> {
                    try {
                        processSocket(finalClient);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop(){
        RUNNING_FLAG=false;
        for (IWebService service :
                registeredServices.values()) {
            service.stop();
        }
        File file=new File(KleeBot.config.getResourcePackDir());
        File[] files=file.listFiles();
        for (File f :
                files) {
            f.delete();
        }
    }
    private String getMIMETypes(String fileName){
        if (fileName.endsWith(".html")){
            return "text/html";
        } else if (fileName.endsWith(".css")){
            return "text/css";
        } else if (fileName.endsWith(".js")){
            return "text/javascript";
        } else if (fileName.endsWith(".jpg")){
            return "image/jpeg";
        } else if (fileName.endsWith(".png")){
            return "image/png";
        } else if (fileName.endsWith(".webp")){
            return "image/webp";
        } else if (fileName.endsWith(".ico")){
            return "image/x-icon";
        } else if (fileName.endsWith("gif")){
            return "image/gif";
        } else if (fileName.endsWith(".bmp")){
            return "image/bmp";
        } else if (fileName.endsWith(".json")){
            return "application/json";
        } else if (fileName.endsWith(".xml")){
            return "application/xml";
        } else if (fileName.endsWith(".txt")){
            return "text/plain";
        } else {
            return "application/octet-stream";
        }
    }
    private void processSocket(Socket client) throws IOException {
        BufferedReader input=new BufferedReader(new InputStreamReader(client.getInputStream()));
        String buffer;
        String method=input.readLine();
        if (method==null){
            return;
        }
        String path=method.substring(method.indexOf("/"),method.indexOf("HTTP")-1);
        if (method.startsWith("GET")){
            method="GET";
        } else if (method.startsWith("POST")){
            method="POST";
        } else {
            sendErrorPage(client.getOutputStream());
            client.getOutputStream().close();
            client.getInputStream().close();
            client.close();
            return;
        }
        HashMap<String,String> headers=new HashMap<>();
        int read=0;
        while ((buffer=input.readLine())!=null&&!buffer.isEmpty()){
            read+=buffer.length();
            String value=buffer.substring(buffer.indexOf(":")+1);
            if (value.startsWith(" ")){
                value=value.substring(1);
            }
            headers.put(buffer.substring(0,buffer.indexOf(":")),value);
        }
        StringBuilder builder=new StringBuilder();
        if (method.equals("POST")){
            if(headers.containsKey("Content-Length")){
                long length=Integer.parseInt(headers.get("Content-Length"));
                for (int i = 0; i < length; i++) {
                    builder.append((char)input.read());
                }
            } else {
                while ((buffer=input.readLine())!=null){
                    builder.append(buffer);
                }
            }
        }
        String body=builder.toString();
        String rawPath=path;
        if (path.contains(":")) {
            rawPath = path.substring(0, path.indexOf(":"));
        }
        logger.info("Receive an Request: "+method+" "+rawPath + " Origin IP: "+client.getInetAddress().getHostName());
        if (registeredServices.containsKey(rawPath)){
            if (method.equals("GET")){
                boolean success=registeredServices.get(rawPath).doGET(new HttpClient(
                        path,
                        body,
                        client.getOutputStream()
                ));
                if (!success){
                    sendErrorPage(client.getOutputStream());
                }
            } else {
                boolean success=registeredServices.get(rawPath).doPOST(new HttpClient(
                        path,
                        body,
                        client.getOutputStream()
                ));
                if (!success){
                    sendErrorPage(client.getOutputStream());
                }
            }
        } else {
            String resourcePath=KleeBot.config.getResourcePackDir();
            if (path.endsWith("/")){
                path+="index.html";
            }
            if (KleeBot.GET_OS()==0){
                resourcePath=resourcePath+path.replace("/","\\");
            } else {
                resourcePath=resourcePath+path;
            }
            File resource=new File(resourcePath);
            if (resource.exists()){
                ByteArrayOutputStream out=new ByteArrayOutputStream();
                String type=getMIMETypes(resourcePath);
                byte[] content= FileUtils.readFile(resourcePath);
                if (content!=null){
                    int content_length=content.length;
                    out.write(
                            """
                                    HTTP/1.1 200 OK
                                    Content-Type: %s
                                    Server: %s
                                    """.formatted(type,KleeBot.GET_VERSION()).getBytes(StandardCharsets.UTF_8)
                    );
                    out.write("Content-Length: %d\n\n".formatted(content_length).getBytes(StandardCharsets.UTF_8));
                    out.write(content);
                    out.flush();
                    out.close();
                    client.getOutputStream().write(out.toByteArray());
                    client.getOutputStream().flush();
                } else {
                    sendErrorPage(client.getOutputStream());
                }
            } else {
                send404Page(client.getOutputStream());
            }
        }
        client.close();
    }
    private void sendErrorPage(OutputStream output) throws IOException {
        String resourcePath=KleeBot.config.getResourcePackDir();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        String errPage=resourcePath+File.separatorChar+"Error.html";
        byte[] page=FileUtils.readFile(errPage);
        out.write(
                """
                        HTTP/1.1 200 OK
                        Content-Type: %s
                        Server: %s
                        """.formatted("text/html",KleeBot.GET_VERSION()).getBytes(StandardCharsets.UTF_8)
        );
        int content_length=page.length;
        out.write("Content-Length: %d\n\n".formatted(content_length).getBytes(StandardCharsets.UTF_8));
        out.write(page);
        out.flush();
        out.close();
        output.write(out.toByteArray());
        output.flush();
    }
    private void send404Page(OutputStream output) throws IOException {
        String resourcePath=KleeBot.config.getResourcePackDir();
        ByteArrayOutputStream out=new ByteArrayOutputStream();
        out.write(
                """
                        HTTP/1.1 404 Not Found
                        Content-Type: %s
                        Server: %s
                        """.formatted("text/html",KleeBot.GET_VERSION()).getBytes(StandardCharsets.UTF_8)
        );
        String errPage=resourcePath+File.separatorChar+"404.html";
        byte[] page=FileUtils.readFile(errPage);
        int content_length=page.length;
        out.write("Content-Length: %d\n\n".formatted(content_length).getBytes(StandardCharsets.UTF_8));
        out.write(page);
        out.flush();
        out.close();
        output.write(out.toByteArray());
        output.flush();
    }
}
