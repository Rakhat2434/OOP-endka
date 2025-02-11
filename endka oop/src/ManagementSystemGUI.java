import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.HashSet;
public class ManagementSystemGUI extends JFrame {
    private JTable tariffTable, subscribersTable, companyTable;
    private DefaultTableModel tariffTableModel, subscribersTableModel, companyTableModel;
    private HashSet<Integer> tariffIds = new HashSet<>(), subscriberIds = new HashSet<>();
    private HashSet<String> companyNames = new HashSet<>();

    public ManagementSystemGUI() {
        setTitle("Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Company", createCompanyPanel());
        tabbedPane.addTab("Tariff", createTariffPanel());
        tabbedPane.addTab("Subscribers", createSubscribersPanel());
        tabbedPane.addTab("Search", createSearchPanel());


        add(tabbedPane);
        loadTariffs();
        loadCompanies();
        loadsubscriber();
    }

    private void loadsubscriber() {
        subscribersTableModel.setRowCount(0); // Очищаем таблицу перед загрузкой

        for (Subscriber subscriber : DatabaseManager.getAllSubscribers()) {
            subscribersTableModel.addRow(new Object[]{
                    subscriber.getId(),
                    subscriber.getName(),
                    subscriber.getPhone(),
                    subscriber.getBalance(),
                    subscriber.getTariff() != null ? subscriber.getTariff() : "None"
            });
        }
    }


    public void loadCompanies() {
        companyNames.clear();
        companyTableModel.setRowCount(0); // Очищаем таблицу перед загрузкой

        String sql = "SELECT name FROM company";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String name = rs.getString("name");
                companyNames.add(name);
                companyTableModel.addRow(new Object[]{name});
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "Error loading companies from database!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private static final String URL = "jdbc:mysql://localhost:3307/sys";
    private static final String USER = "root";
    private static final String PASSWORD = "MS19AK47";
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }


    private JPanel createCompanyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        companyTableModel = new DefaultTableModel(new Object[]{"Company Name"}, 0);
        companyTable = new JTable(companyTableModel);
        JScrollPane scrollPane = new JScrollPane(companyTable);

        addCompany("Beeline");
        addCompany("Tele2");

        JPanel buttonPanel = new JPanel();

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void addCompany(String name) {
        if (!companyNames.contains(name)) {
            companyNames.add(name);
            companyTableModel.addRow(new Object[]{name});
        }
    }

    private JPanel createTariffPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        tariffTableModel = new DefaultTableModel(new Object[]{"ID", "Tariff Name", "Price", "Company"}, 0);
        tariffTable = new JTable(tariffTableModel);
        JScrollPane scrollPane = new JScrollPane(tariffTable);

        loadTariffs();

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Tariff");
        JButton deleteButton = new JButton("Delete Tariff");

        addButton.addActionListener(this::addTariff);
        deleteButton.addActionListener(this::deleteTariff);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void loadTariffs() {
        tariffTableModel.setRowCount(0);

        for (Tariff tariff : DatabaseManager.getAllTariffs()) {
            tariffTableModel.addRow(new Object[]{tariff.getId(), tariff.getTariffName(), tariff.getPrice(), tariff.getCompany()});
        }
    }
    private void refreshSubscribersTable() {
        subscribersTableModel.setRowCount(0);
        for (Subscriber subscriber : DatabaseManager.getAllSubscribers()) {
            subscribersTableModel.addRow(new Object[]{
                    subscriber.getId(),
                    subscriber.getName(),
                    subscriber.getPhone(),
                    subscriber.getBalance(),
                    subscriber.getTariff() != null ? subscriber.getTariff() : "None"
            });
        }
    }

    private void refreshTariffsTable() {
        tariffTableModel.setRowCount(0);
        for (Tariff tariff : DatabaseManager.getAllTariffs()) {
            tariffTableModel.addRow(new Object[]{
                    tariff.getId(),
                    tariff.getTariffName(),
                    tariff.getPrice(),
                    tariff.getCompany()
            });
        }
    }

