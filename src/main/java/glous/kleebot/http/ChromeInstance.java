package glous.kleebot.http;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

public class ChromeInstance {
    private static ChromeDriver instance;
    public static void initialize(){
        ChromeOptions options=new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=2160,1380");
        options.addArguments("--proxy-server=http://127.0.0.1:7890");
        instance=new ChromeDriver(options);
        instance.manage().window().maximize();
    }
    public static void stop(){
        if (instance!=null){
            instance.quit();
        }
    }
    public static synchronized byte[] getScreenShot(String _url,int delay,long scroll) throws InterruptedException {
        instance.get(_url);
        Thread.sleep(delay);
        //instance.executeScript("document.body.style.zoom='1.5'");
        instance.executeScript("window.scrollTo(0,%d)".formatted(scroll));
        byte[] bytes=instance.getScreenshotAs(OutputType.BYTES);
        return bytes;
    }
}
