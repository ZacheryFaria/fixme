package zfaria.fixme.core.notation;

import java.rmi.NoSuchObjectException;

public class FixTag {

    private String type;
    private String value;

    public static final String MSG_TYPE = "35";
    public static final String MSG_NEW_ORDER = "D";
    public static final String MSG_EXEC_REP = "8";
    public static final String MSG_SES_REJ = "3";
    public static final String STATUS_TYPE = "39";
    public static final String STATUS_ACKNOWLEDGE = "0";
    public static final String STATUS_REJ = "8";
    public static final String STATUS_PARTIAL = "1";
    public static final String STATUS_COMPLETE = "2";
    public static final String PURCH_TYPE = "54";
    public static final String PURCH_BUY = "1";
    public static final String PURCH_SELL = "2";
    public static final String ORDERQTY_TYPE = "38";
    public static final String PRICE_TYPE = "44";
    public static final String ORDER_TYPE = "40";
    public static final String ORDER_MARKET = "1";
    public static final String ORDER_LIMIT = "2";
    public static final String ROUTING_SENDER_ID = "50";
    public static final String ROUTING_COMPANY_ID = "49";
    public static final String ROUTING_RECEIVER_ID = "56";
    public static final String SYMBOL_TYPE = "55";
    public static final String SUM_TYPE = "10";

    public FixTag(String type, String value) {
        this.type = type;
        this.value = value;
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
