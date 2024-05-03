package ACO;
//import statements to import relevant modules
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.HashMap;
import java.util.stream.Stream;
import java.io.IOException;
// Import Collections class for sorting later
import java.util.Collections; 
import java.util.Scanner;
// Import the File class and other file relatec components
import java.io.File; 
import java.nio.file.Files;
import java.nio.file.Paths;


//this code implements the Ant Colony Optimisation Algorithm to solve one dimensional bin packing problems. The dataset containing the problem instances can be found in BPP.txt


//the main class
public class Main extends ReadFile {
    //a static final integer that defines the maximum number of iterations to run
    public static final int MAX_ITERATIONS = 300;
    //a static double integer defining the minimum acceptable pheromone value for a trail, will be used in pheromone evaporation to ensure the pheromone values don't drop too low
    public static final double PH_MIN = 0.01;



    //main function (program entry point)
    public static void main(String[] args) {
        //printing the max iteration count
        System.out.print("\nMax Iterations: " + MAX_ITERATIONS + "\n");

        //problemArray is an array containing all problem instances along with their respective data
        //the system that reads the dataset and processes the data is present in the ReadFile class
        problemInstance[] problemArray = readFile();

        //looping through every problem instance in our Problem Array
        //essentially a forEach loop
        for (problemInstance problem : problemArray) {
            //defining a 2 dimensional integer array named items, will be used to store (item weight, number of items) pairs
          //{{w1,#w1},{w2,#w2},...}
            int[][] items = new int[problem.numberOfItemWeights][2];
            for (int j = 0; j < problem.numberOfItemWeights; j++) {
              //assigning the weight and number of items to an array, this array is then an element of the items array
              //items is essentially an array of arrays
                items[j][0] = problem.itemWeight[j];
                items[j][1] = problem.noOfItems[j];
            }

          //creating a new instance of the BinPackingACO class named aco. This is the class responsible for a majority for the bin packing logic
          //the constructor of the BinPackingACO class takes necessary relevant arguments to solve the bin packing problem, including the number of item weights, bin capacity, and an array containing weight, number of item pairs.
          //it's also necessary to pass the problem instance name as an argument 

          //essentially this process will be repeated for every problem Instacnce

          //theres only one bin capacity defined per problem Instance as this is merely a one dimensional bin packing problem
            BinPackingACO aco = new BinPackingACO(problem.problemInstanceName, problem.numberOfItemWeights,
                    problem.binCapacity, items);


            //solutionResult will include the bins and number of bins for a particular bin packing solution
          //this will be returned by the solve() method of the BinPackingACO class
            SolutionResult solutionResult = aco.solve(0.50);

          //getting the number of bins computed by the ACO bin packing algorithm, this data is stored as an instance of the SolutionResult class which contains properties such as the number of bins, and the contents of the bins
            int numberOfBins = solutionResult.getNumberOfBins();
          //the bins themselves are stored as an ArrayList within the solutionResult object
           ArrayList<ArrayList<Integer>> bins = solutionResult.getBins();

          //prinitng the solution for this paticular problem instance along with addtional data such as the occupeid capacity per bin
           System.out.print("Problem Instance " + problem.problemInstanceName + " Bin Packing Solution:\nNumber of bins: "+ numberOfBins+"\n");

          //prinitng the contents of each bin by looping through the contents of each bin
          for (int i = 0; i < bins.size(); i++) {
              ArrayList<Integer> binContents = bins.get(i);
              //Print contents of the bin
              System.out.print(binContents); 
              //determining the total capacity occupied by a bin by cummulatively summing it's elements
              int capacityOccupied = 0;
              for (int weight : binContents) {
                capacityOccupied += weight;
              }
            //printing the total capacity occupied by the items in each bin
              System.out.print(" Occupied Capacity: " + capacityOccupied+"/"+problem.binCapacity);
              System.out.print("\n");
          }
          
           

          //end of problemInstance for loop
        }
      //end of main method
    }

   
}


//instances of the solution result class will later be used to store data pertaining to a particular solution such as the number of bins and the contents of each bin
class SolutionResult {
  //declaring varibales that will later be used to store the number of bins and content of bins for a particular solution
    public int numberOfBins;
    public ArrayList<ArrayList<Integer>> bins;

