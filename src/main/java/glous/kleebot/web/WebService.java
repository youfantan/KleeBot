package glous.kleebot.web;

public interface WebService {
    void initialize();
    void response(HttpClient client);
    void shut();
}
