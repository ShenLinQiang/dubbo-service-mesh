package com.alibaba.dubbo.holder;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import io.netty.channel.Channel;

public class TcpChannelHolder {
	private static ConcurrentHashMap<String, Channel> map = new ConcurrentHashMap<>();
	
	private static AtomicInteger atomicInt = new AtomicInteger();

	public static void put(String requestId, Channel channel) {
		if (map.containsKey(requestId)) {
			return;
		}
		map.put(requestId, channel);
	}

	public static Channel get(String requestId) {
		if (map.containsKey(requestId)) {
			return map.get(requestId);
		}
		return null;
	}

	public static void remove(String requestId) {
		map.remove(requestId);
	}
	
	public static Integer getMaxIndex() {
		return atomicInt.get();
	}
}
