package com.example.io.netty.simple;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.CharsetUtil;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;

/**
 * @description: Netty服务器端
 * @author: JiangRuiLi
 * @date: 2020/6/13
 */
public class NettyServer {

    public static void main(String[] args) throws InterruptedException {

        //创建两个线程组：BossGroup和WorkerGroup
        NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
        NioEventLoopGroup workerGroup = new NioEventLoopGroup(3);

        try {
            //创建服务器端启动对象，配置参数
            ServerBootstrap bootstrap = new ServerBootstrap();

            //链式设置参数
            bootstrap.group(bossGroup, workerGroup);
            //设置NioServerSocketChannel作为通道实现
            bootstrap.channel(NioServerSocketChannel.class);
            //设置线程队列得到的线程个数
            bootstrap.option(ChannelOption.SO_BACKLOG, 128);
            //设置保持活动连接状态
            bootstrap.childOption(ChannelOption.SO_KEEPALIVE, true);
            //创建workderGroup的 EventLoop对应的管道设置处理器
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                //给PipeLine设置处理器
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();
                    pipeline.addLast(new NettyServerHandler());
                }
            });

            System.out.println("服务器端口号：6666");
            ChannelFuture future = bootstrap.bind(6666).sync();

            //异步模型--对关闭的通道进行监听
            future.channel().closeFuture().sync();

        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

/**
 *
 *
 */
class NettyServerHandler extends ChannelInboundHandlerAdapter{

    /**
     *
     * 读取客户端发送的消息
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
//            super.channelRead(ctx, msg);
        System.out.println(Thread.currentThread().getName());
        System.out.println("Server ctx = " + ctx);
        // 将msg转成ByteBuffer(Netty对象）
        ByteBuf buf = (ByteBuf) msg;
        System.out.println("客户端发送的消息是："+ buf.toString(CharsetUtil.UTF_8));
        System.out.println("客户端地址：" + ctx.channel().remoteAddress());

    }

    /**
     *
     * 数据读取完毕
     */
    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
//            super.channelReadComplete(ctx);

        //写入缓冲并刷新
        String str = "你好，我是服务器";
        ctx.writeAndFlush(Unpooled.copiedBuffer(str, CharsetUtil.UTF_8));

    }

    /**
     *
     * 发生异常，关闭通道
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
//            super.exceptionCaught(ctx, cause);
        ctx.channel().close();
    }
}