package com.tt.bigcount.server;

import com.tt.ocean.net.Handler;
import com.tt.ocean.node.Node;
import com.tt.ocean.node.route.RouteForNode;
import com.tt.ocean.proto.OceanProto;
import com.tt.ocean.route.Route;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ConfigurationException;

public class Server extends Node {

    private static final Logger log = LogManager.getLogger(Server.class.getName());

    EventLoopGroup bossGroup = null;
    EventLoopGroup workerGroup = null;

    int __port = 0;


    public Server(int pieceID, String ip, int port) throws ConfigurationException, IllegalArgumentException {
        super("server", "server", pieceID, ip, port);
        this.__port = port;
    }

    public void start() throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup(1);


        init(workerGroup);
        Route route = new RouteForNode(this);


        ServerBootstrap b = new ServerBootstrap();
        b.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .handler(new LoggingHandler(LogLevel.INFO))
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline p = socketChannel.pipeline();


                        p.addLast(new ProtobufVarint32FrameDecoder());
                        p.addLast(new ProtobufDecoder(OceanProto.OceanMessage.getDefaultInstance()));

                        p.addLast(new ProtobufVarint32LengthFieldPrepender());
                        p.addLast(new ProtobufEncoder());

                        Handler handler = new Handler();
                        handler.setRoute(route);
                        p.addLast(handler);
                    }
                });

        b.bind(__port).sync().channel().closeFuture().sync();


    }

    public void stop() {
        if(bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if(workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
