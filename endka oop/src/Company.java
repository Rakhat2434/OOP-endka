import java.util.ArrayList;
import java.util.List;

public class Company {
    private String companyName;
    private List<Tariff> tariffs;

    public Company(String companyName) {
        this.companyName = companyName;
        this.tariffs = new ArrayList<>();
    }

    public String getCompanyName() {
        return companyName;
    }

    public List<Tariff> getTariffs() {
        return tariffs;
    }

    public void addTariff(Tariff tariff) {
        tariffs.add(tariff);
    }



    public String getName() {
        return companyName;
    }
}
