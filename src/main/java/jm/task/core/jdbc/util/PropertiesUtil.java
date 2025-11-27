package jm.task.core.jdbc.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class PropertiesUtil {
    private static final Properties PROPERTIES = new Properties();
    private static final Properties SQLPROPERTIES = new Properties();
    static {
        loadProperties();
    }

    private static void loadProperties() {
        try(InputStream inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("application.properties"))
        {
            PROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        try(InputStream inputStream = PropertiesUtil.class.getClassLoader()
                .getResourceAsStream("sql-queries.properties"))
        {
            SQLPROPERTIES.load(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String get(String key) {
        return PROPERTIES.getProperty(key);
    }

    public static String getSQL(String key) {
        return SQLPROPERTIES.getProperty(key);
    }

    private PropertiesUtil() {
    }
}
