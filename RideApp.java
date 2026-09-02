import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * A simple data class to represent a geographical location.
 */
class Location {
    String name;
    double latitude;
    double longitude;

    public Location(String name, double latitude, double longitude) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    @Override
    public String toString() {
        return name;
    }
}

/**
 * Represents a driver and their vehicle.
 */
class Ride {
    String driverName;
    String carModel;
    Location currentLocation;

    public Ride(String driverName, String carModel, Location currentLocation) {
        this.driverName = driverName;
        this.carModel = carModel;
        this.currentLocation = currentLocation;
    }
}

/**
 * A helper class to present a complete ride option to the user,
 * including the ride itself, the estimated cost, and how far the driver is.
 */
class RideOption {
    Ride ride;
    double estimatedCost;
    double driverDistanceToPickup;

    public RideOption(Ride ride, double estimatedCost, double driverDistanceToPickup) {
        this.ride = ride;
        this.estimatedCost = estimatedCost;
        this.driverDistanceToPickup = driverDistanceToPickup;
    }
}

/**
 * A utility class for all calculations related to rides.
 */
class RideCalculator {

    // --- Pricing Configuration ---
    private static final double BASE_FARE = 2.50; // e.g., $2.50
    private static final double PER_KM_RATE = 1.75; // e.g., $1.75 per km

    /**
     * Calculates the great-circle distance between two points on Earth
     * using the Haversine formula.
     * @return Distance in kilometers.
     */
    public static double calculateDistance(Location loc1, Location loc2) {
        final int R = 6371; // Radius of the earth in kilometers

        double latDistance = Math.toRadians(loc2.latitude - loc1.latitude);
        double lonDistance = Math.toRadians(loc2.longitude - loc1.longitude);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(loc1.latitude)) * Math.cos(Math.toRadians(loc2.latitude))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // converts to kilometers
    }

    /**
     * Calculates the fare for a trip based on its distance.
     * @param distanceInKm The total distance of the trip.
     * @return The total estimated cost.
     */
    public static double calculateFare(double distanceInKm) {
        return BASE_FARE + (distanceInKm * PER_KM_RATE);
    }
}

/**
 * The main application class.
 */
public class RideApp {

    private List<Ride> availableRides;
    private Map<String, Location> locationMap;
    private Scanner scanner;

    public RideApp() {
        this.availableRides = new ArrayList<>();
        this.locationMap = new HashMap<>();
        this.scanner = new Scanner(System.in);
        initializeData();
    }

    /**
     * Sets up the sample data for locations and available drivers.
     */
    private void initializeData() {
        // 1. Define known locations
        Location airport = new Location("Airport", 40.6413, -73.7781);
        Location timesSquare = new Location("Times Square", 40.7580, -73.9855);
        Location centralPark = new Location("Central Park", 40.7829, -73.9654);
        Location brooklynBridge = new Location("Brooklyn Bridge", 40.7061, -73.9969);
        Location wallStreet = new Location("Wall Street", 40.7074, -74.0113);

        locationMap.put("airport", airport);
        locationMap.put("times square", timesSquare);
        locationMap.put("central park", centralPark);
        locationMap.put("brooklyn bridge", brooklynBridge);
        locationMap.put("wall street", wallStreet);

        // 2. Define available drivers and their current locations
        availableRides.add(new Ride("Alice", "Toyota Camry", timesSquare));
        availableRides.add(new Ride("Bob", "Honda Civic", centralPark));
        availableRides.add(new Ride("Charlie", "Ford Mustang", brooklynBridge));
        availableRides.add(new Ride("Diana", "Tesla Model 3", wallStreet));
        availableRides.add(new Ride("Eve", "BMW X5", airport)); // A driver far from the city center
    }

    /**
     * The main loop of the application.
     */
    public void start() {
        System.out.println("--- Welcome to the Java Ride Finder ---");

        // 1. Get Pickup Location
        System.out.print("Enter pickup location (e.g., Times Square, Central Park): ");
        String pickupName = scanner.nextLine().toLowerCase().trim();
        Location pickupLocation = locationMap.get(pickupName);

        if (pickupLocation == null) {
            System.out.println("Error: Pickup location not found. Please try again.");
            return;
        }

        // 2. Get Drop-off Location
        System.out.print("Enter drop-off location (e.g., Brooklyn Bridge, Wall Street): ");
        String dropName = scanner.nextLine().toLowerCase().trim();
        Location dropLocation = locationMap.get(dropName);

        if (dropLocation == null) {
            System.out.println("Error: Drop-off location not found. Please try again.");
            return;
        }

        // 3. Find and Display Available Rides
        System.out.println("\nFinding rides near " + pickupLocation.name + "...");
        List<RideOption> foundRides = findRides(pickupLocation, dropLocation);
        displayRides(foundRides);
        
        scanner.close();
    }

    /**
     * Finds available rides based on pickup and drop locations.
     * @return A list of RideOptions.
     */
    private List<RideOption> findRides(Location pickup, Location drop) {
        List<RideOption> options = new ArrayList<>();
        double tripDistance = RideCalculator.calculateDistance(pickup, drop);
        double tripFare = RideCalculator.calculateFare(tripDistance);

        // Define a "nearby" threshold in kilometers
        final double NEARBY_THRESHOLD_KM = 10.0;

        for (Ride ride : availableRides) {
            double driverDistance = RideCalculator.calculateDistance(ride.currentLocation, pickup);

            // If the driver is within the nearby threshold
            if (driverDistance <= NEARBY_THRESHOLD_KM) {
                options.add(new RideOption(ride, tripFare, driverDistance));
            }
        }
        return options;
    }

    /**
     * Displays the found rides to the user in a formatted way.
     */
    private void displayRides(List<RideOption> rides) {
        if (rides.isEmpty()) {
            System.out.println("Sorry, no rides are available near your location at the moment.");
            return;
        }

        System.out.println("\n--- Available Rides ---");
        System.out.printf("%-15s %-15s %-25s %-20s %-15s\n", "Driver Name", "Car Model", "Driver Location", "Distance to You", "Estimated Cost");
        System.out.println("---------------------------------------------------------------------------------------------------------");

        for (RideOption option : rides) {
            System.out.printf("%-15s %-15s %-25s %-20.2f km $%-14.2f\n",
                    option.ride.driverName,
                    option.ride.carModel,
                    option.ride.currentLocation.name,
                    option.driverDistanceToPickup,
                    option.estimatedCost);
        }
        System.out.println("---------------------------------------------------------------------------------------------------------");
    }

    public static void main(String[] args) {
        RideApp app = new RideApp();
        app.start();
    }
}