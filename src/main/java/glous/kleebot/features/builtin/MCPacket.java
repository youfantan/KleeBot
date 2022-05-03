package glous.kleebot.features.builtin;

import org.bouncycastle.util.Pack;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class MCPacket {
    private ByteArrayOutputStream out=new ByteArrayOutputStream();
    private DataOutputStream dOut=new DataOutputStream(out);
    int PacketID;
    public void writeInt(int i) throws IOException {
        dOut.writeInt(i);
        dOut.flush();
    }
    public void writeByte(byte b) throws IOException {
        dOut.writeByte(b);
        dOut.flush();
    }
    public void writeVarInt(VariableInt i) throws IOException {
        dOut.write(i.getBytes());
        dOut.flush();
    }
    public void writeVarInt(int i) throws IOException {
        dOut.write(new VariableInt(i).getBytes());
        dOut.flush();
    }
    public void writeBytes(byte[] bytes) throws IOException {
        dOut.write(bytes);
        dOut.flush();
    }
    public void writeShort(short s) throws IOException {
        dOut.writeShort(s);
        dOut.flush();
    }
    public void setPacketID(int b){
        this.PacketID=b;
    }
    public byte[] getPacket() throws IOException {
        ByteArrayOutputStream o=new ByteArrayOutputStream();
        o.write(new VariableInt(1+out.size()).getBytes());
        o.write(PacketID);
        o.write(out.toByteArray());
        o.flush();
        o.close();
        dOut.flush();
        dOut.close();
        return o.toByteArray();
    }
    public static MCPacket getRequestPacket() throws IOException {
        MCPacket packet=new MCPacket();
        packet.setPacketID(0x00);
        return packet;
    }
}
