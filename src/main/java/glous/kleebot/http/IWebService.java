package glous.kleebot.http;

import java.io.IOException;

public interface IWebService {
    boolean doGET(HttpClient client) throws IOException;
    boolean doPOST(HttpClient client) throws IOException;
    void init();
    void stop();
}
