package zfaria.fixme.router;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import zfaria.fixme.core.net.FixMessageEncoder;

import java.net.InetSocketAddress;

public class RouterBootstrap {

    private int brokerPort = 5000;
    private int marketPort = 5001;
    private String hostname = "localhost";

    private Thread brokerThread;
    private Thread marketThread;

    public RouterBootstrap() {
        brokerThread = new Thread(() -> startBootstrap(brokerPort));
        marketThread = new Thread(() -> startBootstrap(marketPort));
        brokerThread.start();
        marketThread.start();
    }

    private void startBootstrap(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup);
            b.channel(NioServerSocketChannel.class);
            b.localAddress(new InetSocketAddress(hostname, port));
            b.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) {
                    ch.pipeline().addLast(new FixMessageEncoder());
                    ch.pipeline().addLast(new FixChannelHandler(port));
                }
            });
            b.childOption(ChannelOption.TCP_NODELAY, true);
            b.childOption(ChannelOption.SO_KEEPALIVE, true);

            ChannelFuture f = b.bind().sync();

            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
        }
    }
}