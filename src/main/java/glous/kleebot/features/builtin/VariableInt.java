package glous.kleebot.features.builtin;

import glous.kleebot.annonations.Copy;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.Vector;

public class VariableInt {
    private static final int SEGMENT_BITS = 0x7F;
    private static final int CONTINUE_BIT = 0x80;
    int i;
    public VariableInt(int i){
        this.i=i;
    }
    public VariableInt(){
    }

    @Copy(url = "https://wiki.vg/Protocol")
    public byte[] getBytes() {
        ByteArrayOutputStream o=new ByteArrayOutputStream();
        while (true) {
            if ((i & ~SEGMENT_BITS) == 0) {
                o.write((byte) i);
                return o.toByteArray();
            }
            o.write((byte) (i & SEGMENT_BITS) | CONTINUE_BIT);
            i >>>= 7;
        }
    }

    public void writeBytes(OutputStream out) throws IOException {
        out.write(getBytes());
    }

    @Copy(url = "https://wiki.vg/Protocol")
    public int readBytes(byte[] bytes) throws IOException {
        ByteArrayInputStream in=new ByteArrayInputStream(bytes);
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte =(byte) in.read();
            value |= (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }
        this.i=value;
        return i;
    }

    @Copy(url = "https://wiki.vg/Protocol")
    public int readBytes(InputStream in) throws IOException {
        int value = 0;
        int position = 0;
        byte currentByte;
        while (true) {
            currentByte =(byte) in.read();
            value |= (currentByte & SEGMENT_BITS) << position;
            if ((currentByte & CONTINUE_BIT) == 0) break;
            position += 7;
            if (position >= 32) throw new RuntimeException("VarInt is too big");
        }
        this.i=value;
        return i;
    }
}
