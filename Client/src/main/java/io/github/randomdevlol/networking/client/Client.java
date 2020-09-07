package io.github.randomdevlol.networking.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.logging.Logger;

public class Client {

    private static Logger logger;

    public static void main(String[] args) {
        Client.logger = Logger.getLogger("Client");

        OptionParser parser = new OptionParser() {
            {
                accepts("host")
                        .withRequiredArg()
                        .ofType(String.class)
                        .defaultsTo("localhost")
                        .describedAs("The hostname or ip of the server to connect to");

                accepts("port")
                        .withRequiredArg()
                        .ofType(Integer.class)
                        .defaultsTo(1212)
                        .describedAs("The port of the server to connect to");
            }
        };

        OptionSet optionSet = parser.parse(args);
        new Client(optionSet);
    }

    private String host;
    private int port;

    private Client(OptionSet optionSet) {
        this.host = optionSet.valueOf("host").toString();
        this.port = Integer.parseInt(optionSet.valueOf("port").toString());

        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            Bootstrap bootstrap = new Bootstrap()
                    .group(workerGroup)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) {
                            socketChannel.pipeline().addLast(new ClientHandler());
                        }
                    });

            ChannelFuture future = bootstrap.connect(host, port).sync();
            System.out.println("Connected to " + host + ":" + port);

            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }

}
