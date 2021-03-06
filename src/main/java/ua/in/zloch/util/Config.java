package ua.in.zloch.util;

import java.io.IOException;
import java.util.Properties;

public class Config {
    private static final String PROPERTIES = "application.properties";
    private static Config instance = null;
    private Properties props = null;

    private Config() {
        reloadConfiguration();
    }

    public static Config get() {
        if (null == instance) {
            synchronized(Config.class){
                if (instance == null) {
                    instance = new Config();
                }
            }
        }
        return instance;
    }

    public void reloadConfiguration() {
        props = new Properties();
        try {
            props.load(Config.class.getResourceAsStream("/"+PROPERTIES));
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String strValue(String key) {
        return getProperty(key);
    }

    public int intValue(String key) {
        return Integer.parseInt(getProperty(key));
    }

    public double doubleValue(String key) {
        return Double.parseDouble(getProperty(key));
    }

    public boolean boolValue(String key) {
        return Boolean.parseBoolean(getProperty(key));
    }

    private String getProperty(String key) {
        String value = null;
        if (props.containsKey(key)) {
            value = (String) props.get(key);
        } else {
            System.err.println("Property " + key + " does not defined.");
        }
        return value;
    }

}