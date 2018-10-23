package com.alibaba.dubbo.provider.tcp.server;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.registry.EtcdRegistry;
import com.alibaba.dubbo.registry.IRegistry;
import com.alibaba.dubbo.registry.IpHelper;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

public class TcpServer {
	private Logger logger = LoggerFactory.getLogger(TcpServer.class);
	private IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));

	public void start() {
		NioEventLoopGroup bossGroup = new NioEventLoopGroup(1);
		NioEventLoopGroup workerGroup = new NioEventLoopGroup(4);
		ServerBootstrap bootStrap = new ServerBootstrap();
		bootStrap.group(bossGroup, workerGroup)
				.childOption(ChannelOption.SO_KEEPALIVE, true)
				.childOption(ChannelOption.TCP_NODELAY, true)
				.channel(NioServerSocketChannel.class)
				.childHandler(new TcpServerChannelInitializer());
		String port = System.getProperty("tcp.server.port");
		try {
//			SocketAddress socketAddress = new InetSocketAddress(IpHelper.getHostIp(), Integer.parseInt(port));
			SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", Integer.parseInt(port));

			ChannelFuture future = bootStrap.bind(socketAddress).sync();
			logger.info("tcp服务端启动............." + socketAddress.toString());
			future.channel().closeFuture().sync();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			logger.info("tcp服务异常退出.............");
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}

	}
}