  //the constructor for the SolutionResult class, whcih takes the number of bins, and contents of bins as an argument
    public SolutionResult(int numberOfBins, ArrayList<ArrayList<Integer>> bins) {
      //initialising the numberofbins and bin contents based on the arguments passed in the constructor
        this.numberOfBins = numberOfBins;
        this.bins = bins;
    }

  //public method used to obtain and return the number of bins for a particular solution
    public int getNumberOfBins() {
        return numberOfBins;
    }

  //public method used to obtain and return the contents of bins for a particular solution
    public ArrayList<ArrayList<Integer>> getBins() {
        return bins;
    }
}



//the BinPackingACO class will be the primarily class with most of our bin packing algorithm logic
class BinPackingACO {
  //the prorblemName of a particular problem Instance
    private String problemName;
  //the number of item weights and bin capacity of a problem instance.
  //each problem instance only has one defined bin capacity value standardised across all bins since this is a 1 dimensional bin packing algorithm
    private int numberOfItemWeights, binCapacity;
  //a 2 dimensional integer array used to store 'item weight, number of item with weight' pairs. Each of these pairs are an array
    private int[][] items;
   // Declare a 2D array of double numeric values to store pheromone values
    private double[][] pheromones;
  //defines the evaporation rate
    private double evaporationRate = 0.5;
  //defines the pheromone constant
    private double pheromoneConstant = 0.1;


  //the contstructor of the BInPackingACO class, which takes all necessary data pertaining to a problem instance to generate a solution
    public BinPackingACO(String problemName, int numberOfItemWeights, int binCapacity, int[][] items) {
        this.problemName = problemName; // Initialize the problem name
        this.numberOfItemWeights = numberOfItemWeights; // Initialize the number of item weights
        this.binCapacity = binCapacity; // Initialize the bin capacity
        this.items = items; // Initialize the items array
        this.pheromones = new double[numberOfItemWeights][numberOfItemWeights]; // Initialize the pheromone matrix
      //method to set initial pheromone values
        initializePheromones(); 
      
    }

  //method used to evaluate the fitness of a particular solution, takes three arguments including the number of bins, bin capacity and an argument 'k' which allows for potentially necessary fine tuning of the fitness calculation by adjusting the weight to fill ratio
  public double evaluateFitness(int numberOfBins, int binCapacity, double k) {
      // Calculate fitness based on the number of bins and the total capacity used
      //initial fitness value
      double fitness = 0.0;

      // Minimize the number of bins used based on the fitness value
      fitness += numberOfBins;

      // Calculate the total capacity used and occupied
      int totalCapacityUsed = numberOfBins * binCapacity;

      // Calculate the ratio of total capacity used to maximum capacity
      double fillRatio = (double) totalCapacityUsed / (numberOfBins * binCapacity);

      // Adjust fitness based on the fill ratio and parameter k
      fitness += Math.pow(1.0 - fillRatio, k);

     // Return the computed fitness value
      return fitness;
  }


