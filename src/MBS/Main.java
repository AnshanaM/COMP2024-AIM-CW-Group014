package MBS;

import java.util.ArrayList;

/** Java Implementation of MBS (Minimum Bin Slack) algorithm for solving the 1D BPP (Bin Packing Problem)
 * @author  Anshana Manoharan */

public class Main {
    // Pseudocode
    // call MBS(1)
    // function MBS(q)
    //     for r = q to n'
    //         i = Z'[r]
    //         if weighti <= s(A) then
    //             A = A U {i}
    //             Apply MBS(r+1)
    //             A = A \ {i}
    //             if s(A*) = 0 then
    //                 end
    //     if s(A) <= s(A*) then
    //         A* = A

    static final int TOTAL_PROBLEM_INSTANCES = 5;

    public static void main(String[] args) {
        System.out.println("\n***************************************************************************");
        System.out.println("One-Dimensional Bin Packing Problem");
        System.out.println("Minimum Bin Slack Algorithm");

//        ArrayList avgTime = new ArrayList<>();

        //problemArray stores all 5 problem instances
        ProblemInstance[] problemArray = ReadFile.readFile();
        //iterate through problem array and display the problem instances
        for (int problem = 0; problem < TOTAL_PROBLEM_INSTANCES; problem++) {
//            int sumTime = 0;
//            for (int run = 0 ; run < 30 ; run++ ){

                System.out.println("\n***************************************************************************");
                System.out.println("Generating Solution for " + problemArray[problem].problemInstanceName);
                System.out.println("***************************************************************************\n");

                problemArray[problem].printInfo();
                problemArray[problem].generateItemList();

                ArrayList<Integer> itemList = problemArray[problem].itemList;
                problemArray[problem].sortItemListDescending();
                System.out.println("Descending Order Item List: " + itemList);

                int numberOfItems = itemList.toArray().length;

                //get copy of descending order list
                ArrayList<Integer> descendingItems = new ArrayList<>(numberOfItems);
                for (Integer element : itemList) {
                    descendingItems.add(element);
                }

                System.out.println("Copy Descending Order Item List: " + descendingItems + "\n");

                // create instance of solution and call pack(descendingItems)
                SolutionMBS sol = new SolutionMBS(numberOfItems, descendingItems);
    //            ArrayList<Bin> binList = new ArrayList<>();
                System.out.println("Copy Descending Order Item List 2: " + descendingItems + "\n");
    //            long startTime = System.currentTimeMillis();
                long startTime = System.nanoTime();
                ArrayList<Bin> binList = sol.performBinPacking();
    //            long endTime = System.currentTimeMillis();
                long endTime = System.nanoTime();
                long executionTime = endTime - startTime;

                System.out.println("\n***************************************************************************");

                System.out.println("Problem Instance " + problemArray[problem].problemInstanceName + " Bin Packing Solution:");
                System.out.println("Number of bins: " + binList.size());
                double sum = 0.0;
                for (Bin bin : binList) {
                    int occupiedCapacity = bin.printBinContents();
                    sum += Math.pow(((double) occupiedCapacity / 10000), 2);
                }
                System.out.println("Cost function: " + (sum / binList.size()));
                System.out.println("Time taken for packing: " + executionTime + " nanoseconds.");
//                sumTime+=executionTime;
                System.out.println("***************************************************************************\n");
//            }
//            System.out.println("Average Time: "+((double)sumTime/30));
//            avgTime.add(((double)sumTime/30));
        }
//        System.out.println("avgtime: "+avgTime);
    }
}