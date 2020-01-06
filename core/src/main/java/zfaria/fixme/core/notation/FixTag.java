package zfaria.fixme.core.notation;

import java.rmi.NoSuchObjectException;

public class FixTag {

    private String type;
    private String value;

    public static final String VERSION = "8";

    public static final String MSG_TYPE = "35";
    public static final String MSG_CONNECT = "A";
    public static final String MSG_NEW_ORDER = "D";
    public static final String MSG_QUOTE_REQUEST = "R";
    public static final String MSG_QUOTE = "S";

    /*
     * Returns list of available securities from a market
     * Non-standard
     */
    public static final String MSG_AVAIL_SEC = "Z";

    // Returns a list of available markets for brokers to contact, DESTINATION_ID should be 0
    public static final String MSG_AVAIL_MARK = "Y";

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

    // Self implemented. Refers to the given id of the broker / router.
    // Not part of official FIX protocol
    public static final String CONNECT_ID = "500";

    public FixTag(String type, String value) {
        this.type = type;
        this.value = value;
    }

    public FixTag(String type, int value) {
        this(type, Integer.toString(value));
    }

    public FixTag(String type, Object value) {
        this(type, value.toString());
    }

    public FixTag(String encoded) throws NoSuchObjectException {
        if (encoded.contains("=")) {
            String s[] = encoded.split("=");
            this.type = s[0];
            this.value = s[1];
        } else {
            throw new NoSuchObjectException(encoded);
        }
    }

    public String getType() {
        return type;
    }

    public String getValue() {
        return value;
    }

    public byte[] encode() {
        byte[] buf = new byte[type.length() + value.length() + 1];
        int len = 0;
        for (byte b : type.getBytes()) {
            buf[len++] = b;
        }
        buf[len++] = '=';
        for (byte b : value.getBytes()) {
            buf[len++] = b;
        }
        return buf;
    }

    public int getSize() {
        return type.length() + value.length() + 1;
    }

    public int getSum() {
        byte[] buf = encode();
        int sum = 0;
        for (byte b : buf) {
            sum += b;
        }
        return sum + 1;
    }

}
