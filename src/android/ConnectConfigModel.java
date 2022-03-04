package com.log2c.cordova.plugin.mqtt;

public class ConnectConfigModel {
    private String url;
    private int port;
    private String clientId;
    private int connectionTimeout;
    private int keepAliveInterval;
    private String username;
    private String password;
    private int keepAlive;
    private boolean isBinaryPayload;
    private boolean automaticReconnect;

    public String getUrl() {
        return url;
    }

    public ConnectConfigModel setUrl(String url) {
        this.url = url;
        return this;
    }

    public int getPort() {
        return port;
    }

    public ConnectConfigModel setPort(int port) {
        this.port = port;
        return this;
    }

    public String getClientId() {
        return clientId;
    }

    public ConnectConfigModel setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public ConnectConfigModel setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
        return this;
    }

    public int getKeepAliveInterval() {
        return keepAliveInterval;
    }

    public ConnectConfigModel setKeepAliveInterval(int keepAliveInterval) {
        this.keepAliveInterval = keepAliveInterval;
        return this;
    }

    public String getUsername() {
        return username;
    }

    public ConnectConfigModel setUsername(String username) {
        this.username = username;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public ConnectConfigModel setPassword(String password) {
        this.password = password;
        return this;
    }

    public int getKeepAlive() {
        return keepAlive;
    }

    public ConnectConfigModel setKeepAlive(int keepAlive) {
        this.keepAlive = keepAlive;
        return this;
    }

    public boolean isBinaryPayload() {
        return isBinaryPayload;
    }

    public ConnectConfigModel setBinaryPayload(boolean binaryPayload) {
        isBinaryPayload = binaryPayload;
        return this;
    }

    public boolean isAutomaticReconnect() {
        return automaticReconnect;
    }

    public ConnectConfigModel setAutomaticReconnect(boolean automaticReconnect) {
        this.automaticReconnect = automaticReconnect;
        return this;
    }
}
