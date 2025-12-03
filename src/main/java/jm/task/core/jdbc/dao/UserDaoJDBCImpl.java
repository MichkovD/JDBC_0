package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DatabaseException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@NoArgsConstructor
@Slf4j
public class UserDaoJDBCImpl implements UserDao {
    public static final String CLEAN_USER_TABLE_SQL = PropertiesUtil.getSQL("user.clean.table");
    public static final String CREATE_USER_TABLE_SQL = PropertiesUtil.getSQL("user.create.table");
    public static final String SAVE_USER_SQL = PropertiesUtil.getSQL("user.insert");
    public static final String GET_ALL_USERS_SQL = PropertiesUtil.getSQL("user.get.all");

    private static volatile UserDaoJDBCImpl INSTANCE;

    public static UserDaoJDBCImpl getInstance() {
        UserDaoJDBCImpl localInstance = INSTANCE;
        if (localInstance == null) {
            synchronized (UserDaoJDBCImpl.class) {
                localInstance = INSTANCE;
                if (localInstance == null) {
                    INSTANCE = localInstance = new UserDaoJDBCImpl();
                }
            }
        }
        return localInstance;
    }

    @Override
    public void createUsersTable() {
        try (Connection con = Util.open();
             Statement st = con.createStatement()) {
            st.execute(CREATE_USER_TABLE_SQL);
            log.info("JDBC: Table 'user' created");
        } catch (SQLException e) {
            log.error("JDBC: Error while creating the table", e);
            throw new DatabaseException("Failed to create users table", e);
        }
    }

    @Override
    public void dropUsersTable() {
        try(Connection con = Util.open();
            Statement st = con.createStatement())
        {
            st.execute("drop table if exists public.user");
            log.info("JDBC: Table dropped successfully");
        } catch (SQLException e) {
            log.error("JDBC: Error dropping table", e);
            throw new DatabaseException("JDBC implementation failed",e);
        }
    }
    @Override
    public void saveUser(String name, String lastName, byte age) {

        try (Connection con = Util.open();
             PreparedStatement st = con.prepareStatement(SAVE_USER_SQL)) {
            st.setString(1, name);
            st.setString(2, lastName);
            st.setByte(3, age);
            st.executeUpdate();
            log.info("JDBC: User added: {} {} (age: {})", name, lastName, age);
            } catch (SQLException e) {
                log.error("JDBC: Error saving user", e);
                throw new DatabaseException("Error saving user",e);
        }
    }

    @Override
    public void removeUserById(long id) {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement("delete from public.user where id = ?"))
        {
            st.setLong(1, id);
            st.execute();
            log.info("JDBC: user removed successfully");
        } catch (SQLException e) {
            log.error("JDBC: Error removing user", e);
            throw new DatabaseException("JDBC implementation failed",e);
        }
    }

    @Override
    public List<User> getAllUsers(int page, int size) {
        if (page < 1 || size < 1){
            throw new IllegalArgumentException("should do page >= 1 and size >= 1");
        }
        ArrayList<User> usersList = new ArrayList<User>();
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement(GET_ALL_USERS_SQL))
        {
            st.setInt(1, size);
            st.setInt(2, (page - 1) * size);
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getByte("age"));
                usersList.add(user);
            }
            log.info("JDBC: list of users withdrawn successfully");
        } catch (SQLException e) {
            log.error("JDBC: Error getting all users", e);
            throw new DatabaseException("JDBC implementation failed",e);
        }
        return usersList;
    }

    @Override
    public void cleanUsersTable() {
        try (Connection con = Util.open()) {
            con.setAutoCommit(false);
            try (Statement st = con.createStatement()) {
                int deletedCount = st.executeUpdate(CLEAN_USER_TABLE_SQL);
                con.commit();

                log.info("JDBC: Cleared {} users from table", deletedCount);
            } catch (SQLException e) {
                con.rollback();
                throw new DatabaseException("Failed to clean table", e);
            }
        } catch (SQLException e) {
            log.error("JDBC: Error cleaning users table", e);
            throw new DatabaseException("Failed to clean table", e);
        }
    }
}

