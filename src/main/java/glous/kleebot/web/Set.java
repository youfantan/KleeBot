package glous.kleebot.web;

public class Set {
    private String K;
    private String V;
    public Set(String K,String V){
        this.K=K;
        this.V=V;
    }

    public String getK() {
        return K;
    }

    public String getV() {
        return V;
    }

    public void setK(String k) {
        K = k;
    }

    public void setV(String v) {
        V = v;
    }
}
