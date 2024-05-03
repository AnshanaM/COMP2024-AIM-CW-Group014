package GGACGT;
import java.util.ArrayList;

public class nextFitHeuristic {
/**
 * When processing next item
 * check if it fits in the same bin as the last item.
 * Use a new bin only if it does not.
 * */
    public static ArrayList<Bin> NF(ArrayList<Integer> randomPermutationitemList) {
        ArrayList<Bin> binCollection = new ArrayList<>();
        boolean binInputStatus = false;
        Bin currentBin = new Bin();
        for (Integer item : randomPermutationitemList) {
            binInputStatus = currentBin.addToBin(item);
            if (!binInputStatus) {
                binCollection.add(currentBin); // close bin and add to collection
                currentBin = new Bin(); // open a new bin
                currentBin.addToBin(item); // add the item which couldn't fit to this new bin
            }
        }
        binCollection.add(currentBin);
        return binCollection;
    }
}

