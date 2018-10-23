package com.alibaba.dubbo.provider.rpc.client;

import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import com.alibaba.dubbo.provider.rpc.model.JsonUtils;
import com.alibaba.dubbo.provider.rpc.model.Request;
import com.alibaba.dubbo.provider.rpc.model.RpcInvocation;
import com.alibaba.dubbo.registry.IRegistry;

import io.netty.channel.Channel;

public class RpcClient {

	private ConnecManager connectManager;

	public RpcClient(IRegistry registry) {
		this.connectManager = new ConnecManager();
	}

	public RpcClient() {
		this.connectManager = new ConnecManager();
	}

	public void invoke(Long requestId, String parameter)
			throws Exception {
		Channel channel = connectManager.getChannel();
		RpcInvocation invocation = new RpcInvocation();
		invocation.setMethodName("hash");
		invocation.setAttachment("path", "com.alibaba.dubbo.performance.demo.provider.IHelloService");
		invocation.setParameterTypes("Ljava/lang/String;"); // Dubbo内部用"Ljava/lang/String"来表示参数类型是String
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(out));
		JsonUtils.writeObject(parameter, writer);
		invocation.setArguments(out.toByteArray());
		Request request = new Request();
		request.setId(requestId);
		request.setVersion("2.0.0");
		request.setTwoWay(true);
		request.setData(invocation);
		channel.writeAndFlush(request);
	}
}
