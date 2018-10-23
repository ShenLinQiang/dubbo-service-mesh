package com.alibaba.dubbo.provider.rpc.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.ExceptionUtil;
import com.alibaba.dubbo.holder.TcpChannelHolder;
import com.alibaba.dubbo.proto.TcpResponseProto;
import com.alibaba.dubbo.proto.TcpResponseProto.TcpResponse.Builder;
import com.alibaba.dubbo.provider.rpc.model.RpcResponse;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {
	private static Logger logger = LoggerFactory.getLogger(RpcClientHandler.class);

	@Override
	protected void channelRead0(ChannelHandlerContext channelHandlerContext, RpcResponse response) {
		try {
			String requestId = response.getRequestId();
			Builder newBuilder = TcpResponseProto.TcpResponse.newBuilder();
			newBuilder.setId(requestId);
			newBuilder.setParameter(new String(response.getBytes()));
			com.alibaba.dubbo.proto.TcpResponseProto.TcpResponse tcpResponse = newBuilder.build();
			Channel channel = TcpChannelHolder.get(requestId);
			channel.writeAndFlush(tcpResponse);
		} catch (Exception e) {
			logger.error(ExceptionUtil.getMessage(e));
		}

	}

}
