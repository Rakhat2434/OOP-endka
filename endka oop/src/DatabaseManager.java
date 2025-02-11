import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseManager {
    private static final String URL = "jdbc:mysql://localhost:3307/sys";
    private static final String USER = "root";
    private static final String PASSWORD = "MS19AK47";

    // Получение соединения с БД
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    // Получение всех компаний
    public static List<String> getAllCompanies() {
        List<String> companies = new ArrayList<>();
        String sql = "SELECT name FROM company";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                companies.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            System.err.println("Error loading companies: " + e.getMessage());
            e.printStackTrace();
        }
        return companies;
    }

    // Получение всех подписчиков
    public static List<Subscriber> getAllSubscribers() {
        List<Subscriber> subscribers = new ArrayList<>();
        String sql = "SELECT id, name, number, balance, tariff FROM subscriber";

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                subscribers.add(new Subscriber(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("number"),
                        rs.getDouble("balance"),
                        rs.getString("tariff")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading subscribers: " + e.getMessage());
            e.printStackTrace();
        }
        return subscribers;
    }

    // Получение всех тарифов
    public static List<Tariff> getAllTariffs() {
        List<Tariff> tariffs = new ArrayList<>();
        String query = "SELECT id, name, price, company FROM tariff"; // исправлено название таблицы

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {

            while (rs.next()) {
                tariffs.add(new Tariff(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("price"),
                        rs.getString("company")
                ));
            }
        } catch (SQLException e) {
            System.err.println("Error loading tariffs: " + e.getMessage());
            e.printStackTrace();
        }
        return tariffs;
    }

    // Добавление подписчика
    public static boolean addSubscriber(Integer id, String name, String number, double balance, String tariff) {
        String query = "INSERT INTO subscriber (id, name, number, balance, tariff) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setString(3, number);
            pstmt.setDouble(4, balance);
            pstmt.setString(5, tariff);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding subscriber: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    // Удаление тарифа
    public static boolean deleteTariff(int tariffId) {
        String query = "DELETE FROM tariff WHERE id = ?"; // исправлено название таблицы

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, tariffId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting tariff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public static boolean addTariff(int id, String name, double price, String company) {
        String query = "INSERT INTO tariff (id, name, price, company) VALUES (?, ?, ?, ?)";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setInt(1, id);
            pstmt.setString(2, name);
            pstmt.setDouble(3, price);
            pstmt.setString(4, company);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("Error adding tariff: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
    public static boolean companyExists(String company) {
        String query = "SELECT COUNT(*) FROM company WHERE name = ?";

        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {

            pstmt.setString(1, company);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0; // Исправлен порядок выполнения запроса
            }
        } catch (SQLException e) {
            System.err.println("Error checking company existence: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }
}
