package com.alex.sm4demo.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Created by Alex on 2022/3/25 15:42
 */
public class ConfigUtils {
    public static Properties myProp = new Properties();
    public static InputStream myResource = ConfigUtils.class.getResourceAsStream("/config/seting.properties");
    static {
        try {
            myProp.load(myResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String getMyConf(String props) {
        return myProp.getProperty(props);
    }

    public static void main(String[] args) {
        final ConfigUtils myConfig = new ConfigUtils();
        System.out.println(myConfig.getMyConf("redis.ip"));
    }
}
