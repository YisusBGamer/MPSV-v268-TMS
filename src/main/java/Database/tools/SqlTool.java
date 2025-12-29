/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  Database.tools.SqlTool$DatabaseAction
 */
package Database.tools;

import Database.DatabaseException;
import Database.DatabaseLoader;
import Database.mapper.IMapper;
import Database.tools.SqlTool;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlTool {
    public static ResultSet query(Connection con, String query) throws SQLException {
        PreparedStatement stmt = con.prepareStatement(query);
        return stmt.executeQuery();
    }
    interface DatabaseAction {
        Object execute(Connection con) throws Exception;
    }

    public static void domain(DatabaseAction action, String message) {
        try (Connection con = SqlTool.getConnection();){
            action.execute(con);
            System.out.println(message);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("url", "root", "root");
    }

    public static void update(String sql) {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static void update(Connection con, String sql) {
        try (PreparedStatement ps = con.prepareStatement(sql);){
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static void update(String sql, Object ... values) {
        DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                SqlTool.compile(ps, values);
                ps.executeUpdate();
            }
            return null;
        });
    }

    public static void update(Connection con, String sql, Object ... values) {
        try (PreparedStatement ps = con.prepareStatement(sql);){
            SqlTool.compile(ps, values);
            ps.executeUpdate();
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static Object updateAndGet(String sql, Object ... values) {
        return SqlTool.updateAndGet(sql, 1, values);
    }

    public static Object updateAndGet(String sql, int columnIndex, Object ... values) {
        return DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql, 1);){
                Integer n;
                block16: {
                    ResultSet rs;
                    block14: {
                        Object object;
                        block15: {
                            SqlTool.compile(ps, values);
                            ps.executeUpdate();
                            rs = ps.getGeneratedKeys();
                            try {
                                if (!rs.next()) break block14;
                                object = rs.getObject(columnIndex);
                                if (rs == null) break block15;
                            }
                            catch (Throwable throwable) {
                                if (rs != null) {
                                    try {
                                        rs.close();
                                    }
                                    catch (Throwable throwable2) {
                                        throwable.addSuppressed(throwable2);
                                    }
                                }
                                throw throwable;
                            }
                            rs.close();
                        }
                        return object;
                    }
                    n = -1;
                    if (rs == null) break block16;
                    rs.close();
                }
                return n;
            }
        });
    }

    public static int executeUpdate(String sql, Object ... values) {
        return DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                SqlTool.compile(ps, values);
                Integer n = ps.executeUpdate();
                return n;
            }
        });
    }

    public static int executeUpdate(Connection con, final String sql, final Object... values) {
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            compile(ps, values);
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }


    public static ResultSet query(Connection con, String sql, Object ... values) {
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            SqlTool.compile(ps, values);
            return ps.executeQuery();
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static <T> T queryAndGet(String sql, IMapper<T> rso) {
        return (T)DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery();){
                if (rs.next()) {
                    Object t = rso.mapper(rs);
                    return t;
                }
            }
            return null;
        });
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T queryAndGet(Connection con, String sql, IMapper<T> rso) {
        try (PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery();){
            if (!rs.next()) return null;
            T t = rso.mapper(rs);
            return t;
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static <T> T queryAndGet(String sql, IMapper<T> rso, Object ... values) {
        return (T)DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                SqlTool.compile(ps, values);
                try (ResultSet rs = ps.executeQuery();){
                    if (rs.next()) {
                        Object t = rso.mapper(rs);
                        return t;
                    }
                }
            }
            return null;
        });
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    public static <T> T queryAndGet(Connection con, String sql, IMapper<T> rso, Object ... values) {
        try (PreparedStatement ps = con.prepareStatement(sql);){
            SqlTool.compile(ps, values);
            try (ResultSet rs = ps.executeQuery();){
                if (!rs.next()) return null;
                T t = rso.mapper(rs);
                return t;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static <T> List<T> queryAndGetList(String sql, IMapper<T> rso) {
        return DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                ArrayList arrayList;
                block13: {
                    ResultSet rs = ps.executeQuery();
                    try {
                        ArrayList list = new ArrayList();
                        while (rs.next()) {
                            list.add(rso.mapper(rs));
                        }
                        arrayList = list;
                        if (rs == null) break block13;
                    }
                    catch (Throwable throwable) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    rs.close();
                }
                return arrayList;
            }
        });
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static <T> List<T> queryAndGetList(Connection con, String sql, IMapper<T> rso) {
        try (PreparedStatement ps = con.prepareStatement(sql);){
            ArrayList<T> arrayList;
            block15: {
                ResultSet rs = ps.executeQuery();
                try {
                    ArrayList<T> list = new ArrayList<T>();
                    while (rs.next()) {
                        list.add(rso.mapper(rs));
                    }
                    arrayList = list;
                    if (rs == null) break block15;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return arrayList;
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static <T> List<T> queryAndGetList(String sql, IMapper<T> rso, Object ... values) {
        return DatabaseLoader.DatabaseConnection.domain(con -> {
            try (PreparedStatement ps = con.prepareStatement(sql);){
                ArrayList arrayList;
                block13: {
                    SqlTool.compile(ps, values);
                    ResultSet rs = ps.executeQuery();
                    try {
                        ArrayList list = new ArrayList();
                        while (rs.next()) {
                            list.add(rso.mapper(rs));
                        }
                        arrayList = list;
                        if (rs == null) break block13;
                    }
                    catch (Throwable throwable) {
                        if (rs != null) {
                            try {
                                rs.close();
                            }
                            catch (Throwable throwable2) {
                                throwable.addSuppressed(throwable2);
                            }
                        }
                        throw throwable;
                    }
                    rs.close();
                }
                return arrayList;
            }
        });
    }

    /*
     * Enabled aggressive exception aggregation
     */
    public static <T> List<T> queryAndGetList(Connection con, String sql, IMapper<T> rso, Object ... values) {
        try (PreparedStatement ps = con.prepareStatement(sql);){
            ArrayList<T> arrayList;
            block15: {
                SqlTool.compile(ps, values);
                ResultSet rs = ps.executeQuery();
                try {
                    ArrayList<T> list = new ArrayList<T>();
                    while (rs.next()) {
                        list.add(rso.mapper(rs));
                    }
                    arrayList = list;
                    if (rs == null) break block15;
                }
                catch (Throwable throwable) {
                    if (rs != null) {
                        try {
                            rs.close();
                        }
                        catch (Throwable throwable2) {
                            throwable.addSuppressed(throwable2);
                        }
                    }
                    throw throwable;
                }
                rs.close();
            }
            return arrayList;
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    public static List<Map<String, Object>> customSqlResult(String sql, Object ... values) {
        return DatabaseLoader.DatabaseConnection.domainThrowsException(con -> {
            ArrayList list = new ArrayList();
            try (PreparedStatement ps = con.prepareStatement(sql);){
                SqlTool.compile(ps, values);
                try (ResultSet rs = ps.executeQuery();){
                    ResultSetMetaData metaData = rs.getMetaData();
                    while (rs.next()) {
                        HashMap<String, Object> map = new HashMap<String, Object>();
                        for (int i = 0; i < metaData.getColumnCount(); ++i) {
                            String column = metaData.getColumnLabel(i + 1);
                            map.put(column, rs.getObject(column));
                        }
                        if (map.isEmpty()) continue;
                        list.add(map);
                    }
                }
            }
            return list;
        });
    }

    public static boolean next(ResultSet rs) {
        try {
            return rs.next();
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static void compile(PreparedStatement ps, Object ... values) {
        try {
            for (int i = 0; i < values.length; ++i) {
                ps.setObject(i + 1, values[i]);
            }
        }
        catch (SQLException ex) {
            throw new DatabaseException(ex);
        }
    }
}

