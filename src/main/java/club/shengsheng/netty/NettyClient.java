package club.shengsheng.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;

import java.util.concurrent.TimeUnit;

/**
 * @author gongxuanzhangmelt@gmail.com
 **/
public class NettyClient {

    public static void main(String[] args) throws InterruptedException {
        String host = "localhost";
        int port = 8080;
        NioEventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .option(ChannelOption.SO_KEEPALIVE, true)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                    }
                });
            ChannelFuture future = bootstrap.connect(host, port).sync();
            group.scheduleAtFixedRate(() -> {
                future.channel().writeAndFlush("hi " + System.currentTimeMillis());
            }, 0, 1000, TimeUnit.MILLISECONDS);
            Channel channel = future.channel();
            channel.closeFuture().sync(); // 等待关闭
        } finally {
            group.shutdownGracefully();
        }
    }
}
