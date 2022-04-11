package com.jw.home.common.spec;

public enum DeviceConnection {
    websocket,
    mqtt;

    public static boolean isConnection(String name) {
        for (DeviceConnection connection : values()) {
            if (connection.name().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
