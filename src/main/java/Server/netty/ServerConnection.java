/*
 * Decompiled with CFR 0.152.
 */
package Server.netty;

import Server.ServerType;
import Server.netty.ServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class ServerConnection {
    private final int port;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();
    private final ServerType type;
    private int world = -1;
    private int channels = -1;
    private ServerBootstrap boot;
    private Channel channel;

    public ServerConnection(int port, int world, int channels, ServerType type) {
        this.port = port;
        this.world = world;
        this.channels = channels;
        this.type = type;
    }

    public void run() {
        try {
            this.boot = ((ServerBootstrap)((ServerBootstrap)new ServerBootstrap().group(this.bossGroup, this.workerGroup).channel(NioServerSocketChannel.class)).option(ChannelOption.SO_BACKLOG, 1000)).childOption(ChannelOption.TCP_NODELAY, true).childOption(ChannelOption.SO_KEEPALIVE, true).childHandler(new ServerInitializer(this.world, this.channels, this.type));
            this.channel = this.boot.bind(this.port).sync().channel().closeFuture().channel();
        }
        catch (Exception e) {
            throw new RuntimeException("啟動失敗 - " + this.type.name() + ":" + String.valueOf(this.channel.remoteAddress()));
        }
    }

    public void close() {
        this.channel.close();
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
    }
}

