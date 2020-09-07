package io.github.randomdevlol.networking.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.Date;

public class ClientHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf in = (ByteBuf) msg;

        try {
            long currentMillis = (in.readUnsignedInt() - 2208988800L) * 1000;
            System.out.println(new Date(currentMillis));
            ctx.close();
        } finally {
            in.release();
        }
    }
}
