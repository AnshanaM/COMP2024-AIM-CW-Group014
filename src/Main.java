import java.util.ArrayList;

public class Main {
    // Notation
    // | Code             | Notation     | Description
    // | noOfItems        | n            | Number of items
    // | binCapacity      | c            | Bin capacity
    // | optimalNoOfBins  | opt          | Number of bins in the optimal solution
    // | itemWeight       | wi           | Weight of the item i (i=1,...,n)
    // | binWeight        | l(j)         | Accumulated weight of bin j
    // | descendingItems  | Z            | List of items sorted in decreasing order according to itemWeight
    // | solution         | Sol          | Solution i.e.,complete assignment of items to bins
    // | numberOfBins     | m            | Number of bins in the solution
    // | slack            | s(j)         | Free space/slack in the bin i.e., c - l(j)
    // | setOfItemsN      | N            | Set of n items
    // | setOfWeightsN    | W            | set of n weights

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

    static final int TOTAL_PROBLEM_INSTANCES = 1;

    public static void main(String[] args) {
        System.out.println("\n***************************************************************************");
        System.out.println("One-Dimensional Bin Packing Problem");
        System.out.println("Minimum Bin Slack Algorithm");

        System.out.println("***************************************************************************");
        System.out.println("Getting problem instances");
        System.out.println("***************************************************************************\n");

        //problemArray stores all 5 problem instances
        ProblemInstance[] problemArray = ReadFile.readFile();
        //iterate through problem array and display the problem instances
        for (int problem = 0 ; problem < TOTAL_PROBLEM_INSTANCES; problem++){

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



            System.out.println("Copy Descending Order Item List: " + descendingItems+ "\n");

            System.out.println("***************************************************************************");
            System.out.println("Generating Solution for "+problemArray[problem].problemInstanceName);
            System.out.println("***************************************************************************\n");

            // create instance of solution and call pack(descendingItems)
            SolutionMBS sol = new SolutionMBS(numberOfItems,descendingItems);
//            ArrayList<Bin> binList = new ArrayList<>();
            System.out.println("Copy Descending Order Item List 2: " + descendingItems+ "\n");
            ArrayList<Bin> binList = sol.performBinPacking();
            System.out.println("Problem Instance "+(problem+1)+" Bin Packing Solution:");
            for (Bin bin : binList){
                bin.printBinContents();
            }
        }

    }
}