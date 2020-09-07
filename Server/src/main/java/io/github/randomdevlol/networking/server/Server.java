package io.github.randomdevlol.networking.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import joptsimple.OptionParser;
import joptsimple.OptionSet;

import java.util.logging.Logger;

public class Server {

    public static Logger logger;

    public static void main(String[] args) {
        Server.logger = Logger.getLogger("Client");

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
        new Server(optionSet);
    }

    private String host;
    private int port;

    private Server(OptionSet optionSet) {
        this.host = optionSet.valueOf("host").toString();
        this.port = Integer.parseInt(optionSet.valueOf("port").toString());

        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

         try {
             ServerBootstrap bootstrap = new ServerBootstrap();
             bootstrap.group(bossGroup, workerGroup)
                     .channel(NioServerSocketChannel.class)
                     .childHandler(new ChannelInitializer<SocketChannel>() {
                         @Override
                         public void initChannel(SocketChannel ch) {
                             ch.pipeline().addLast(new ServerHandler());
                         }
                     })
                     .option(ChannelOption.SO_BACKLOG, 128)
                     .childOption(ChannelOption.SO_KEEPALIVE, true);

             ChannelFuture future = bootstrap.bind(host, port).sync();

             System.out.println("Listening on " + host + ":" + port);

             future.channel().closeFuture().sync();
         } catch (InterruptedException e) {
             e.printStackTrace();
         } finally {
             bossGroup.shutdownGracefully();
             workerGroup.shutdownGracefully();
         }
    }

}
