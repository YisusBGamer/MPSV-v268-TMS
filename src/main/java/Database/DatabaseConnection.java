/*
 * Decompiled with CFR 0.152.
 */
package Database;

import Database.DatabaseException;
import Database.DatabaseLoader;
import java.sql.Connection;
import java.sql.SQLException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DatabaseConnection
implements AutoCloseable {
    private static final Logger log = LoggerFactory.getLogger("Database");
    private final Connection conn;

    public DatabaseConnection() {
        this(DatabaseLoader.getConnection());
    }

    public DatabaseConnection(boolean notAutoCommit) {
        this(DatabaseLoader.getConnection(), true);
    }

    private DatabaseConnection(Connection conn) {
        this.conn = conn;
    }

    private DatabaseConnection(Connection conn, boolean notAutoCommit) {
        this.conn = conn;
        try {
            this.conn.setAutoCommit(false);
        }
        catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public final Connection getConnection() {
        return this.conn;
    }

    public final void commit() {
        try {
            this.conn.commit();
        }
        catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public final void rollback() {
        try {
            this.conn.rollback();
        }
        catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    @Override
    public final void close() {
        try {
            this.conn.close();
        }
        catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }

    public static <T> T domain(DatabaseInterface<T> interfaces) {
        return DatabaseConnection.domain(interfaces, "資料庫異常", false);
    }

    public static <T> T domain(DatabaseInterface<T> interfaces, String errmsg) {
        return DatabaseConnection.domain(interfaces, errmsg, false);
    }

    public static <T> T domain(DatabaseInterface<T> interfaces, String errmsg, boolean needShutdown) {
        T object;
        block7: {
            object = null;
            try (DatabaseConnection con = new DatabaseConnection(true);){
                object = interfaces.domain(con.getConnection());
                con.commit();
            }
            catch (Throwable e) {
                log.error(errmsg, e);
                if (!needShutdown) break block7;
                System.exit(0);
            }
        }
        return object;
    }

    public static <T> T domainThrowsException(DatabaseInterface<T> interfaces) throws DatabaseException {
        T object = null;
        try (DatabaseConnection con = new DatabaseConnection(true);){
            object = interfaces.domain(con.getConnection());
            con.commit();
        }
        catch (Exception e) {
            throw new DatabaseException(e);
        }
        return object;
    }

    public static interface DatabaseInterface<T> {
        public T domain(Connection var1) throws SQLException;
    }
}

