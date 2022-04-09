package glous.kleebot.utils;

import org.apache.commons.compress.archivers.ArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;

import java.io.*;

public class ZipUtils {
    public static void extractZipFile(String filePath,String extractPath){
        if (!extractPath.endsWith(String.valueOf(File.separatorChar))){
            extractPath+=File.separatorChar;
        }
        File destDir=new File(extractPath);
        if (!destDir.exists()){
            destDir.mkdirs();
        }
        try {
            File file=new File(filePath);
            ZipArchiveInputStream zipStream=new ZipArchiveInputStream(new FileInputStream(file));
            ArchiveEntry entry;
            while ((entry=zipStream.getNextEntry())!=null) {
                String entryName = entry.getName();
                entryName = extractPath+ entryName;
                if (entry.isDirectory()) {
                    File dir = new File(entryName);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                } else {
                    File destFile = new File(entryName);
                    if (!destFile.getParentFile().exists()) {
                        destFile.getParentFile().mkdirs();
                    }
                    if (!destFile.exists()) {
                        destFile.createNewFile();
                    }
                    BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(destFile));
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = zipStream.read(buffer)) != -1) {
                        out.write(buffer, 0, len);
                        out.flush();
                    }
                    out.close();
                }
            }
            zipStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
