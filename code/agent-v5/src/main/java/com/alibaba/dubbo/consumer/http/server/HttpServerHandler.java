package com.alibaba.dubbo.consumer.http.server;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.consumer.tcp.client.TcpClient;
import com.alibaba.dubbo.holder.HttpChannelHolder;
import com.alibaba.dubbo.proto.TcpRequestProto;
import com.alibaba.dubbo.proto.TcpRequestProto.TcpRequest.Builder;
import com.alibaba.dubbo.registry.Endpoint;
import com.alibaba.dubbo.registry.EtcdRegistry;
import com.alibaba.dubbo.registry.IRegistry;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.multipart.Attribute;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

	private Logger logger = LoggerFactory.getLogger(HttpServerHandler.class);

	private static HttpDataFactory factory = new DefaultHttpDataFactory();
	private static final int[] ENDPOINT_ARRAY = { 0, 1, 1, 2, 2, 2, 0, 1, 1, 2, 2, 2 };
	private static List<Endpoint> endpoints = null;
	private static IRegistry registry = new EtcdRegistry(System.getProperty("etcd.url"));
	private static Object lock = new Object();
	private static TcpClient tcpClient = new TcpClient();
	private static Random random = new Random();
	private static AtomicLong atomicLong = new AtomicLong();

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		FullHttpRequest fullRequest = (FullHttpRequest) msg;
		HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(factory, fullRequest);
		try {
			Builder newBuilder = TcpRequestProto.TcpRequest.newBuilder();
			long id = atomicLong.getAndIncrement();
			newBuilder.setId(id);
			Attribute parameter = (Attribute) decoder.getBodyHttpData("parameter");
			newBuilder.setParameter(parameter.getValue());
			com.alibaba.dubbo.proto.TcpRequestProto.TcpRequest tcpRequest = newBuilder.build();
			HttpChannelHolder.putHttpChannel(String.valueOf(id), ctx.channel());
			fullRequest.release();
			decoder.destroy();
			if (null == endpoints) {
				synchronized (lock) {
					if (null == endpoints) {
						endpoints = registry.find("com.alibaba.dubbo.performance.demo.provider.IHelloService");
					}
				}
			}
			Endpoint endpoint = endpoints.get(ENDPOINT_ARRAY[random.nextInt(6)]);
			Channel channel = tcpClient.getChannel(endpoint.getHost(), endpoint.getPort());
			channel.writeAndFlush(tcpRequest);
		} catch (Exception e) {
			logger.error(e.toString());
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(cause.toString());
	}
}
