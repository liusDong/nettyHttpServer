package com.lsd.netty.server;

import com.lsd.netty.config.Config;
import com.lsd.netty.spring.Container;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedFile;
import io.netty.util.CharsetUtil;

import javax.activation.MimetypesFileTypeMap;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


import static io.netty.handler.codec.http.HttpHeaderNames.CONNECTION;
import static io.netty.handler.codec.http.HttpHeaderNames.CONTENT_TYPE;
import static io.netty.handler.codec.http.HttpHeaderValues.KEEP_ALIVE;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpUtil.isKeepAlive;
import static io.netty.handler.codec.http.HttpUtil.setContentLength;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * @program:httpnettyserver
 * @Author:liushengdong
 * @Description:
 * @Date:Created in 2019-09-03 16:22
 * @Modified By:
 */
public class ServerHttpFileHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    Config config = Container.getConfig();
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        String uri = request.uri();
        String webRoot = config.getWebRoot();
        String fileRoot = config.getFileRoot();

        System.out.println(uri);
        if(webRoot.equals(uri)){
            File file = new File(fileRoot);
            if(file.isDirectory()){
                senfListing(ctx,file);
            }
        }else if(uri.startsWith(webRoot)){
            File dir = new File(fileRoot);
            List<File> files = Arrays.asList(dir.listFiles());
            String downloadFileName = uri.substring(webRoot.length()+1);
            files.stream().filter(file -> !file.isDirectory()).forEach(file -> {
                Optional.of(downloadFileName).ifPresent(fileName->{
                    if(fileName .equals(file.getName())){
                        RandomAccessFile randomAccessFile;
                        try{
                            System.out.println("开始下载");
                            randomAccessFile=new RandomAccessFile(file,"r");
                            Long fileLength=randomAccessFile.length();
                            HttpResponse httpResponse=new DefaultHttpResponse(HTTP_1_1,OK);
                            setContentLength(httpResponse,fileLength);
                            setContentTypeHeader(httpResponse,file);
                            if (isKeepAlive(request)){
                                httpResponse.headers().set(CONNECTION,KEEP_ALIVE);
                            }
                            ctx.writeAndFlush(httpResponse);
                            ChannelFuture sendFileFuture = ctx.write(
                                    new ChunkedFile(randomAccessFile,0,fileLength,8192),ctx.newProgressivePromise());
                            sendFileFuture.addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) throws Exception {
                                    System.out.println("is Done");
                                    if(future.isSuccess()){
                                        System.out.println("is already done");
                                    }
                                }
                            });
                            ChannelFuture lastChannelFuture=ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                            if (!isKeepAlive(request)){
                                lastChannelFuture.addListener(ChannelFutureListener.CLOSE );

                            }
                        }catch (FileNotFoundException e){
                            e.printStackTrace();
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            });

        }else {
            FullHttpResponse httpResponse = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
            ByteBuf byteBuf= Unpooled.copiedBuffer("file not exists", CharsetUtil.UTF_8);
            httpResponse.content().writeBytes(byteBuf);
            byteBuf.release();
            ctx.writeAndFlush(httpResponse).addListener(ChannelFutureListener.CLOSE);
        }

    }

    private void senfListing(ChannelHandlerContext channelHandlerContext, File dir) {
        FullHttpResponse response=new DefaultFullHttpResponse(HTTP_1_1,OK);
        response.headers().set(CONTENT_TYPE,"text/html;charset=UTF-8");
        StringBuilder builder =new StringBuilder();
        String dirPath=dir.getPath();
        builder.append("<!DOCTYPE html> \r\n");
        builder.append("<html><head><title>");
        builder.append(dirPath);
        builder.append("目录:");
        builder.append("</title></head><body>\r\n");
        builder.append("<h3>");
        builder.append("</h3>\r\n");
        builder.append("<ul>");
        for (File f:dir.listFiles()){
            if (f.isHidden()||!f.canRead()||f.isDirectory()){
                continue;
            }
            String fname=f.getName();
            builder.append("<li>链接：<a href=\" ");
            builder.append(config.getWebRoot()+"\\"+fname);
            builder.append("\" >");
            builder.append(fname);
            builder.append("</a></li>\r\n");
        }
        builder.append("</ul></body></html>\r\n");

        ByteBuf byteBuf= Unpooled.copiedBuffer(builder, CharsetUtil.UTF_8);
        response.content().writeBytes(byteBuf);
        byteBuf.release();
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);

    }
    private void setContentTypeHeader(HttpResponse httpResponse, File file) {
        MimetypesFileTypeMap mimetypesFileTypeMap=new MimetypesFileTypeMap();
        httpResponse.headers().set(CONTENT_TYPE,mimetypesFileTypeMap.getContentType(file.getPath()));
    }
}
