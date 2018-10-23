package com.alibaba.dubbo.holder;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

public class HttpChannelHolder {

    private static ConcurrentHashMap<String,Channel> map = new ConcurrentHashMap<>();
    //key: requestId , value: httpchannel
    public static void putHttpChannel(String requestId,Channel channel) {
    	if(map.containsKey(requestId)) {
    		return ;
    	}
    	map.put(requestId, channel);
    }
    
    public static Channel getHttpChannel(String requestId) {
    	if(map.containsKey(requestId)) {
    		return map.get(requestId);
    	}
    	return null;
    }
    
    public static void removeHttpChannel(String requestId) {
    	map.remove(requestId);
    }

}
