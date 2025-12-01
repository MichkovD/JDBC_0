package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DatabaseException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.hibernate.query.Query;
import org.jboss.logging.Logger;

import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final Logger logger = LoggerFactory.logger(UserDaoHibernateImpl.class);

    private static final String CREATE_TABLE_SQL = PropertiesUtil.getSQL("user.create.table");

    private static final String DROP_TABLE_SQL = PropertiesUtil.getSQL("user.drop.table");


    private static volatile UserDaoHibernateImpl INSTANCE;

    public static UserDaoHibernateImpl getInstance() {
        UserDaoHibernateImpl localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (UserDaoHibernateImpl.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new UserDaoHibernateImpl();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void createUsersTable() {
        try (var session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();
            session.createSQLQuery(CREATE_TABLE_SQL).executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Override
    public void dropUsersTable() {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();
            session.createSQLQuery(DROP_TABLE_SQL).executeUpdate();
            session.getTransaction().commit();
            logger.info("Hibernate: dropping table successfully");
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error dropping users", e);
            throw new DatabaseException("Hibernate implementation failed",e);
        }
    }


    @Override
    public void saveUser(String name, String lastName, byte age) {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();
            session.save(User.builder()
                    .name(name)
                    .lastName(lastName)
                    .age(age)
                    .build());
            session.getTransaction().commit();
            logger.info("Hibernate: saving user successfully");
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error saving user", e);
            throw new DatabaseException("Hibernate implementation failed",e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try (var session = Util.getSessionFactory().openSession()) {
            session.beginTransaction();//begin
            User userToDel = session.get(User.class, id);
            if (userToDel != null){
                session.delete(userToDel);
                logger.info("Hibernate: removing user by ID - good");
            } else {
                logger.warn("Hibernate: no user with this ID");
            }
        } catch (RuntimeException e) {
            logger.error("Hibernate: Error removing user", e);
            throw new DatabaseException("Hibernate implementation failed",e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> usersList;
        var session = Util.getSessionFactory().openSession();
        Query<User> query = session.createQuery("FROM User", User.class);
        usersList = query.list();
        session.close();
        logger.info("Hibernate: getting all users successfully");
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        try (var session = Util.getSessionFactory().openSession()){
            session.beginTransaction();
            session.createQuery("DELETE FROM User")
            .executeUpdate();
            session.getTransaction().commit();
            logger.info("Hibernate: cleaning all users successfully");
        } catch (RuntimeException e) {
            throw new DatabaseException(e);
        }
    }
}