  //method that will be finally used to solve a particular problem instance and return the solution, it returns an instance of the SolutionResult class, which contains all necessary data pertaining to a particular solution
  public SolutionResult solve(double k) {
    //initially declaring an empty instance of the SolutionResult class, this will be updated and used to store the bestSolution
    SolutionResult bestSolution = new SolutionResult(this.numberOfItemWeights,new ArrayList<ArrayList<Integer>>());

    //iterating MAX_ITERATION number of times, important to have a terminating criteria.
      for (int iteration = 0; iteration < Main.MAX_ITERATIONS; iteration++) {
        //generating a new solution for the problem, this will be the current solution
         SolutionResult currentSolution = generateSolution();

          // Evaluate fitness of the current solution
          double fitness = evaluateFitness(currentSolution.getNumberOfBins(), this.binCapacity, k);

        //if this current solution is apparently better than the best solution (After perfoming fitness calculations), then update the best solution, the current solution will be the new best solution
          if (fitness <  bestSolution.getNumberOfBins()) { 
            // Update best solution since fitness is better
            //the current solution is the new best solution
            bestSolution.bins =  currentSolution.getBins(); 
            bestSolution.numberOfBins =  currentSolution.getNumberOfBins();
          }

          //updating the pheromones based on the number of bins in the best solution and the number of bins in the current solution
          updatePheromones(currentSolution.getNumberOfBins(),bestSolution.getNumberOfBins());
        //evaporating pheromones after each iteration
          evaporatePheromones();
      }

    //returning the computed bestSolution as an instance of the SolutionResult class
      return bestSolution;
  }






//this method will be used to generateSolutions for a particular problem instance. It will do so several times, each time with the goal to improve on it's current solution, until it reaches the max number of iterations defined.
  private SolutionResult generateSolution() {
    // Initialize the variable to keep track of the number of bins used
      int numberOfBins = 0; 
    // Create a list to store remaining items to be packed
      ArrayList<Integer> remainingItems = new ArrayList<>(); 

      // Copy items to remainingItems list, basically flattening the array since it's easier to handle
      for (int[] item : items) { // Iterate over each item
          int weight = item[0]; // Get the weight of the item
          int count = item[1]; // Get the count of the item
          for (int j = 0; j < count; j++) { // Repeat for the count of the item
              remainingItems.add(weight); // Add the item's weight to the remaining items list
          }
      }

      // Sort the items in decreasing order of size (FFD heuristic)
     // Sort the remaining items in decreasing order of size
      //this can be done in one line thanks to the sort() and reverseOrder() methods of the Collections library
      Collections.sort(remainingItems, Collections.reverseOrder());
      // Initialize bins as an empty arrayList, this will be systematically updated later
      ArrayList<ArrayList<Integer>> bins = new ArrayList<>(); 
   

      while (!remainingItems.isEmpty()) { // Repeat until all items are packed
          int currentBinWeight = 0; // Initialize the current bin weight
          ArrayList<Integer> bin = new ArrayList<>(); // Create a new bin
          Iterator<Integer> iterator = remainingItems.iterator(); // Create an iterator for remaining items
          while (iterator.hasNext()) { // Repeat until there are no more remaining items
              int weight = iterator.next(); // Get the weight of the next item
              if (currentBinWeight + weight <= binCapacity) { // Check if adding the item does not exceed bin capacity
                  currentBinWeight += weight; // Update the current bin weight
                  bin.add(weight); // Add the item to the bin
                  iterator.remove(); // Remove the item from the remaining items list, since it had already been processed
              }
          }
          bins.add(bin); // Add the filled bin to the list of bins   
          numberOfBins++; // Increment the number of bins used
      }

      return new SolutionResult(numberOfBins,bins); // Return the total number of bins used and the list of bins along with bin contents, both are properities of an instance of the SolutionResult class
  }


//mmethod responsible for updating pheromones
    private void updatePheromones(int solutionSize, int bestSolution) {
        // Calculate the amount of pheromone to add based on the solution size and pheromone constant
        double pheromoneToAdd = pheromoneConstant / solutionSize;

        // Loop through each row of the pheromone matrix
        for (int i = 0; i < pheromones.length; i++) {
            // Loop through each column of the current row
           //basically looping through every element of the pheremone matrix
            for (int j = 0; j < pheromones[i].length; j++) {
                // Add pheromone to the current trail based on the calculated amount
                pheromones[i][j] += pheromoneToAdd; 
            }
        }
    }


  //method responsible for handling pheromone evaporation
    private void evaporatePheromones() {
        // Evaporate pheromone trails to balance exploration and exploitation
        // Loop through each row of the pheromone matrix
        for (int i = 0; i < pheromones.length; i++) {
            // Loop through each column of the current row
            //basically looping through every element of the pheremone matrix
            for (int j = 0; j < pheromones[i].length; j++) {
                // Evaporate the pheromone by reducing its value based on the evaporation rate
                pheromones[i][j] *= (1 - evaporationRate);

                // Ensure pheromone values don't fall below a minimum threshold
                // If the pheromone value is less than the minimum threshold
                if (pheromones[i][j] < Main.PH_MIN) {
                  // set it to the threshold, can't be lesser than the minimum threshold
                    pheromones[i][j] = Main.PH_MIN;
                }
            }
        }
    }


   // Method to initialize pheromone values
   private void initializePheromones() {
       for (int i = 0; i < pheromones.length; i++) { // Iterate over rows of the pheromone matrix
           Arrays.fill(pheromones[i], pheromoneConstant); // Set initial pheromone values for each item combination
       }
   }
}

