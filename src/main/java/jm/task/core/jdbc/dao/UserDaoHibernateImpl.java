package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final Logger logger = LoggerFactory.logger(UserDaoJDBCImpl.class);

    private static final String CREATE_TABLE_SQL = PropertiesUtil.getSQL("user.create.table");

    private static final String DROP_TABLE_SQL = PropertiesUtil.getSQL("user.drop.table");

    private static final String GET_USERS_SQL = PropertiesUtil.getSQL("user.get.all");

    public static final UserDaoHibernateImpl INSTANCE = new UserDaoHibernateImpl();

    public static UserDaoHibernateImpl getInstance() {
        return INSTANCE;
    }

    public UserDaoHibernateImpl() {

    }

    @Override
    public void createUsersTable() {
        try (var session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();//begin
            session.createSQLQuery(CREATE_TABLE_SQL).executeUpdate();
            session.getTransaction().commit(); //end
        }
    }

    @Override
    public void dropUsersTable() {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();//begin
            session.createSQLQuery(DROP_TABLE_SQL).executeUpdate();
            session.getTransaction().commit(); //end
            logger.info("Hibernate: dropping table successfully");
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error dropping users", e);
            throw new DaoException("Hibernate implementation failed",e);
        }
    }


    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();//begin
            session.save(User.builder()
                    .name(name)
                    .lastName(lastName)
                    .age(age)
                    .build());
            session.getTransaction().commit(); //end
            logger.info("Hibernate: saving user successfully");
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error saving user", e);
            throw new DaoException("Hibernate implementation failed",e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (var session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();//begin
            session.delete(User.builder().id(id).build());
            session.getTransaction().commit(); //end
            logger.info("Hibernate: removing user by ID");
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error removing user", e);
            throw new DaoException("Hibernate implementation failed",e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList;
        var session = Util.getSessionFactory().openSession();
        session.beginTransaction();//begin
        Query<User> query = session.createQuery("FROM User", User.class);
        System.out.println(query.list());
        usersList = query.list();
        session.getTransaction().commit(); //end
        session.close();
        logger.info("Hibernate: getting all users successfully");
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();//begin
            session.createQuery("DELETE FROM User")//создал запрос
            .executeUpdate(); //выполнил
            session.getTransaction().commit(); //end
            logger.info("Hibernate: cleaning all users successfully");
        } catch (RuntimeException e) {
            throw new DaoException(e);
        }
    }
}
