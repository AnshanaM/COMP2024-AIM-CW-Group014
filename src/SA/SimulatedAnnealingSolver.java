package SA;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

/** Java Implementation of SA (Simulated Annealing) algorithm for solving the 1D BPP (Bin Packing Problem)
 * @author  Aravindh Palaniguru */

public class SimulatedAnnealingSolver {
    private List<Bin> bins;
    private List<List<Item>> sets;
    private problemInstance problem;
    private double temperature;
    private double finalTemperature;
    private double coolingRate;
    private int tempLength;
    private Random random;
    private double z; // Current objective function value
    private double zStar; // Best objective function value found so far
    private boolean improvementFlag;
    private int noImprovementCount;
    private int noofsets = 13;
    private List<Bin> bestBins; //a* best solution so far
    List<double[]> dataPoints = new ArrayList<>();

    private void logDataPoint() {
        double[] dataPoint = {temperature, z, zStar, bins.size()};
        dataPoints.add(dataPoint);
    }

    private void outputData() {
        System.out.println("Temperature, Z, ZStar ,NumberOfBins");
        for (double[] dataPoint : dataPoints) {
            System.out.println(dataPoint[0] + "," + dataPoint[1] + "," + dataPoint[2] + "," + dataPoint[3]);
        }
    }
    public void generateinitsol() {
        InitialSolutionGenerator generator = new InitialSolutionGenerator(bins, sets, problem);
        generator.generate();
        this.z = calculateObjective();
        this.zStar = this.z;
        this.bestBins = deepCopyBins(this.bins);
//        logDataPoint();
    }


    public SimulatedAnnealingSolver(problemInstance problem, double initialTemperature, double finalTemperature, double coolingRate, int tempLength) {
        this.problem = problem;
        this.temperature = initialTemperature;
        this.finalTemperature = finalTemperature;
        this.coolingRate = coolingRate;
        this.tempLength = tempLength;
        this.random = new Random();
        this.bins = new ArrayList<>();
        this.sets = new ArrayList<>();
        for (int i = 0; i < noofsets; i++) {
            this.sets.add(new ArrayList<>());
        }
        this.z = Double.MAX_VALUE; // Initialize with worst case
        this.zStar = Double.MAX_VALUE; // Initialize with worst case
    }

    private Bin findBinOfItem(Item item) {
        // Search through all bins to find the one containing the item
        for (Bin bin : bins) {
            if (bin.containsItem(item)) {
                return bin;
            }
        }
        return null; // Return null if the item is not found in any bin
    }

    public void distributeItemsIntoSets() {
        List<Item> allItems = new ArrayList<>();
        // Aggregate items
        for (int i = 0; i < problem.numberOfItemWeights; i++) {
            for (int j = 0; j < problem.noOfItems[i]; j++) {
                allItems.add(new Item(problem.itemWeight[i], -1)); // Temporarily set setIndex as -1
            }
        }

        // Randomize item order to prevent bias in distribution
        Collections.shuffle(allItems, random);

        // Sort items by weight in descending order
        allItems.sort((item1, item2) -> item2.getWeight() - item1.getWeight());

        int totalItems = allItems.size();
        int itemsPerSet = totalItems / noofsets;
        int extraItems = totalItems % noofsets;

        int currentItemIndex = 0;
        for (int setIndex = 0; setIndex < noofsets; setIndex++) {
            for (int i = 0; i < itemsPerSet; i++) {
                if (currentItemIndex < allItems.size()) {
                    Item item = allItems.get(currentItemIndex++);
                    item.setSetIndex(setIndex); // Correctly set the setIndex
                    sets.get(setIndex).add(item);
                }
            }
            if (setIndex < extraItems && currentItemIndex < allItems.size()) {
                Item item = allItems.get(currentItemIndex++);
                item.setSetIndex(setIndex); // Correctly set the setIndex
                sets.get(setIndex).add(item);
            }
        }
    }


