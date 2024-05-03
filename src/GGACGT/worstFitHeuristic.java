package GGACGT;
import java.util.ArrayList;
import java.util.Collections;

/**
 * for every item:
 *  sort bin collection in descending order of leftover capacity
 *  check if item fits
 *  if fits:
 *      continue
 *  else:
 *      open a new bin
 * */
public class worstFitHeuristic {
    public static ArrayList<Bin> WFn(ArrayList<Integer> itemsAbove50, ArrayList<Integer> permutationRemainingItems) {
        ArrayList<Bin> binCollection = new ArrayList<>();
        // if we have items above 50% capacity, give them separate bins and add to a collection of bins.
        // this will be the base for our solution.
        if (!itemsAbove50.isEmpty()){
            for (Integer item : itemsAbove50) {
                Bin bin = new Bin();
                bin.addToBin(item);
                binCollection.add(bin);
            }
        }
        System.out.println("Pre-set bins:");
        for (Bin bin : binCollection) {
            bin.printBinContents();
        }
        // perform WF heuristic on remaining items
        ArrayList<Bin> updatedBinCollection = WF(binCollection, permutationRemainingItems);
        System.out.println("\nRaw Solution (without encoding):");
        for (Bin bin : updatedBinCollection) {
            bin.printBinContents();
        }
        return updatedBinCollection;
    }
    private static ArrayList<Bin> WF(ArrayList<Bin> existingBinCollection, ArrayList<Integer> permutationRemainingItems){
        int start = 0;
        if (existingBinCollection.size()==0){
            Bin newBin = new Bin();
            newBin.addToBin(permutationRemainingItems.get(0)); // assume that at least one item below 50% capacity
            existingBinCollection.add(newBin);
            start = 1;
        }
        for (int i = start; i < permutationRemainingItems.size();i++){
            if (existingBinCollection.size()>1) {
                Collections.sort(existingBinCollection, Bin.remainingCapacityComparator);
            }
            boolean binInputStatus = existingBinCollection.get(0).addToBin(permutationRemainingItems.get(i));
            if (!binInputStatus){ // if item too big to fit into the largest spaced bin, we need a new bin
                Bin newBin = new Bin();
                newBin.addToBin(permutationRemainingItems.get(i));
                existingBinCollection.add(newBin);
            }
        }
        return existingBinCollection;
    }
}
