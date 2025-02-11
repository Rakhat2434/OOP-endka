import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

    public class Database {
        private static final String URL = "jdbc:mysql://localhost:3307/sys";
        private static final String USER = "root";
        private static final String PASSWORD = "MS19AK47";

        // Получение соединения с БД
        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
}
