package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.exception.DaoException;
import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.PropertiesUtil;
import jm.task.core.jdbc.util.Util;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

public class UserDaoJDBCImpl implements UserDao {
    private static final Logger logger = LoggerFactory.logger(UserDaoJDBCImpl.class);

    public UserDaoJDBCImpl() {

    }

    public static final String CREATE_USER_TABLE_SQL = PropertiesUtil.getSQL("user.create.table");

    public static final String SAVE_USER_SQL = PropertiesUtil.getSQL("user.insert");

    public static final String GET_ALL_USERS_SQL = PropertiesUtil.getSQL("user.get.all");

    public static final UserDaoJDBCImpl INSTANCE = new UserDaoJDBCImpl();

    public static UserDaoJDBCImpl getInstance() {
        return INSTANCE;
    }

    public void createUsersTable() {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement(CREATE_USER_TABLE_SQL))
        {
            st.execute();
            logger.info("JDBC: Table created successfully");
        } catch (SQLException e) {
            logger.error("JDBC: Error creating table", e);
            throw new DaoException("JDBC implementation failed",e);
        }
    }

    public void dropUsersTable() {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement("drop table if exists public.user"))
        {
            logger.info("JDBC: Table dropped successfully");
            st.execute();
        } catch (SQLException e) {
            logger.error("JDBC: Error dropping table", e);
            throw new DaoException("JDBC implementation failed",e);
        }
    }

    public void saveUser(String name, String lastName, byte age) {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement(SAVE_USER_SQL))
        {
            st.setString(1, name);
            st.setString(2, lastName);
            st.setByte(3, age);
            st.execute();
            logger.info("JDBC: user added successfully");
        } catch (SQLException e) {
            logger.error("JDBC: Error adding user", e);
            throw new DaoException("JDBC implementation failed",e);
        }

    }

    public void removeUserById(long id) {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement("delete from public.user where id = ?"))
        {
            logger.info("JDBC: user removed successfully");
            st.setLong(1, id);
            st.execute();
        } catch (SQLException e) {
            logger.error("JDBC: Error removing user", e);
            throw new DaoException("JDBC implementation failed",e);
        }
    }

    public List<User> getAllUsers() {
        ArrayList<User> usersList = new ArrayList<User>();
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement(GET_ALL_USERS_SQL))
        {
            ResultSet rs = st.executeQuery();
            while(rs.next()) {
                User user = new User();
                user.setId(rs.getLong("id"));
                user.setName(rs.getString("name"));
                user.setLastName(rs.getString("last_name"));
                user.setAge(rs.getByte("age"));
                usersList.add(user);
            }
        } catch (SQLException e) {
            logger.error("JDBC: Error getting all users", e);
            throw new DaoException("JDBC implementation failed",e);
        }
        return usersList;
    }

    public void cleanUsersTable() {
        try(Connection con = Util.open();
            PreparedStatement st = con.prepareStatement(PropertiesUtil.getSQL("user.clean.table")))
        {
            logger.info("Connect есть. Удаляю всех user JDBC");
            st.execute();
        } catch (SQLException e) {
            throw new DaoException(e);
        }
    }
}

