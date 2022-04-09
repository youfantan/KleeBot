package glous.kleebot.utils.builtin;

public class HttpResponse {
    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(int responseCode) {
        this.responseCode = responseCode;
    }

    public HttpResponse(byte[] body, int responseCode) {
        this.body = body;
        this.responseCode = responseCode;
    }
    public HttpResponse(){}

    private byte[] body;
    private int responseCode;
}
