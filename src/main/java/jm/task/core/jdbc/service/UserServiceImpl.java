package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import lombok.NoArgsConstructor;
import java.util.List;

@NoArgsConstructor
public class UserServiceImpl implements UserService {
    private final int DEFAULT_PAGE = 1;
    private final int DEFAULT_SIZE = 100;

    private static final boolean switchToHibernate = Boolean.parseBoolean(PropertiesUtil.get("switchToHibernate"));
    public UserDao userDao = getUserDao();

    private UserDao getUserDao(){
        if (switchToHibernate){
            return UserDaoHibernateImpl.getInstance();
        }
        return UserDaoJDBCImpl.getInstance();
    }

    public void createUsersTable() {
        userDao.createUsersTable();
    }

    public void dropUsersTable() {
        userDao.dropUsersTable();
    }

    public void saveUser(String name, String lastName, byte age) {
        userDao.saveUser(name, lastName, age);
    }

    public void removeUserById(long id) {
        userDao.removeUserById(id);
    }

    public List<User> getAllUsers(int page, int size) {
        return userDao.getAllUsers(page, size);
    }

    public List<User> getAllUsers() {
        return userDao.getAllUsers(DEFAULT_PAGE, DEFAULT_SIZE);
    }

    public void cleanUsersTable() {
        userDao.cleanUsersTable();
    }
}
