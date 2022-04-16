package shandiankulishe.kleebot.tests;

import glous.kleebot.utils.FileUtils;
import org.jetbrains.skija.*;
import org.jetbrains.skija.Canvas;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SkijaTest {
    @Test
    public void testShow() throws IOException {
        byte[]bytes=FileUtils.readFile("background.jpg");
        long start=System.currentTimeMillis();
        Surface surface=Surface.makeRasterN32Premul(7340,4200);
        Canvas canvas=surface.getCanvas();
        canvas.drawImage(Image.makeFromEncoded(bytes),0,0);
        Image image=surface.makeImageSnapshot();
        Data pngData=image.encodeToData(EncodedImageFormat.JPEG);
        ByteBuffer byteBuf=pngData.toByteBuffer();
        Path path=Path.of("out.jpg");
        ByteChannel channel=Files.newByteChannel(path,StandardOpenOption.CREATE,StandardOpenOption.TRUNCATE_EXISTING,StandardOpenOption.WRITE);
        channel.write(byteBuf);
        channel.close();
        Long end=System.currentTimeMillis();
        System.out.println(end-start);
    }
}
