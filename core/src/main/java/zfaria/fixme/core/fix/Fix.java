package zfaria.fixme.core.fix;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Fix {

    public static final String VERSION = "8";

    public static final String MSG_TYPE = "35";
    public static final String MSG_CONNECT = "A";
    public static final String MSG_NEW_ORDER = "D";
    public static final String MSG_NO_DESTINATION = "d";

    public static final String SENDER_ID = "49";
    public static final String DESTINATION_ID = "56";

    // Side refers how the funds/securities are moving, buy/sell are implemented.
    public static final String SIDE = "54";
    public static final String SIDE_BUY = "1";
    public static final String SIDE_SELL = "2";

    public static final String ORDSTATUS = "39";
    public static final String ORDSTATUS_ACKNOWLEDGE = "0";
    public static final String ORDSTATUS_REJECTED = "8";
    public static final String ORDSTATUS_PARTIAL = "1";
    public static final String ORDSTATUS_COMPLETE = "2";

    public static final String ORDERQTY = "38";

    public static final String PRICE = "44";

    public static final String SYMBOL = "55";

    public static final String SUM_TYPE = "10";

    public static final String SIZE = "9";

    // Non-standard
    // Amount of funds broker has, used buying
    public static final String FUNDS = "499";

    // Self implemented. Refers to the given id of the broker / router.
    // Not part of official FIX protocol
    public static final String CONNECT_ID = "500";

    private Map<String, String> tag = new LinkedHashMap<>();
    private byte[] buffer;

    public Fix(String msgType) {
        tag.put(VERSION, "FIX.4.2");

        tag.put(SIZE, calcSize());

        tag.put(MSG_TYPE, msgType);
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
        for (String s : list) {
            String kv[] = s.split("=");
            tag.put(kv[0], kv[1]);
        }
        tag.remove(SUM_TYPE);
    }

    public void addTag(String key, String value) {
        tag.put(key, value);
    }

    public void addTag(String key, Object o) {
        tag.put(key, o.toString());
    }

    public String getTag(String type) {
        return tag.get(type);
    }

    /**
     * Returns length of message string, used for the checksum.
     * Does not include version, message, or checksum itself in the length.
     * @return
     */
    public String calcSize() {
        int len = 0;
        for (Map.Entry<String, String> e : tag.entrySet()) {
            if (e.getKey().equals(VERSION) || e.getKey().equals(SIZE)) {
                continue;
            }
            len += getSize(e.getKey(), e.getValue());
        }
        return Integer.toString(len);
    }

    /**
     * Returns the total length of the message, used for transit integrity.
     * @return
     */
    public int getTotalLength() {
        int size = 0;

        for (Map.Entry<String, String> e : tag.entrySet()) {
            size += getSize(e.getKey(), e.getValue());
        }
        size += 6 + 1;
        return size;
    }

    /**
     * Calculates the checksum value postfixed to the message. Equal to the ASCII sum of all
     * characters in the string, mod 256.
     * @return
     */
    public int calcCheckSum() {
        int sum = 0;
        for (Map.Entry<String, String> e : tag.entrySet()) {
            if (e.getKey().equals(SUM_TYPE)) {
                continue;
            }
            for (byte b : e.getKey().getBytes()) {
                sum += b;
            }
            for (byte b : e.getValue().getBytes()) {
                sum += b;
            }
            sum += 1;
            sum += '=';
        }

        return sum % 256;
    }

    public byte[] serialize() {
        tag.put(SIZE, calcSize());
        byte[] buf = new byte[getTotalLength()];
        int len = 0;
        for (Map.Entry<String, String> e : tag.entrySet()) {
            String s = String.format("%s=%s", e.getKey(), e.getValue());
            for (byte b : s.getBytes()) {
                buf[len++] = b;
            }
            buf[len++] = (byte)1;
        }
        String sum = String.format("%s=%03d", SUM_TYPE, calcCheckSum());
        for (byte b : sum.getBytes()) {
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

    public int getSize(String k, String v) {
        return k.length() + v.length() + 2;
    }
}
