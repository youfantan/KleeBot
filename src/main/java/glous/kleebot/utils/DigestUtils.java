package glous.kleebot.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import glous.kleebot.References;

import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.*;

public class DigestUtils {
    //encrypted data as AES128 algorithm with CBC/PKCS#7
    public static byte[] AES128CBCPKCS7Encrypt(byte[] src){
        try {
            Security.addProvider(new BouncyCastleProvider());
            Cipher cipher=Cipher.getInstance("AES/CBC/PKCS7Padding");
            IvParameterSpec iv=new IvParameterSpec(References.WHISPER_KEY_IV.getBytes(StandardCharsets.UTF_8));
            SecretKey key=new SecretKeySpec(References.WHISPER_KEY_PASSWD.getBytes(StandardCharsets.UTF_8),"AES/CBC/PKCS7Padding");
            cipher.init(Cipher.ENCRYPT_MODE,key,iv);
            byte[] data=cipher.doFinal(src);
            return data;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | InvalidAlgorithmParameterException | IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
            return null;
        }
    }
}
