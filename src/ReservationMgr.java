import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * Manager of all reservations and tables.
 * Supports addition, removal and amendment of reservations.
 * @author
 * @since 2021-11-5
 */
public class ReservationMgr implements Serializable {
    /**
     * Collection of all tables in the restaurant.
     */
    private final ArrayList<Table> allTables;
    /**
     * Collection of all reservations in the restaurant.
     */
    private final ArrayList<Reservation> allReservations;
    
    /**
    * Class constructor with default settings
    */
    public ReservationMgr() {
        this.allTables = new ArrayList<>();
        this.allReservations = new ArrayList<>();
        addTables();
        createScheduler();
    }
    /**
    * Add table with default available seats    
    */
    private void addTables() {
        allTables.add(new Table(2));
        allTables.add(new Table(2));
        allTables.add(new Table(4));
        allTables.add(new Table(4));
    }
    /**
    * Return total no. of reservation
    *
    * @return       total no. of reservation
    */
    public int getTotalNoOfReservations(){
        return allReservations.size();
    }

    /**
     * Makes a reservation. Finds a suitable table that can contain the number of persons.
     * Reservation will be added to the collection and asks the table to be marked as unavailable at the specified date and time.
     * @param r Reservation.
     */
    public boolean makeReservation(Reservation r) {
        LocalDate date = r.getDate();
        LocalTime time = r.getTime();
        int noOfPax = r.getNoOfPax();
        for (Table t : allTables) {
            if (noOfPax > t.getCapacity())
                continue;
            if (t.checkAvailabilityAt(date, time)) {
                t.markAsUnavailableAt(date, time);
                r.setTableNo(allTables.indexOf(t));
                allReservations.add(r);
                System.out.println("Reservation made successfully at table number " + allTables.indexOf(t));
                return true;
            }
        }
        System.out.println("No available table found");
        return false;
    }

    /**
     * Cancels a reservation.
     * Removes the reservation from the collection and asks the table to be marked as available for other reservations at the specified date and time.
     * @param reservationNo Reservation number.
     */
    public void cancelReservation(int reservationNo) {
        Reservation r = allReservations.get(reservationNo);
        LocalDate date = r.getDate();
        LocalTime time = r.getTime();
        int tableNo = r.getNoOfPax();
        allTables.get(tableNo).markAsAvailableAt(date, time);
        allReservations.remove(reservationNo);
        System.out.println("Reservation has been cancelled");
    }
    /**
    * Update existing reservation date
    *
    * @param    reservationNo   reservation no. to be updated
    * @param    newDate         Date to be updated
    */
    public void updateReservation(int reservationNo, LocalDate newDate) {
        Reservation deepCopy = new Reservation(allReservations.get(reservationNo));
        deepCopy.setDate(newDate);
        updateReservation(reservationNo, deepCopy);
    }
    /**
    * Update existing reservation time
    *
    * @param    reservationNo   reservation no. to be updated
    * @param    newTime         Time to be updated
    */
    public void updateReservation(int reservationNo, LocalTime newTime) {
        Reservation deepCopy = new Reservation(allReservations.get(reservationNo));
        deepCopy.setTime(newTime);
        updateReservation(reservationNo, deepCopy);
    }
    /**
    * Update existing reservation no of pax
    *
    * @param    reservationNo   reservation no. to be updated
    * @param    newNoOfPax      No of pax to be updated
    */
    public void updateReservation(int reservationNo, int newNoOfPax) {
        Reservation deepCopy = new Reservation(allReservations.get(reservationNo));
        deepCopy.setNoOfPax(newNoOfPax);
        updateReservation(reservationNo, deepCopy);
    }
    /**
    * Update existing reservation customer
    *
    * @param    reservationNo   reservation no. to be updated
    * @param    newCustomer     customer to be updated
    */
    public void updateReservation(int reservationNo, Customer newCustomer) {
        allReservations.get(reservationNo).setCustomer(newCustomer);
        System.out.println("Customer details updated");
    }

    /**
     * Updates a reservation.
     * @param oldReservationNo Reservation number of the outdated reservation to be removed.
     * @param newReservation Updated reservation to be added.
     */
    private void updateReservation(int oldReservationNo, Reservation newReservation) {
        System.out.println("Reservation updated by performing the actions below");
        Reservation oldReservation = allReservations.get(oldReservationNo);
        cancelReservation(oldReservationNo);
        if (!makeReservation(newReservation)) {
            makeReservation(oldReservation);
            System.out.println("Update unsuccessful. Previous reservation restored.");
        }
        System.out.println("Update completed");
    }

