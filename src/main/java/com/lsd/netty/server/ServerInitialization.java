package com.lsd.netty.server;


import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @program:httpnettyserver
 * @Author:liushengdong
 * @Description:
 * @Date:Created in 2019-09-03 16:10
 * @Modified By:
 */
public class ServerInitialization extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch){
        ChannelPipeline pipeline = ch.pipeline();
        //http 服务端编解码器
        pipeline.addLast("httpServerCodec",new HttpServerCodec());
        //http 粘包拆包聚合器
        pipeline.addLast("httpObjectAggregator",new HttpObjectAggregator(512*1024));
        //http 块数据写入
        pipeline.addLast("chunkedWriteHandler",new ChunkedWriteHandler());
        //http 自定义文件handler
        pipeline.addLast("serverHttpFileHandler",new ServerHttpFileHandler());
    }
}
