package zfaria.fixme.core.notation;

import io.netty.buffer.ByteBuf;

public class FixSerializer {

    public static Fix deserialize(ByteBuf buf) {
        byte[] bytes = new byte[buf.readableBytes()];
        buf.readBytes(bytes);
        return new Fix(bytes);
    }

    public static Fix deserialize(Object o) {
        return deserialize((ByteBuf) o);
    }

}