    /**
     * Prints a list of all active reservations to the console.
     */
    public void viewAllReservations() {
        System.out.println("---List of all reservations---");
        for (Reservation r : allReservations)
            System.out.println("Reservation " + allReservations.indexOf(r) + ": " + r.toString());
        System.out.println("----------");
    }

    /**
     * Prints a list of all the tables and their respective availabilities to the console.
      */
    public void viewAllTables() {
        System.out.println("---List of all tables---");
        for (Table t : allTables)
            System.out.println("Table " + allTables.indexOf(t) + ": " + t.toString());
        System.out.println("----------");
    }
    /**
    * Check the availability of table 
    *
    * @param    date    the date to be check for availabity
    * @param    time    the time to be check for availabity
    * @param    noOfPax no of pax for the reservation
    */
    public void checkAvailabilityAt(LocalDate date, LocalTime time, int noOfPax) {
        boolean tableFound = false;
        for (Table t : allTables) {
            if (noOfPax > t.getCapacity())
                continue;
            if (t.checkAvailabilityAt(date, time))
                System.out.println("Table " + allTables.indexOf(t) + " is available for reservation at " + date + " " + time);
                tableFound = true;
        }
        if (!tableFound) {
            System.out.println("No tables are available for reservation at " + date + " " + time);
        }
    }
    /**
    * Remove the expired reservation
    */
    public void removeNoShowReservations() {
        allReservations.sort(Comparator.comparing(Reservation::getDate).thenComparing(Reservation::getTime));
        for (ListIterator<Reservation> it = allReservations.listIterator(); it.hasNext(); ) {
            Reservation r = it.next();
            LocalDate date = r.getDate();
            LocalTime time = r.getTime();
            int tableNo = r.getNoOfPax();
            boolean custArrived = r.getCustArrived();

            LocalDateTime now = LocalDateTime.now();
            LocalDateTime reservationDateTime = LocalDateTime.of(date, time);
            if (now.isAfter(reservationDateTime.plusMinutes(15)) && !custArrived) {
                System.out.println(r + " has expired and will be automatically removed.");
                Table t = allTables.get(tableNo);
                t.markAsAvailableAt(date, time);
                it.remove();
            }
        }
    }
    
    /**
    * Scheduler that will run the function at certain rate of time(for removing expired reservation)
    */
    public void createScheduler() {
        Timer timerSchedule = new Timer(true);
        TimerTask taskToRun = new ExpiredReservationsRemover(this);
        int minutesNow = LocalTime.now().getMinute();
        int offset;
        if (minutesNow < 15) {
            offset = (15 - minutesNow) * 1000 * 60;
        } else {
            offset = (75 - minutesNow) * 1000 * 60;
        }
        timerSchedule.scheduleAtFixedRate(taskToRun, offset, 3600000);
    }
    /**
    * View the list of table with reservations
    */
    public void viewTablesWithReservationsNow() {
        System.out.println("---List of tables with reservations now---");
        for (Table t : allTables) {
            // tables with a reservation now are not available for another reservation
            if (!t.checkAvailabilityAt(LocalDate.now(), LocalTime.now()))
                System.out.println("Table " + allTables.indexOf(t) + ": " + t);
        }
        System.out.println("----------");
    }
    
    /**
    * Return customer at the specific table
    *
    * @param    tableNo the index no of table
    * @return           the customer of that specific table
    */
    public Customer getCustomerAt(int tableNo) {
        for (Reservation r : allReservations) {
            LocalDate date = r.getDate();
            LocalTime time = r.getTime();
            if (r.getTableNo() == tableNo && date.isEqual(LocalDate.now()) && time.getHour() == LocalTime.now().getHour()) {
                r.setCustArrived(true);
                return r.getCustomer();
            }
        }
        return null;
    }
    /**
    * Remove reservation on a table that has already made payment
    */
    public void removeReservationAfterPayment(int tableNo) {
        for (ListIterator<Reservation> it = allReservations.listIterator(); it.hasNext(); ) {
            Reservation r = it.next();
            LocalDate date = r.getDate();
            LocalTime time = r.getTime();
            if (r.getTableNo() == tableNo && date.isEqual(LocalDate.now()) && time.getHour() == LocalTime.now().getHour()) {
                allTables.get(r.getTableNo()).markAsAvailableAt(r.getDate(), r.getTime());
                it.remove();
            }
        }
    }
}
