public class Subscriber {
    private int id;
    private String name;
    private String number;
    private double balance;
    private String tariff;

    public Subscriber(int id, String name, String number, double balance, String tariff) {
        this.id = id;
        this.name = name;
        this.number = number;
        this.balance = balance;
        this.tariff = tariff;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getPhone() { return number; }
    public double getBalance() { return balance; }
    public String getTariff() { return tariff; }

    public void setTariff(String tariff) {
        this.tariff = tariff;
    }

    public void replenishAccount(double amount) {
        if (amount > 0) {
            this.balance += amount;
        }
    }

    @Override
    public String toString() {
        return "Subscriber{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", phone='" + number + '\'' +
                ", balance=" + balance +
                ", tariff='" + tariff + '\'' +
                '}';
    }

}
