import java.io.Serializable;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The main application where the {@code RRPSS}  works. This class provides the interface for the user.
 *
 * @since 2021-11-5
 */
public class RRPSS implements Serializable {
    /**
     * {@link OrderApp} with options
     */
    private final OrderApp orderApp;
    /**
     * {@link ReservationApp} with options
     */
    private final ReservationApp reservationApp;
    /**
     * {@link MenuApp} with options
     */
    private final MenuApp menuApp;
    /**
     * {@link StaffApp} application
     */
    private final StaffApp staffApp;
    /**
     * {@link SalesReport} application
     */
    private final SalesReport salesReportApp;
    /**
     * Object Manager of Reservation,{@link ReservationMgr}
     */
    private final ReservationMgr reservationMgr;
    /**
     * Object Manager of Order,{@link OrderMgr}
     */
    private final OrderMgr orderMgr;
    /**
     * Object Manager of MenuItem,{@link MenuMgr}
     */
    private final MenuMgr menuMgr;
    /**
     * Object Manager of {@link StaffMgr}
     */
    private final StaffMgr staffMgr;

    /**
     * Class Constructor with default settings
     */
    public RRPSS() {
        salesReportApp = new SalesReport();
        orderMgr = new OrderMgr();
        menuMgr = new MenuMgr();
        reservationMgr = new ReservationMgr();
        staffMgr = new StaffMgr();
        reservationApp = new ReservationApp(reservationMgr);
        menuApp = new MenuApp(menuMgr);
        staffApp = new StaffApp(staffMgr);
        orderApp = new OrderApp(reservationMgr, orderMgr, menuMgr, salesReportApp, staffApp);
    }

    /**
     * Main function that runs the {@code RRPSS} app
     *
     * @param args it stores Java command line arguments and is an array of type java.lang.String class
     */
    public static void main(String[] args) {
        RRPSS rrpssApp;
        Database database = new Database();
        rrpssApp = (RRPSS) database.load("file.txt");
        if (rrpssApp == null) {
            System.out.println("New RRPSS system created.");
            rrpssApp = new RRPSS();
        }
        rrpssApp.rrpssOptions();
        database.save(rrpssApp, "file.txt");
        System.out.println("Saving....");
        System.out.println("Exited!");
    }

    /**
     * Interface of the application
     */
    public void rrpssOptions() {
        int choice = 0;

        while (true) {
            System.out.print("\nWelcome to Krusty Krab!\n" +
                    "Please select one of the options below:\n" +
                    "1. Manage reservations\n" +
                    "2. Manage orders\n" +
                    "3. Edit the menu\n" +
                    "4. View sales report\n" +
                    "5. Manage staff\n" +
                    "6. Quit RRPSS and save all data\n" +
                    "Enter your choice: ");
            try {
                Scanner sc = new Scanner(System.in);
                choice = sc.nextInt();
                sc.nextLine();
            } catch (InputMismatchException e) {
                System.out.println("Invalid input");
            }

            switch (choice) {
                case 1:
                    reservationApp.openOptions();
                    break;
                case 2:
                    orderApp.openOptions();
                    break;
                case 3:
                    menuApp.openOptions();
                    break;
                case 4:
                    salesReportApp.openOptions();
                    break;
                case 5:
                    staffApp.openOptions();
                    break;
                case 6:
                    return;
                default:
                    System.out.println("Invalid option. Try again!");
            }
        }
    }
}
