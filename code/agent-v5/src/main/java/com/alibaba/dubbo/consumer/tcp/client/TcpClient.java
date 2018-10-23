package com.alibaba.dubbo.consumer.tcp.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class TcpClient {

	public TcpClient() {
		bootstrap = new Bootstrap().group(eventLoopGroup)
				.option(ChannelOption.SO_KEEPALIVE, true)
				.option(ChannelOption.TCP_NODELAY, true)
				.channel(NioSocketChannel.class)
				.handler(new TcpClientChannelInitializer());
	}

	private EventLoopGroup eventLoopGroup = new NioEventLoopGroup(4);

	private Bootstrap bootstrap;

	private Channel channel_small;
	private Channel channel_mid;
	private Channel channel_large;

	private Object lock = new Object();

	public Channel getChannel(String host, int port) throws Exception {
		if ("10.10.10.3".equals(host)) {
			if (null != channel_small) {
				return channel_small;
			}
			synchronized (lock) {
				channel_small = bootstrap.connect(host, port).sync().channel();
			}
			return channel_small;

		} else if ("10.10.10.4".equals(host)) {
			if (null != channel_mid) {
				return channel_mid;
			}
			synchronized (lock) {
				channel_mid = bootstrap.connect(host, port).sync().channel();
			}

			return channel_mid;
		} else {
			if (null != channel_large) {
				return channel_large;
			}
			synchronized (lock) {
				channel_large = bootstrap.connect(host, port).sync().channel();
			}
			return channel_large;
		}
	}

}
