package com.alibaba.dubbo.provider.tcp.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.ExceptionUtil;
import com.alibaba.dubbo.holder.TcpChannelHolder;
import com.alibaba.dubbo.proto.TcpRequestProto;
import com.alibaba.dubbo.provider.rpc.client.RpcClient;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TcpServerHandler extends ChannelInboundHandlerAdapter {
	private static Logger logger = LoggerFactory.getLogger(TcpServerHandler.class);
	private static RpcClient rpcClient = new RpcClient();
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		TcpRequestProto.TcpRequest request = (TcpRequestProto.TcpRequest) msg;
		TcpChannelHolder.put(String.valueOf(request.getId()), ctx.channel());
		rpcClient.invoke(request.getId(), request.getParameter());
	}


	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		logger.error(ExceptionUtil.getMessage(cause));
		ctx.close();
	}

}
