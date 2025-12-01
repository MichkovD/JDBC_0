package jm.task.core.jdbc.exception;

import jm.task.core.jdbc.dao.UserDaoJDBCImpl;
import org.hibernate.annotations.common.util.impl.LoggerFactory;
import org.jboss.logging.Logger;

public class DatabaseException extends RuntimeException {
    private static final Logger logger = LoggerFactory.logger(DatabaseException.class);

    public DatabaseException(Exception e) {
        super(e);
        logger.error("Data Base Exception happened.", e);
    }

    public DatabaseException(String message, Exception e)
    {
        super(message, e);
        logger.error("Data Base Exception happened." + message, e);
    }
}
