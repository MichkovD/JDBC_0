package jm.task.core.jdbc.util;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import jm.task.core.jdbc.model.User;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class Util {
    @Getter
    private static final SessionFactory sessionFactory;
    private static final String URL_KEY = "db.url";
    private static final String USERNAME_KEY = "db.username";
    private static final String PASSWORD_KEY = "db.password";

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
        SessionFactory tempSessionFactory = null;
        try {
            log.info("Конфигурация для хибера вынесена в отдельный файл");
            Configuration configuration = new Configuration();
            configuration.configure();
            configuration.addAnnotatedClass(User.class);
            StandardServiceRegistryBuilder registryBuilder = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties());
            tempSessionFactory = configuration.buildSessionFactory(registryBuilder.build());
        } catch (Exception e) {
            log.error("Error while connecting to DB");
        }
        sessionFactory = tempSessionFactory;
    }

    private Util() {
    }
}