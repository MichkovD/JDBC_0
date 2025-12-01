package jm.task.core.jdbc.util;

import jm.task.core.jdbc.Main;
import lombok.Data;
import org.hibernate.SessionFactory;
import jm.task.core.jdbc.model.User;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.jboss.logging.Logger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Util {
    private static SessionFactory sessionFactory;
    private static final Logger logger = LoggerFactory.logger(Util.class);
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static Connection open() {
        try {
          return DriverManager.getConnection(PropertiesUtil.get(URL_KEY),
                   PropertiesUtil.get(USERNAME_KEY),
                   PropertiesUtil.get(PASSWORD_KEY));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    static {
        try {
            logger.info("Конфигурация для хибера вынесена в отдельный файл");
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(User.class);
             StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                       .applySettings(configuration.getProperties());
             sessionFactory = configuration.buildSessionFactory(registryBuilder.build());
        } catch (Exception e) {
            logger.error("Error while connecting to DB");
        }
    }
    private Util() {
    }
}
