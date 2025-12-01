package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DatabaseException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import java.util.List;


@NoArgsConstructor
@Slf4j
public class UserDaoHibernateImpl implements UserDao {
    private static final int MAX_USERS_TO_GET = 1000;

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
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(CREATE_TABLE_SQL).executeUpdate();
            transaction.commit();
            log.info("Hibernate: Table 'user' created successfully");
        } catch (Exception e) {
            log.error("Failed to create users table", e);
            rollbackSafely(transaction);
            throw new DatabaseException("Failed to create users table", e);
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(DROP_TABLE_SQL).executeUpdate();
            transaction.commit();
            log.info("Hibernate: Table dropped successfully");
        } catch (Exception e) {
            log.error("Failed to drop the table", e);
            rollbackSafely(transaction);
            throw new DatabaseException("Failed to drop the table", e);
        }
    }


    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (var session = Util.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.save(User.builder()
                    .name(name)
                    .lastName(lastName)
                    .age(age)
                    .build());
            transaction.commit();
            log.info("Hibernate: saving user successfully");
        } catch (RuntimeException e) {
            log.error("Hibernate: Error saving user", e);
            rollbackSafely(transaction);
            throw new DatabaseException("Hibernate: saving user failed",e);
        }
    }

    @Override
    public void removeUserById(long id) {
        log.debug("Removing user with id {}", id);
        Transaction transaction = null;

        try (Session session = Util.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = new User();
            user.setId(id);
            session.delete(user);
            transaction.commit();
            log.info("Hibernate: User with id {} was deleted", id);

        } catch (RuntimeException e) {
            log.error("Error while removing user with id {}", id, e);
            rollbackSafely(transaction);
            throw new DatabaseException("Error while removing user with id " + id, e);
        }
    }

    @Override
    public List<User> getAllUsers() {
        try (var session = Util.getSessionFactory().openSession()) {
            List<User> usersList = session.createQuery("FROM User", User.class)
                    .setMaxResults(MAX_USERS_TO_GET)
                    .list();

            log.info("Hibernate: successfully got {} users", usersList.size());
            return usersList;

        } catch (RuntimeException e) {
            log.error("Hibernate: Error getting all users", e);
            throw new DatabaseException("Hibernate implementation failed", e);
        }
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (var session = Util.getSessionFactory().openSession()){
            transaction = session.beginTransaction();
            session.createQuery("DELETE FROM User")
            .executeUpdate();
            transaction.commit();
            log.info("Hibernate: cleaning all users successfully");
        } catch (RuntimeException e) {
            log.error("Error cleaning the table", e);
            rollbackSafely(transaction);
            throw new DatabaseException("Error cleaning the table", e);
        }
    }

    private void rollbackSafely(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
                log.debug("Transaction rolled back");
            } catch (Exception rollbackEx) {
                log.error("Failed to rollback transaction", rollbackEx);
            }
        }
    }
}