    private void addTariff(ActionEvent e) {
        try {
            String idStr = JOptionPane.showInputDialog("Enter Tariff ID:");
            if (idStr == null || idStr.trim().isEmpty()) return; // Отмена или пустой ввод

            int id = Integer.parseInt(idStr.trim());
            if (id <= 0) {
                JOptionPane.showMessageDialog(null, "Invalid ID!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String name = JOptionPane.showInputDialog("Enter Tariff Name:");
            if (name == null || name.trim().isEmpty()) return;

            String priceStr = JOptionPane.showInputDialog("Enter Price:");
            if (priceStr == null || priceStr.trim().isEmpty()) return;

            double price = Double.parseDouble(priceStr.trim());
            if (price < 0) {
                JOptionPane.showMessageDialog(null, "Price cannot be negative!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String company = JOptionPane.showInputDialog("Enter Company Name:");
            if (company == null || company.trim().isEmpty()) return;

            if (!DatabaseManager.companyExists(company)) {
                JOptionPane.showMessageDialog(null, "Company does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (DatabaseManager.addTariff(id, name, price, company)) {
                refreshTariffsTable(); // Перезагружаем таблицу
                JOptionPane.showMessageDialog(null, "Tariff added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Tariff with this ID already exists!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Invalid number format!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "An error occurred: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteTariff(ActionEvent e) {
        int selectedRow = tariffTable.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) tariffTableModel.getValueAt(selectedRow, 0);

            int confirm = JOptionPane.showConfirmDialog(null, "Are you sure you want to delete this tariff?",
                    "Confirm Deletion", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                if (DatabaseManager.deleteTariff(id)) {
                    refreshTariffsTable(); // Перезагружаем таблицу после удаления
                    JOptionPane.showMessageDialog(null, "Tariff deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to delete tariff!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a tariff to delete!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private JPanel createSubscribersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        subscribersTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Phone", "Balance", "Tariff"}, 0);
        subscribersTable = new JTable(subscribersTableModel);
        JScrollPane scrollPane = new JScrollPane(subscribersTable);

        JPanel buttonPanel = new JPanel();
        JButton addButton = new JButton("Add Subscriber");
        JButton deleteButton = new JButton("Delete Subscriber");
        JButton updateButton = new JButton("Update Subscriber");
        JButton buyTariffButton = new JButton("Buy Tariff");

        addButton.addActionListener(this::addSubscriber);
        deleteButton.addActionListener(this::deleteSubscriber);
        updateButton.addActionListener(this::updateSubscriber);
        buyTariffButton.addActionListener(this::buyTariff);

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(buyTariffButton);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void addSubscriber(ActionEvent actionEvent) {
        try {
            int id = Integer.parseInt(JOptionPane.showInputDialog("Enter Subscriber ID:").trim());
            String name = JOptionPane.showInputDialog("Enter Name:").trim();
            String number = JOptionPane.showInputDialog("Enter Phone:").trim();
            double balance = Double.parseDouble(JOptionPane.showInputDialog("Enter Balance:").trim());
            String tariff = JOptionPane.showInputDialog("Enter Tariff:").trim();
            if(tariff != null){
                tariff = tariff.trim();
                if(tariff.isEmpty()){
                    tariff = "None";
                }
                else if(!tariffExists(tariff)){
                    JOptionPane.showMessageDialog(null, "Tariff does not exist!", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            if (DatabaseManager.addSubscriber(id, name, number, balance, tariff)) {
                subscribersTableModel.addRow(new Object[]{id, name, number, balance, "None"});
                refreshSubscribersTable();

                JOptionPane.showMessageDialog(null, "Subscriber added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                refreshSubscribersTable();

            } else {
                JOptionPane.showMessageDialog(null, "Failed to add subscriber!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(null, "Invalid input!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private void updateSubscriber(ActionEvent actionEvent) {
        int selectedRow = subscribersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a subscriber to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int subscriberid = (int) subscribersTable.getValueAt(selectedRow, 0); // Получаем ID подписчика

        String newName = JOptionPane.showInputDialog(this, "Enter new Subscriber Name:",
                subscribersTable.getValueAt(selectedRow, 1));
        if (newName == null || newName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Subscriber name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String newPhone = JOptionPane.showInputDialog(this, "Enter new Phone Number:",
                subscribersTable.getValueAt(selectedRow, 2));
        if (newPhone == null || newPhone.trim().isEmpty() || !newPhone.matches("\\d+")) {
            JOptionPane.showMessageDialog(this, "Phone number must contain only digits and cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        DefaultTableModel model = (DefaultTableModel) subscribersTable.getModel();
        for (int i = 0; i < model.getRowCount(); i++) {
            if (i != selectedRow && model.getValueAt(i, 2).equals(newPhone)) {
                JOptionPane.showMessageDialog(this, "This phone number is already in use!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        String newBalanceStr = JOptionPane.showInputDialog(this, "Enter new Balance:",
                subscribersTable.getValueAt(selectedRow, 3)); // Получаем старое значение из таблицы

        if (newBalanceStr == null || newBalanceStr.trim().isEmpty() || !newBalanceStr.matches("\\d+(\\.\\d{1,2})?")) {
            JOptionPane.showMessageDialog(this, "Balance must be a valid number!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE subscriber SET name = ?, number = ?, balance = ? WHERE id = ?")) {

            stmt.setString(1, newName.trim());
            stmt.setString(2, newPhone.trim());
            stmt.setDouble(3, Double.parseDouble(newBalanceStr));
            stmt.setInt(4, subscriberid);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                model.setValueAt(newName.trim(), selectedRow, 1);
                model.setValueAt(newPhone.trim(), selectedRow, 2);
                model.setValueAt(newBalanceStr, selectedRow, 3);
                refreshSubscribersTable();
                JOptionPane.showMessageDialog(this, "Subscriber updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update subscriber!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }


        double newBalance = Double.parseDouble(newBalanceStr);

        model.setValueAt(newName.trim(), selectedRow, 1);
        model.setValueAt(newPhone.trim(), selectedRow, 2);
        model.setValueAt(newBalance, selectedRow, 3);
        loadsubscriber(); // Перезагрузка всех подписчиков

        refreshSubscribersTable();

        JOptionPane.showMessageDialog(this, "Subscriber updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }


    private void deleteSubscriber(ActionEvent e) {
        int selectedRow = subscribersTable.getSelectedRow();

        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(null, "Select a subscriber to delete!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int id = (int) subscribersTableModel.getValueAt(selectedRow, 0); // Получаем ID пользователя

        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement("DELETE FROM subscriber WHERE id = ?")) {

            stmt.setInt(1, id);
            int affectedRows = stmt.executeUpdate(); // Выполняем удаление

            if (affectedRows > 0) {
                // Удаляем из списка и таблицы интерфейса
                subscriberIds.remove(Integer.valueOf(id));
                subscribersTableModel.removeRow(selectedRow);
                JOptionPane.showMessageDialog(null, "Subscriber deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "Failed to delete subscriber!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }

        // Обновляем таблицу
        refreshSubscribersTable();
        loadsubscriber();
    }



    private void buyTariff(ActionEvent e) {
        int selectedRow = subscribersTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a subscriber first!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String tariffName = JOptionPane.showInputDialog("Enter Tariff Name:");
        if (tariffName == null || tariffName.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Tariff name cannot be empty!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!tariffExists(tariffName)) {  // Проверка тарифа через БД
            JOptionPane.showMessageDialog(this, "Tariff not found!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        double tariffPrice = getTariffPrice(tariffName); // Получение цены из БД
        if (tariffPrice == -1) {
            JOptionPane.showMessageDialog(this, "Error retrieving tariff price!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Object balanceObj = subscribersTableModel.getValueAt(selectedRow, 3);
        double balance;

        if (balanceObj instanceof Number) {
            balance = ((Number) balanceObj).doubleValue();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid balance value!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (balance < tariffPrice) {
            JOptionPane.showMessageDialog(this, "Insufficient balance!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        balance -= tariffPrice;

        // Обновление в базе данных
        int subscriberId = (int) subscribersTableModel.getValueAt(selectedRow, 0);
        String sql = "UPDATE subscriber SET balance = ?, tariff = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setDouble(1, balance);
            stmt.setString(2, tariffName);
            stmt.setInt(3, subscriberId);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                // Только теперь обновляем таблицу
                subscribersTableModel.setValueAt(balance, selectedRow, 3);
                subscribersTableModel.setValueAt(tariffName, selectedRow, 4);
                refreshSubscribersTable();
                refreshTariffsTable();


                JOptionPane.showMessageDialog(this, "Tariff purchased successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Failed to update database!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static boolean tariffExists(String name) {
        String sql = "SELECT COUNT(*) FROM tariff WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static double getTariffPrice(String name) {
        String sql = "SELECT price FROM tariff WHERE name = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, name);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("price");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }
    private JPanel createSearchPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JLabel label = new JLabel("Search Phone:");
        JTextField searchField = new JTextField(15);
        JButton searchButton = new JButton("🔍");
        searchButton.setPreferredSize(new Dimension(40, 25));

        searchButton.addActionListener(e -> {
            String phoneNumber = searchField.getText().trim();
            if (phoneNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a phone number to search.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            DefaultTableModel model = (DefaultTableModel) subscribersTable.getModel();
            boolean found = false;
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 2).equals(phoneNumber)) {
                    String name = (String) model.getValueAt(i, 1);
                    String tariff = (String) model.getValueAt(i, 4); // Получаем тариф

                    JOptionPane.showMessageDialog(this,
                            "Subscriber Found!\nName: " + name + "\nPhone: " + phoneNumber + "\nTariff: " + tariff,
                            "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    found = true;
                    break;
                }
            }

            if (!found) {
                JOptionPane.showMessageDialog(this, "No subscriber found with phone number: " + phoneNumber,
                        "Not Found", JOptionPane.WARNING_MESSAGE);
            }
        });

        panel.add(label);
        panel.add(searchField);
        panel.add(searchButton);
        return panel;
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagementSystemGUI().setVisible(true));
    }
}
