package com.lsd.netty.config;

/**
 * @program:httpnettyserver
 * @Author:liushengdong
 * @Description:
 * @Date:Created in 2019-09-03 15:46
 * @Modified By:
 */
public class Config {
    private String host;
    private int port;
    private int workerThread;
    private String webRoot;
    private String fileRoot;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public int getWorkerThread() {
        return workerThread;
    }

    public void setWorkerThread(int workerThread) {
        this.workerThread = workerThread;
    }

    public String getWebRoot() {
        return webRoot;
    }

    public void setWebRoot(String webRoot) {
        this.webRoot = webRoot;
    }

    public String getFileRoot() {
        return fileRoot;
    }

    public void setFileRoot(String fileRoot) {
        this.fileRoot = fileRoot;
    }
}
