package zfaria.fixme.core.notation;

import java.rmi.NoSuchObjectException;
import java.util.ArrayList;
import java.util.List;

public class Fix {

    private FixTag version;
    private FixTag size;
    private FixTag msg;
    private FixTag sum;
    private List<FixTag> tags;
    private byte[] buffer;

    public Fix(String msgType) {
        version = new FixTag("8", "FIX.4.2");
        msg = new FixTag("35", msgType);
        tags = new ArrayList<>();
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
                    tags.add(new FixTag(s));
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

    public FixTag calcSize() {
        int len = 0;
        len += msg.getSize() + 1;
        for (FixTag tag : tags) {
            len += tag.getSize() + 1;
        }
        return new FixTag("9", "" + len);
    }

    public int getTotalLength() {
        sum = new FixTag(FixTag.SUM_TYPE, String.format("%03d", calcSum()));
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

    public int calcSum() {
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
}
