package glous.kleebot.utils;


import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

public class FileUtils {
    private static int DEBUG_PORT;
    public static void enableDebug(int port){
        DEBUG_PORT=port;
    }
    public static byte[] readFile(String fileName) throws IOException {
        BufferedInputStream in = new BufferedInputStream(new FileInputStream(fileName));
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int bytesRead;
        byte[] buffer = new byte[1024];
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.close();
        in.close();
        return out.toByteArray();
    }
    public static String readStream(InputStream stream){
        try {
            BufferedReader reader=new BufferedReader(new InputStreamReader(stream));
            String line;
            StringBuilder builder=new StringBuilder();
            while ((line=reader.readLine())!=null){
                builder.append(line);
            }
            reader.close();
            return builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static String readFile(String fileName, Charset charset) throws IOException {
        byte[] bytes = readFile(fileName);
        if (bytes == null) {
            return null;
        }
        return new String(bytes, charset);
    }

    public static void writeFile(String fileName, byte[] content) throws IOException {
        BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(fileName));
        out.write(content);
        out.flush();
        out.close();
    }

    public static void writeFile(String fileName, String content) throws IOException {
        writeFile(fileName, content.getBytes(StandardCharsets.UTF_8));
    }
    public static byte[] download(String _url, Proxy proxy, Map<String,String> extraHeaders) {
        try {
            URL url = new URL(_url);
            HttpURLConnection conn;
            if (proxy != null) {
                conn = (HttpURLConnection) url.openConnection(proxy);
            } else if (DEBUG_PORT!=0){
                trustAllHosts();
                conn = (HttpURLConnection) url.openConnection(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("localhost",DEBUG_PORT)));
            }
            else {
                conn = (HttpURLConnection) url.openConnection();
            }
            if (extraHeaders!=null){
                for (String k :
                        extraHeaders.keySet()) {
                    conn.setRequestProperty(k, extraHeaders.get(k));
                }
            }
            BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
            int bytesRead;
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();
            return out.toByteArray();
        } catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    public static byte[] download(String _url) throws IOException {
        return download(_url, null,null);
    }
    public static byte[] download(String _url,Proxy proxy) throws IOException {
        return download(_url,proxy,null);
    }
    private static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[]{};
            }

            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }
        }};
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
