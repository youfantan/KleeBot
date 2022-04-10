package glous.kleebot.http;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;

import java.io.File;

public class ChromeInstance {
    private static ChromeDriver instance;
    public static void initialize(){
        ChromeOptions options=new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--disable-gpu");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=2160,1380");
        instance=new ChromeDriver(options);
        instance.manage().window().maximize();
    }
    public static void stop(){
        if (instance!=null){
            instance.close();
            instance.quit();
        }
    }
    public static synchronized byte[] getScreenShot(String _url,int delay,long scroll) throws InterruptedException {
        instance.get(_url);
        Thread.sleep(delay);
        instance.executeScript("window.scrollTo(0,%d)".formatted(scroll));
        byte[] bytes=instance.getScreenshotAs(OutputType.BYTES);
        instance.get("about:blank");
        return bytes;
    }
}
