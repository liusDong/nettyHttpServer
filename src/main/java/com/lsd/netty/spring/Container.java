package com.lsd.netty.spring;

import com.lsd.netty.config.Config;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @program:httpnettyserver
 * @Author:liushengdong
 * @Description:
 * @Date:Created in 2019-09-03 16:01
 * @Modified By:
 */
public class Container {

    private Container(){

    }

    public static ApplicationContext instance(){
        return ContainerHolder.application;
    }

    public static Config getConfig(){
        return (Config) ContainerHolder.application.getBean("config");
    }

    private static class ContainerHolder{
        public static final ApplicationContext application = new ClassPathXmlApplicationContext("spring-config.xml");
    }
}
