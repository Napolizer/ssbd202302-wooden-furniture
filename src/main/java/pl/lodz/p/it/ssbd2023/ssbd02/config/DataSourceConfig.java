package pl.lodz.p.it.ssbd2023.ssbd02.config;

import jakarta.annotation.sql.DataSourceDefinition;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.sql.Connection;

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd02admin",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://${ENV=DB_HOST:127.0.0.1}:3306/ssbd02?useSSL=false&allowPublicKeyRetrieval=true",
    user = "ssbd02admin",
    password = "dbadmin",
    initialPoolSize = 1,
    minPoolSize = 0,
    maxPoolSize = 1,
    maxIdleTime = 10)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd02mok",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://${ENV=DB_HOST:127.0.0.1}:3306/ssbd02?useSSL=false&allowPublicKeyRetrieval=true",
    user = "ssbd02mok",
    password = "dbmok",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd02moz",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://${ENV=DB_HOST:127.0.0.1}:3306/ssbd02?useSSL=false&allowPublicKeyRetrieval=true",
    user = "ssbd02moz",
    password = "dbmoz",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@DataSourceDefinition(
    name = "java:app/jdbc/ssbd02auth",
    className = "com.mysql.cj.jdbc.MysqlDataSource",
    url = "jdbc:mysql://${ENV=DB_HOST:127.0.0.1}:3306/ssbd02?useSSL=false&allowPublicKeyRetrieval=true",
    user = "ssbd02auth",
    password = "dbauth",
    transactional = true,
    isolationLevel = Connection.TRANSACTION_READ_COMMITTED)

@Stateless
public class DataSourceConfig {
  @PersistenceContext(unitName = "ssbd02adminPU")
  private EntityManager em;
}
