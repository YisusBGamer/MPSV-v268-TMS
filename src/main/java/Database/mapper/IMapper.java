/*
 * Decompiled with CFR 0.152.
 */
package Database.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface IMapper<T> {
    public T mapper(ResultSet var1) throws SQLException;
}

