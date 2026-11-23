package co.edu.udea.certificacion.bookify.utils.connetiondb;

import org.apache.commons.dbcp2.BasicDataSource;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import static co.edu.udea.certificacion.bookify.utils.Configuracion.valor;

public class PostgreSQLConnection extends ExecuteQueryDb {

    public PostgreSQLConnection(String propertiesFile) { super(propertiesFile); }

    public void loadProperties(String propertiesFilePath) {
        Properties properties = new Properties();
        try (FileInputStream input = new FileInputStream(propertiesFilePath)) {
            properties.load(input);
            dataSource = new BasicDataSource();
            dataSource.setUrl(String.format("%s%s:%s/%s",
                    valor("db.JDBC", properties.getProperty("db.JDBC")),
                    valor("db.HOST", properties.getProperty("db.HOST")),
                    valor("db.PORT", properties.getProperty("db.PORT")),
                    valor("db.NAME_DATABASE", properties.getProperty("db.NAME_DATABASE"))));
            dataSource.setUsername(valor("db.USER", properties.getProperty("db.USER")));
            dataSource.setPassword(valor("PASSPSQL", "bookify_qa"));
            dataSource.setMaxTotal(Integer.parseInt(valor("db.poolSize", properties.getProperty("db.poolSize"))));
        } catch (IOException e) {
            throw new IllegalStateException("Error loading properties file: " + e.getMessage());
        }
    }
}