package jm.task.core.jdbc;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.PropertiesUtil;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

public class Main {
    private static final Logger logger = LoggerFactory.logger(Main.class);
    private static final boolean switchToHibernate = Boolean.parseBoolean(PropertiesUtil.get("switchToHibernate"));

    public static void main(String[] args) {

        if (switchToHibernate){
            logger.info("Application started with hibernate implementation");
            MyTestHibernate();
        }
        else{
            logger.info("Application started with JDBC implementation");
            MyTestJDBC();
        }
    }

    public static void MyTestJDBC(){
        UserService userService = new UserServiceImpl();
        userService.createUsersTable();
        userService.saveUser("Maven", "Christ", (byte)27);
        userService.saveUser("Jack", "Black", (byte)33);
        userService.saveUser("Posty", "Gres", (byte)69);
        userService.saveUser("Hyber", "Nate", (byte)100);

        userService.removeUserById(1);
        userService.removeUserById(1);
        userService.cleanUsersTable();
        userService.dropUsersTable();
    }

    public static void MyTestHibernate(){
        UserService userService = new UserServiceImpl();
        userService.dropUsersTable();
        userService.createUsersTable();
        userService.saveUser("Anna", "Black", (byte)32);
        userService.saveUser("Jack", "Black", (byte)33);
        userService.removeUserById(1);
        userService.cleanUsersTable();
        userService.dropUsersTable();
    }
}
