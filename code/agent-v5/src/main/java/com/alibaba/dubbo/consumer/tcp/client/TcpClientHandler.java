package com.alibaba.dubbo.consumer.tcp.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.dubbo.ExceptionUtil;
import com.alibaba.dubbo.holder.HttpChannelHolder;
import com.alibaba.dubbo.proto.TcpResponseProto;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpHeaderValues;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class TcpClientHandler extends SimpleChannelInboundHandler<TcpResponseProto.TcpResponse> {
	private static Logger logger = LoggerFactory.getLogger(TcpClientHandler.class);


	@Override
	protected void channelRead0(ChannelHandlerContext ctx, TcpResponseProto.TcpResponse msg) throws Exception {
		String rpcResult = msg.getParameter();
		ByteBuf buf = Unpooled.wrappedBuffer(rpcResult.getBytes());
		DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
				HttpResponseStatus.OK, buf);
		response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/plain; charset=UTF-8");
		response.headers().set(HttpHeaderNames.CONTENT_LENGTH,rpcResult.length());
		response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
		try {
			String requestId = msg.getId();
			Channel channel = HttpChannelHolder.getHttpChannel(requestId);
			channel.writeAndFlush(response);
			HttpChannelHolder.removeHttpChannel(requestId);
		}catch (Exception e) {
			logger.error(ExceptionUtil.getMessage(e));
		}
	}

}
