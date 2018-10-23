package com.alibaba.dubbo.consumer.http.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class HttpServer {
	private Logger logger = LoggerFactory.getLogger(HttpServer.class);

	public void start() {
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workGroup = new NioEventLoopGroup(4);
		ServerBootstrap bootstrap = new ServerBootstrap();
		bootstrap.group(bossGroup, workGroup)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.channel(NioServerSocketChannel.class)
				.childHandler(new HttpServerChannelInitializer());
		String port = System.getProperty("http.server.port");
		SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", Integer.parseInt(port));
		try {
			ChannelFuture future = bootstrap.bind(socketAddress).sync();
			logger.info("http服务端启动............." + socketAddress.toString());
			future.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} finally {
			logger.info("http服务异常退出.............");
			bossGroup.shutdownGracefully();
			workGroup.shutdownGracefully();
		}
	}
}
