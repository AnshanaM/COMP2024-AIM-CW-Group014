package GGACGT;
import java.util.ArrayList;
import java.util.Collections;

/**
 * for each item:
 *  sort the existing bin collection
 *  if doesn't fit:
 *      create new bin
 *      add item to bin
 *      add bin to collection
 *  else:
 *      add item to current bin
 *  */
public class WF {
    public static ArrayList<Bin> worstFit(ArrayList<Integer> randomPermutationitemList) {
        ArrayList<Bin> binCollection = new ArrayList<>();
        Bin firstBin = new Bin();
        firstBin.addToBin(randomPermutationitemList.get(0));
        binCollection.add(firstBin);
        for (int i = 1; i < randomPermutationitemList.size();i++){
            if (binCollection.size()>1) { // if more than 1 bin, sort
                Collections.sort(binCollection, Bin.remainingCapacityComparator);
            }
            boolean binInputStatus = binCollection.get(0).addToBin(randomPermutationitemList.get(i));
            if (!binInputStatus){ // if item too big to fit into the largest spaced bin, we need a new bin
                Bin newBin = new Bin();
                newBin.addToBin(randomPermutationitemList.get(i));
                binCollection.add(newBin);
            }
        }
        return binCollection;
    }
}
