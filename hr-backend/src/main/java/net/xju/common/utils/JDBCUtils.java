package net.xju.common.utils;

import com.alibaba.druid.pool.DruidDataSourceFactory;

import javax.sql.DataSource;
import java.io.InputStream;
import java.sql.Connection;
import java.util.Properties;

public class JDBCUtils {

    private static DataSource dataSource=null;
    private static ThreadLocal<Connection> threadLocal = new ThreadLocal<>();

    // 加载配置文件
    static {
        //1.创建Properties集合，用于存储外部配置文件的key和value值。
        Properties properties = new Properties();
        // 通过类加载 获取inputStream
        InputStream resourceAsStream = JDBCUtils.class.getClassLoader().getResourceAsStream("db.properties");
        try {
            properties.load(resourceAsStream);
            for (String key : new String[]{"url", "username", "password"}) {
                String value = System.getenv("HR_DB_" + key.toUpperCase(java.util.Locale.ROOT));
                if (value != null) properties.setProperty(key, value);
            }
            dataSource = DruidDataSourceFactory.createDataSource(properties);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // 获取连接
    public static Connection getConnection() {
        try {
            //在ThreadLocal中获取Connection
            Connection connection = threadLocal.get();
            if (connection == null) {
                connection = dataSource.getConnection();
                threadLocal.set(connection);
            }
            return connection;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    // 释放连接
    public static void close(){
        try {
            Connection connection = threadLocal.get();
            if (connection != null) {
                //如果开启了事务的手动提交，操作完毕后，归还给连接池之前，要将事务的自动提交改为true
                connection.setAutoCommit(true);
                connection.close();
                threadLocal.remove();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public static DataSource getDataSource() {
        return dataSource;
    }


}
