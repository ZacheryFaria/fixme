package zfaria.fixme.core.notation;

import io.netty.buffer.ByteBuf;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Fix {

    private FixTag version;
    private FixTag size;
    private FixTag msg;
    private FixTag sum;
    private List<FixTag> tags;
    private byte[] buffer;

    public Fix(String msgType) {
        tags = new ArrayList<>();
        version = new FixTag(FixTag.VERSION, "FIX.4.2");
        msg = new FixTag(FixTag.MSG_TYPE, msgType);
        size = calcSize();
    }

    public Fix(byte[] buffer) {
        this.buffer = buffer;
        List<String> list = new ArrayList<>();
        StringBuilder builder = new StringBuilder();
        for (byte b : buffer) {
            if (b == 1) {
                list.add(builder.toString());
                builder.setLength(0);
            } else {
                builder.append((char)b);
            }
        }
        tags = new ArrayList<>(list.size());
        for (String s : list) {
            try {
                FixTag tag = new FixTag(s);
                if (version == null)
                    version = tag;
                else if (size == null)
                    size = tag;
                else if (msg == null)
                    msg = tag;
                else if (!tag.getType().equals(FixTag.SUM_TYPE))
                    tags.add(tag);
            } catch (NoSuchObjectException e) {
            }
        }
    }

    public void addTag(FixTag tag) {
        this.tags.add(tag);
    }

    public FixTag getTag(String type) {
        for (FixTag tag : tags) {
            if (tag.getType().equals(type)) {
                return tag;
            }
        }
        return null;
    }

    /**
     * Returns length of message string, used for the checksum.
     * Does not include version, message, or checksum itself in the length.
     * @return
     */
    public FixTag calcSize() {
        int len = 0;
        len += msg.getSize() + 1;
        for (FixTag tag : tags) {
            len += tag.getSize() + 1;
        }
        return new FixTag("9", "" + len);
    }

    /**
     * Returns the total length of the message, used for transit integrity.
     * @return
     */
    public int getTotalLength() {
        sum = new FixTag(FixTag.SUM_TYPE, String.format("%03d", calcCheckSum()));
        int size = 0;
        size += version.getSize() + 1;
        size += msg.getSize() + 1;
        size += this.size.getSize() + 1;
        for (FixTag tag : tags) {
            size += tag.getSize() + 1;
        }
        size += sum.getSize() + 1;
        return size;
    }

    /**
     * Calculates the checksum value postfixed to the message. Equal to the ASCII sum of all
     * characters in the string, mod 256.
     * @return
     */
    public int calcCheckSum() {
        int i = 0;
        i += version.getSum();
        i += msg.getSum();
        i += size.getSum();
        for (FixTag tag : tags) {
            i += tag.getSum();
        }
        return i % 256;
    }


    public byte[] serialize() {
        size = calcSize();
        byte[] buf = new byte[getTotalLength()];
        int len = 0;
        for (byte b : version.encode()) {
            buf[len++] = b;
        }
        buf[len++] = (byte)1;
        for (byte b : size.encode()) {
            buf[len++] = b;
        }
        buf[len++] = (byte)1;
        for (byte b : msg.encode()) {
            buf[len++] = b;
        }
        buf[len++] = (byte)1;
        for (FixTag tag : tags) {
            for (byte b : tag.encode()) {
                buf[len++] = b;
            }
            buf[len++] = (byte)1;
        }
        for (byte b : sum.encode()) {
            buf[len++] = b;
        }
        buf[len++] = (byte)1;
        return buf;
    }

    public String toString() {
        byte[] buf = serialize();
        StringBuilder sb = new StringBuilder(buf.length);
        for (byte b : buf) {
            if (b == 1)
                sb.append('|');
            else
                sb.append((char)b);
        }
        return sb.toString();
    }

    public String getMessageType() {
        return msg.getValue();
    }
}
