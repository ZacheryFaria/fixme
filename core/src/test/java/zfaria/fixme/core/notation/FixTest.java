package zfaria.fixme.core.notation;

import org.junit.Test;

import static org.junit.Assert.*;

public class FixTest {

    public static String asString(byte[] buf) {
        StringBuilder builder = new StringBuilder(buf.length);
        for (byte b : buf) {
            if (b == 1) {
                builder.append("|");
            } else {
                builder.append((char)b);
            }
        }
        return builder.toString();
    }

    @Test
    public void calcSizeTest() {
        String s = "8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|";
        s = s.replace('|', (char)1);
        Fix f = new Fix(s.getBytes());

        assertEquals("65", f.calcSize().getValue());

        Fix f2 = new Fix(FixTag.MSG_CONNECT);
        f2.addTag(new FixTag(FixTag.CONNECT_ID, 100000 + ""));

        assertEquals("16", f2.calcSize().getValue());
    }

    @Test
    public void serializeTest() {
        String os = "8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|";
        String s = os.replace('|', (char)1);
        Fix f = new Fix(s.getBytes());
        byte[] buff = f.serialize();

        Fix ff = new Fix(buff);
        byte[] buff2 = ff.serialize();
        assertArrayEquals(f.serialize(), ff.serialize());

        Fix fff = new Fix(buff2);
        byte[] buff3 = fff.serialize();
        assertArrayEquals(buff, buff3);

        assertEquals(os, asString(buff));
    }

    @Test
    public void getTagTest() {
        Fix f = new Fix(FixTag.MSG_NEW_ORDER);
        FixTag ft = new FixTag(FixTag.SIDE, FixTag.SIDE_BUY);
        f.addTag(ft);


        assertEquals(ft, f.getTag(FixTag.SIDE));
    }

    @Test
    public void checksumTest() {
        String os = "8=FIX.4.2|9=65|35=A|49=SERVER|56=CLIENT|34=177|52=20090107-18:15:16|98=0|108=30|10=062|";
        String s = os.replace('|', (char)1);
        Fix f = new Fix(s.getBytes());
        assertEquals(62, f.calcCheckSum());
    }


}