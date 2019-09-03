package com.lsd.netty.server;


import com.lsd.netty.config.Config;
import com.lsd.netty.spring.Container;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.springframework.context.ApplicationContext;
import java.util.Optional;

/**
 * @program:httpnettyserver
 * @Author:liushengdong
 * @Description:
 * @Date:Created in 2019-09-03 15:44
 * @Modified By:
 */
public class Server {

    public static void main(String[] args) {
        ApplicationContext application = Container.instance();
        Config config = (Config)application.getBean("config");
        Server server = new Server();
        server.bind(config);
    }
    private void bind(Config config){

        String host = Optional.ofNullable(config.getHost()).orElse("localhost");
        int port = Optional.ofNullable(config.getPort()).orElse(8888);
        int workerThread = Optional.ofNullable(config.getWorkerThread()).orElse(8);


        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup(workerThread);

        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.channel(NioServerSocketChannel.class)
                    .group(bossGroup,workerGroup)
                    .childHandler(new ServerInitialization());
            ChannelFuture channelFuture = bootstrap.bind(host, port).sync();

            System.out.println("服务端启动：");
            System.out.println("host:"+host+"\r\n"+"port:"+port);

            channelFuture.channel().closeFuture().sync();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}
