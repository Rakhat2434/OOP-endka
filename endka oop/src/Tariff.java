import java.util.ArrayList;
import java.util.List;

public class Tariff {
    private int id;
    private String tariffName;
    private double price;
    private Company company;
    private List<Subscriber> subscribers;


    public Tariff(int id, String name, double price, String company) {
        this.id = id;
        this.tariffName = name;
        this.price = price;
        this.company = new Company(company);
        this.subscribers = new ArrayList<>();
    }
    public int getId() { // ✅ Метод getId() добавлен
        return id;
    }
    public String getTariffName() {
        return tariffName;
    }
    public double getPrice() { // ✅ Метод getPrice() добавлен
        return price;
    }

    public String getCompany() {
        return company.getName();
    }


    public List<Subscriber> getSubscribers() {
        return subscribers;
    }

    public void addSubscriber(Subscriber subscriber) {
        subscribers.add(subscriber);
    }



}
