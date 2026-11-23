package co.edu.udea.certificacion.bookify.utils.connetiondb;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class ExecuteQueryDb {

    protected BasicDataSource dataSource;

    public ExecuteQueryDb(String propertiesFile) {
        loadProperties(propertiesFile);
    }
    private Connection connect() throws SQLException {
        return dataSource.getConnection();
    }

    private PreparedStatement preparar(Connection conn, String query, Object... parametros) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(query);
        for (int i = 0; i < parametros.length; i++) {
            pstmt.setObject(i + 1, parametros[i]);
        }
        return pstmt;
    }

    abstract void loadProperties(String propertiesFilePath);

    // Use this method for SQL SELECT statements.
    public List<Map<String, String>> executeQuery(String query, Object... parametros) {
        List<Map<String, String>> results = new ArrayList<>();

        try (Connection conn = connect();
             PreparedStatement pstmt = preparar(conn, query, parametros);
             ResultSet rs = pstmt.executeQuery()) {
            int columnCount = rs.getMetaData().getColumnCount();
            while (rs.next()) {
                Map<String, String> row = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = rs.getMetaData().getColumnLabel(i);
                    String columnValue = rs.getString(i);
                    row.put(columnName, columnValue);
                }
                results.add(row);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(e);
        }

        return results;
    }

    // Use this method for SQL INSERT, UPDATE or DELETE statements.
    public void executeQueryAffectRows(String query, Object... parametros) {
        try (Connection conn = connect();
            PreparedStatement pstmt = preparar(conn, query, parametros)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
        throw new IllegalStateException(e);
        }
    }

}
