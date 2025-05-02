package club.shengsheng.netty;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class NettyServer {

    public static void main(String[] args) {
        Map<Channel, List<String>> db = new ConcurrentHashMap<>();
        ServerBootstrap serverBootstrap = new ServerBootstrap().group(new NioEventLoopGroup(), new NioEventLoopGroup())
            .channel(NioServerSocketChannel.class)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new LineBasedFrameDecoder(1024))
                        .addLast(new StringDecoder())
                        .addLast(new StringEncoder())
                        .addLast(new ResponseHandler())
                        .addLast(new DbHandler(db));
                }
            });
        ChannelFuture bindFuture = serverBootstrap.bind(8080);
        bindFuture.addListener(f -> {
            if (f.isSuccess()) {
                System.out.println("服务器成功监听端口" + 8080);
            } else {
                System.out.println("服务器监听端口失败");
            }
        });
    }

    static class ResponseHandler extends SimpleChannelInboundHandler<String> {
        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            System.out.println(msg);
            String message = msg + " world\n";
            ctx.channel().writeAndFlush(message);
            ctx.fireChannelRead(msg);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() + "注册了");
            ctx.fireChannelRegistered();
        }
    }

    static class DbHandler extends SimpleChannelInboundHandler<String> {
        private Map<Channel, List<String>> db;

        public DbHandler(Map<Channel, List<String>> db) {
            this.db = db;
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {
            List<String> messageList = db.computeIfAbsent(ctx.channel(), k -> new ArrayList<>());
            messageList.add(msg);
        }

        @Override
        public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() + "注册了");
        }

        @Override
        public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() + "解除注册了");
        }

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            System.out.println(ctx.channel() + "可以使用了");
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            List<String> strings = db.get(ctx.channel());
            System.out.println(strings);
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        }
    }

}
