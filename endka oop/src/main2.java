import java.util.Scanner;
import javax.swing.SwingUtilities;
import java.util.List;

public class main2 {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ManagementSystemGUI gui = new ManagementSystemGUI();
            gui.setVisible(true);
        });

        Scanner scanner = new Scanner(System.in);
        Company company1 = new Company("Beeline");
        Company company2 = new Company("Tele2");

        Tariff tariff1 = new Tariff(1, "1500", 1500, "Beeline");
        Tariff tariff2 = new Tariff(2, "1700", 1700, "Tele2");

        company1.addTariff(tariff1);
        company2.addTariff(tariff2);

        Subscriber subscriber1 = new Subscriber(1, "Alisher", "7752121122", 0, "1500");
        Subscriber subscriber2 = new Subscriber(2, "Rakhat", "7713351111", 0, "1500");
        Subscriber subscriber3 = new Subscriber(3, "Aa", "1122333", 0, "1700");

        while (true) {
            System.out.println("\n--- Menu ---");
            System.out.println("1. View Available Tariffs");
            System.out.println("2. Add Subscriber");
            System.out.println("3. Display Subscribers");
            System.out.println("4. Replenish Subscriber Balance");
            System.out.println("5. Search Subscriber by Phone");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    displayTariffs(company1, company2);
                    break;
                case 2:
                    addSubscriber(scanner, company1, company2);
                    break;
                case 3:
                    displaySubscribers(company1);
                    displaySubscribers(company2);
                    break;
                case 4:
                    replenishBalance(scanner, company1, company2);
                    break;
                case 5:
                    searchSubscriber(scanner, company1, company2);
                    break;
                case 6:
                    System.out.println("Exiting...");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Try again.");
            }
        }
    }

    private static void displayTariffs(Company company1, Company company2) {
        System.out.println("Available Tariffs:");
        System.out.println(company1.getCompanyName() + ": " + company1.getTariffs());
        System.out.println(company2.getCompanyName() + ": " + company2.getTariffs());
    }

    private static void addSubscriber(Scanner scanner, Company company1, Company company2) {
        System.out.print("Enter subscriber name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter tariff name: ");
        String tariff = scanner.nextLine();

        Tariff selectedTariff = findTariff(company1, company2, tariff);
        if (selectedTariff != null) {
            Subscriber newSubscriber = new Subscriber(0, name, phone, 0, tariff);
            selectedTariff.addSubscriber(newSubscriber);
            System.out.println("Subscriber added successfully.");
        } else {
            System.out.println("Tariff not found.");
        }
    }

    private static Tariff findTariff(Company company1, Company company2, String tariffName) {
        return company1.getTariffs().stream()
                .filter(t -> t.getTariffName().equalsIgnoreCase(tariffName))
                .findFirst()
                .orElseGet(() -> company2.getTariffs().stream()
                        .filter(t -> t.getTariffName().equalsIgnoreCase(tariffName))
                        .findFirst()
                        .orElse(null));
    }

    private static void displaySubscribers(Company company) {
        System.out.println("Subscribers of " + company.getCompanyName() + ":");
        for (Tariff tariff : company.getTariffs()) {
            List<Subscriber> subscribers = tariff.getSubscribers();
            for (Subscriber subscriber : subscribers) {
                System.out.println(subscriber);
            }
        }
    }

    private static void replenishBalance(Scanner scanner, Company company1, Company company2) {
        System.out.print("Enter subscriber phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter amount to replenish: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();

        Subscriber subscriber = findSubscriberByPhone(company1, company2, phone);
        if (subscriber != null) {
            subscriber.replenishAccount(amount);
            System.out.println("New balance: " + subscriber.getBalance());
        } else {
            System.out.println("Subscriber not found.");
        }
    }

    private static void searchSubscriber(Scanner scanner, Company company1, Company company2) {
        System.out.print("Enter subscriber phone number: ");
        String phone = scanner.nextLine();

        Subscriber subscriber = findSubscriberByPhone(company1, company2, phone);
        if (subscriber != null) {
            System.out.println("Subscriber found: " + subscriber);
        } else {
            System.out.println("Subscriber not found.");
        }
    }

    private static Subscriber findSubscriberByPhone(Company company1, Company company2, String phone) {
        for (Tariff tariff : company1.getTariffs()) {
            for (Subscriber subscriber : tariff.getSubscribers()) {
                if (subscriber.getPhone().equals(phone)) {
                    return subscriber;
                }
            }
        }
        for (Tariff tariff : company2.getTariffs()) {
            for (Subscriber subscriber : tariff.getSubscribers()) {
                if (subscriber.getPhone().equals(phone)) {
                    return subscriber;
                }
            }
        }
        return null;
    }
}