    private void printAllBinWeights() {
        System.out.println("Current state of all bins:");
        int binIndex = 1;
        for (Bin bin : bins) {
            List<Item> items = bin.getItems(); // Assuming getItems() returns the List<Item> in the Bin
            System.out.println("Bin " + binIndex + ": Total Load = " + bin.currentLoad());
            System.out.print("Items' weights in this bin: [");
            for (int i = 0; i < items.size(); i++) {
                System.out.print(items.get(i).getWeight());
                if (i < items.size() - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println("]");
            binIndex++;
        }
    }
    private void performSwapAndEvaluate() {
        Bin maxWeightBin = bins.stream()
                .max(Comparator.comparingDouble(Bin::currentLoad))
                .orElse(null);

        if (maxWeightBin == null) {
//            System.out.println("Error: Failed to find a bin with the maximum load.");
            return;
        }

        List<Integer> feasibleSetIndices = getFeasibleSetIndices(maxWeightBin);
        if (feasibleSetIndices.isEmpty()) {
//            System.out.println("No action taken: No sets contain items in the heaviest bin.");
            return;
        }

        int randomSetIndex = feasibleSetIndices.get(random.nextInt(feasibleSetIndices.size()));
        List<Item> selectedSet = sets.get(randomSetIndex);

//        System.out.println("BEGIN");
//        System.out.println("Selected Set " + randomSetIndex + " contents:");
//        selectedSet.forEach(item -> System.out.println("Item Weight: " + item.getWeight()));
//
//        System.out.println("Heaviest Bin " + maxWeightBin.getId() + " contents:");
//        maxWeightBin.getItems().forEach(item -> System.out.println("Weight: " + item.getWeight()));

        Item itemInMaxBin = selectRandomItemFromSetInBin(selectedSet, maxWeightBin);
        Item itemToSwap = selectAnotherRandomItem(selectedSet, itemInMaxBin);
        if (itemInMaxBin == null || itemToSwap == null) {
//            System.out.println("Swap skipped: Failed to find suitable items for the swap.");
            return;
        }

        Bin binOfItemToSwap = findBinOfItem(itemToSwap);

//        System.out.println("Swap attempted between:");
//        System.out.println("Item in Max Bin: Weight = " + itemInMaxBin.getWeight() + ", Total Weight of Bin = " + maxWeightBin.currentLoad());
//        System.out.println("Item to Swap: Weight = " + itemToSwap.getWeight() + ", Total Weight of Bin = " + (binOfItemToSwap != null ? binOfItemToSwap.currentLoad() : "Unknown"));

        boolean removedFromMaxBin = maxWeightBin.removeItem(itemInMaxBin);
        boolean removedFromSwapBin = binOfItemToSwap.removeItem(itemToSwap);
        if (!removedFromMaxBin || !removedFromSwapBin) {
//            System.out.println("Swap failed: Unable to remove items from bins for the swap.");
            return;
        }

        try {
            boolean addedToMaxBin = maxWeightBin.addItem(itemToSwap);
            boolean addedToSwapBin = binOfItemToSwap.addItem(itemInMaxBin);
            if (!addedToMaxBin || !addedToSwapBin) {
//                System.out.println("Swap rejected due to capacity issues.");
                revertSwap(maxWeightBin, itemInMaxBin, binOfItemToSwap, itemToSwap);
                return;
            }

            double newObjective = calculateObjective();
            double delta = newObjective - z;
            double rand = random.nextDouble();
            double sa = Math.exp(-delta / temperature);
            if (delta < 0) {
                z = newObjective;
                if (z < zStar) {
                    zStar = z;
                    bestBins = deepCopyBins(bins);
                }
                improvementFlag = true;
//                System.out.println("Z: " + z + " Swap successful: Improvement found with new objective " + newObjective + ".");
            } else if (rand < sa) {
                z = newObjective;
//                System.out.println("Random number: " + rand + ", SA number: " + sa);
//                System.out.println("Z: " + z + " Swap successful: Change accepted by simulated annealing criteria with new objective " + newObjective + ".");
            } else {
//                System.out.println("Random number: " + rand + ", SA number: " + sa);
//                System.out.println("Swap rejected: No improvement in objective (" + z + ") and simulated annealing criteria not met.");
                revertSwap(maxWeightBin, itemInMaxBin, binOfItemToSwap, itemToSwap);
            }
        } catch (IllegalStateException e) {
//            System.out.println("Critical error during swap: " + e.getMessage() + " Reverting changes.");
            revertSwap(maxWeightBin, itemInMaxBin, binOfItemToSwap, itemToSwap);
        }

//        // Log the state of bins after the swap
//        System.out.println("After swap:");
//        System.out.println("Bin A ID: " + maxWeightBin.getId() + ", Total Weight: " + maxWeightBin.currentLoad());
//        maxWeightBin.getItems().forEach(item -> System.out.println("Item Weight: " + item.getWeight()));
//        System.out.println("Bin B ID: " + (binOfItemToSwap != null ? binOfItemToSwap.getId() : "Unknown") + ", Total Weight: " + (binOfItemToSwap != null ? binOfItemToSwap.currentLoad() : "Unknown"));
//        binOfItemToSwap.getItems().forEach(item -> System.out.println("Item Weight: " + item.getWeight()));
    }




    private List<Integer> getFeasibleSetIndices(Bin maxWeightBin) {
        List<Integer> feasibleSets = new ArrayList<>();
        for (int i = 0; i < sets.size(); i++) {
            List<Item> set = sets.get(i);
            boolean hasItemInBin = set.stream().anyMatch(maxWeightBin::containsItem);
            if (hasItemInBin) {
                feasibleSets.add(i);
            }
        }
        return feasibleSets;
    }

    private void revertSwap(Bin binOne, Item itemOne, Bin binTwo, Item itemTwo) {
//        System.out.println("Reverting swap between Bin " + binOne.getId() + " and Bin " + binTwo.getId() + ".");
        binOne.removeItem(itemTwo);
        binTwo.removeItem(itemOne);
        if (!binOne.addItem(itemOne) || !binTwo.addItem(itemTwo)) {
//            System.out.println("Critical error: Failed to revert items to their original bins.");
        } else {
//            System.out.println("Reversion successful: Items returned to their original bins.");
        }
    }

    private Item selectRandomItemFromSetInBin(List<Item> set, Bin maxWeightBin) {
        // Find all items in the set that are also in the maxWeightBin
        List<Item> itemsInBin = set.stream()
                .filter(item -> maxWeightBin.containsItem(item))
                .collect(Collectors.toList());

        if (itemsInBin.isEmpty()) {
            // No items from the set are in the max weight bin, return null
            return null;
        }

        // Randomly select one item from those found in the bin
        return itemsInBin.get(random.nextInt(itemsInBin.size()));
    }

    private Item selectAnotherRandomItem(List<Item> set, Item excludedItem) {
        // Exclude items that are identical to the excludedItem so swap makes sense
        List<Item> possibleItems = set.stream()
                .filter(item -> !item.equals(excludedItem) && item.getWeight() != excludedItem.getWeight())
                .collect(Collectors.toList());

        if (possibleItems.isEmpty()) {
            return null; // No suitable items found for swapping
        }

        // Randomly select one item from those that are not identical
        return possibleItems.get(random.nextInt(possibleItems.size()));
    }


    private double calculateObjective() {
        int maxLoad = 0;
        for (Bin bin : bins) {
            maxLoad = Math.max(maxLoad, bin.currentLoad());
        }
        return maxLoad;
    }
    private List<Bin> deepCopyBins(List<Bin> originalBins) {
        List<Bin> copyBins = new ArrayList<>();
        for (Bin originalBin : originalBins) {
            Bin copyBin = new Bin(originalBin.getCapacity()); // Use the getter for capacity
            for (Item item : originalBin.getItems()) {
                // Assuming a copy constructor in Item class
                copyBin.addItem(new Item(item)); // Use the copy constructor for items
            }
            copyBins.add(copyBin);
        }
        return copyBins;
    }

    public int printBestSolution() {
        System.out.println("Best Solution with objective value Z = " + zStar + ":");
        double sum = 0.0;
        int binIndex = 1; // Start bin numbering at 1
        for (Bin bin : bestBins) {
            System.out.println("Bin " + binIndex + " (" + bin.currentLoad() + "/" + bin.getCapacity() + "):");
            printBinItems(bin);
            binIndex++;
            sum += Math.pow(((double) bin.currentLoad()/10000),2);
        }
        System.out.println("Cost function: " + (sum/bins.size()));
        return binIndex;
    }

    // Helper method to print the items in a bin in a formatted manner
    private void printBinItems(Bin bin) {
        List<Item> items = bin.getItems();
        if (items.isEmpty()) {
            System.out.println("  [Empty]");
        } else {
            System.out.print("  Items' weights: [");
            for (int i = 0; i < items.size(); i++) {
                System.out.print(items.get(i).getWeight());
                if (i < items.size() - 1) {
                    System.out.print(", "); // Separate items with a comma and space
                }
            }
            System.out.println("]");
        }
    }
    public void printSets() {
        System.out.println("Displaying the " + noofsets + " sets:");
        for (int i = 0; i < sets.size(); i++) {
            System.out.println("\nSet " + (i + 1) + " Weights :");
            for (Item item : sets.get(i)) {
                System.out.printf(item.getWeight()+" ");
            }
//            System.out.println("\n");
        }
    }

    public void printSolution() {
        int binNumber = 1;
        double totalWeight = 0;
        for (Bin bin : bins) {
            System.out.println("Bin " + binNumber + " (" + bin.currentLoad() + "/" + problem.binCapacity + "):");
            bin.printItems(); // Assuming Bin has a method to print its items
            totalWeight += bin.currentLoad();
            binNumber++;
        }
        System.out.println("Z: " + z);
        System.out.println("ZStar (best objective): " + zStar+"\n");
//        System.out.println("Total weight of the problem instance: " + totalWeight);
    }

    public static void main(String[] args) throws IOException {
        problemInstance[] problems = ReadFile.readFile();
        String userHomeFolder = System.getProperty("user.home");
        String desktopPath = userHomeFolder + "\\Desktop\\"; // Path to Desktop
//        String csvFileName = "simulated_annealing_results.csv"; // The name of the CSV file
//        String csvFilePath = desktopPath + csvFileName;
//        String csvFilePath = "src/SA/simulated_annealing_results.csv";

        int problemIndex = 1; // Start with 1 for naming files distinctly

//        String filePath = "outputRuns.txt";
//        FileWriter writer = new FileWriter(filePath);
//        ArrayList binRuns = new ArrayList();

//        ArrayList avgTime = new ArrayList<>();

        int instance = 1;
        for (problemInstance problem : problems) {
//            int sumTime = 0;
            //running the following 30 times
//            for (int run = 0; run < 30; run++) {
//                System.out.println("run"+run);
                System.out.println("--------------------------------------------------------------------------------------------------");
                System.out.println("Problem Instance " + instance);
                if (problem != null) {
                    double initialTemp = 100.0; // Example initial temperature
                    double finalTemp = 10; // Example final temperature
                    double cool = 0.7; // Slightly adjusted cooling rate for more gradual cooling
                    int templength = 50; // Example temperature length
                    int tempCount = 0;
                    // Instantiate the solver with the current problem instance
                    SimulatedAnnealingSolver solver = new SimulatedAnnealingSolver(problem, initialTemp, finalTemp, cool, templength);

                    // Distribute items into sets and generate initial solution
                    solver.distributeItemsIntoSets();
                    solver.generateinitsol();
                    solver.printSets();
                    System.out.println("\n\nInitial Solution:");
                    solver.printSolution();
                    solver.improvementFlag = false; // Initialize improvement flag
                    solver.noImprovementCount = 0;

                    long startTime = System.nanoTime();
                    // Now perform simulated annealing with swap and evaluate steps
                    while (solver.temperature > solver.finalTemperature) {
                        solver.improvementFlag = false; // Reset flag for each temperature level

                        for (int i = 0; i < solver.tempLength; i++) {
                            solver.performSwapAndEvaluate();
                            solver.logDataPoint();
                            if (solver.improvementFlag) {
                                tempCount = 0; // Reset counter if improvement is found
//                            System.out.println("Reset");
                            } else {
                                tempCount++;
                                if (tempCount >= solver.tempLength) {
                                    System.out.println("Termination criteria met. No improvements for a full cycle at the current temperature.");
                                    break; // Exiting early as no improvement found
                                }
                            }
                        }

                        // Cooling step
                        if (!solver.improvementFlag) {
                            solver.noImprovementCount++;
                            if (solver.noImprovementCount >= solver.tempLength) {
                                System.out.println("Termination criteria met. No improvements for " + solver.noImprovementCount + " consecutive cycles.");
                                break;
                            }
                        } else {
                            solver.noImprovementCount = 0;
                        }

                        solver.temperature *= solver.coolingRate;
                        System.out.println("Temperature cooled to: " + solver.temperature);
                        tempCount = 0;
                    }

                    long endTime = System.nanoTime(); // Record end time
                    long executionTime = endTime - startTime;
                    // Final solution
                    System.out.println("\n");
//                solver.printSolution();
                int avg = solver.printBestSolution();
//                solver.outputData();

                    // Write CSV for the current problem instance
//                solver.writeDataToCSV(csvFilePath, problemIndex);
                    problemIndex++;

                    System.out.println("Time taken for algorithm execution: " + executionTime + " nanoseconds.");
//                    sumTime+=executionTime;
                    System.out.println("--------------------------------------------------------------------------------------------------\n");


                    //adding to the problem instances bin array which stores the number of bins for 30 runs
//                    binRuns.add(bin_num-1);
                }
//                System.out.println("Average Time: "+((double)sumTime/30));
                instance++;
            }
//            avgTime.add(((double)sumTime/30));
////            printing 30 bin packings after 30 runs
//            System.out.println(problem.problemInstanceName+binRuns);
//            writer.write(problem.problemInstanceName+"\n"+binRuns + "\n");
//            binRuns.clear();
//        }
//        System.out.println("avgtime: "+avgTime);
//        writer.close();
    }
}
