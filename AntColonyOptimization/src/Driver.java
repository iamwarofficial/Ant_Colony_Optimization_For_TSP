import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class Driver {
    static final int NUMBER_OF_ANTS = 500;
    static final double PROCESSING_CYCLE_PROBABILITY = 0.8;
    static ArrayList<City> initialRoute = new ArrayList<City>(Arrays.asList(
            new City("Boston", 42.3601, -71.0589),
            new City("Houston", 29.7604, -95.3698),
            new City("Austin", 30.2672, -97.7431),
            new City("San Francisco", 37.7749, -122.4194),
            new City("Denver", 39.7392, -104.9903),
            new City("Los Angeles", 34.0522, -118.2437),
            new City("Chicago", 41.8781, -87.6298),
            new City("New York", 40.7128, -74.0059)
            //new City("Sydney", -33.8675, 151.2070),
            //new City("Tokyo", 35.6895, 139.6917),
            //new City("Cape Town", -33.9249, 18.4241)
    ));
    static ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
    static ExecutorCompletionService<Ant> executorCompletionService = new ExecutorCompletionService<Ant>(executorService);
    private Route shortestRoute = null;
    private int activeAnts = 0;
    public static void main(String[] args) throws IOException {
        System.out.println("> "+NUMBER_OF_ANTS + " Artificial Ants ...");
        Driver driver = new Driver();
        driver.printHeading();
        AntColonyOptimization aco = new AntColonyOptimization();
        IntStream.range(1, NUMBER_OF_ANTS).forEach(x -> {
            executorCompletionService.submit(new Ant(aco, x));
            driver.activeAnts++;
            if (Math.random() > PROCESSING_CYCLE_PROBABILITY) driver.processAnts();
        });
        driver.processAnts();
        executorService.shutdown();
        System.out.println("\nOptimal Route : "+Arrays.toString(driver.shortestRoute.getCities().toArray()));
        System.out.println("w/ Distance   : " + driver.shortestRoute.getDistance());
    }
    private void processAnts() {
        while (activeAnts > 0) {
            try {
                Ant ant = executorCompletionService.take().get();
                Route currentRoute = ant.getRoute();
                if (shortestRoute == null || currentRoute.getDistance() < shortestRoute.getDistance()) {
                    shortestRoute = currentRoute;
                    StringBuffer distance = new StringBuffer("       "+String.format("%.2f", currentRoute.getDistance()));
                    IntStream.range(0, 21 - distance.length()).forEach(k -> distance.append(" "));
                    System.out.println(Arrays.toString(shortestRoute.getCities().toArray()) + " |" + distance + "| "+ ant.getAntNumb());
                }
            } catch (Exception e) {e.printStackTrace();}
            activeAnts--;
        }
    }
    private void printHeading() {
        String headingColumn1 = "Route";
        String remainingHeadingColumns = "Distance (in miles) | ant #";
        int cityNamesLength = 0;
        for (int x = 0; x < initialRoute.size(); x++) cityNamesLength += initialRoute.get(x).getName().length();
        int arrayLength = cityNamesLength + initialRoute.size()*2;
        int partialLength = (arrayLength - headingColumn1.length())/2;
        for (int x=0; x < partialLength; x++) System.out.print(" ");
        System.out.print(headingColumn1);
        for (int x=0; x < partialLength; x++) System.out.print(" ");
        if ((arrayLength % 2) == 0) System.out.print(" ");
        System.out.println(" | "+ remainingHeadingColumns);
        cityNamesLength += remainingHeadingColumns.length() + 3;
        for (int x=0; x < cityNamesLength+initialRoute.size()*2; x++) System.out.print(" ");
        System.out.println("");
    }
}
