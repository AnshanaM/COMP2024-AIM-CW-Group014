package MBS;

import java.util.ArrayList;
import java.util.Collections;

public class SolutionMBS {
    public ArrayList<Integer> Z = new ArrayList<>();
    public int n;
    public int BIN_CAPACITY = 10000;
    public Bin packedBin;
    ArrayList<Bin> binList;
    Bin bin;

    public Bin optimallyPackedBin;
    int slack;

    //constructor
    public SolutionMBS(int noOfItems, ArrayList<Integer> descendingList){
        slack = BIN_CAPACITY;
        n = noOfItems;
        Z = descendingList;
        packedBin = new Bin();
        optimallyPackedBin = new Bin();
    }

    // returns bin slack/remaining capacity
    public int getSlack(Bin bin){
        slack = bin.getRemainingCapacity();
        return slack;
    }

    // pseudocode:
    // call MBS(1)
    // function MBS(q)
    //     for r = q to n'
    //         i = Z[r]
    //         if weighti <= s(A) then
    //             A = A U {i}
    //             Apply MBS(r+1)
    //             A = A \ {i}
    //             if s(A*) = 0 then
    //                 exit
    //     if s(A) <= s(A*) then
    //         A* = A

    private void MBS(int q){
        for (int r = q; r < n; r ++){
//            System.out.println("Iteration q: " + q + " number of items: "+ n);
            int item = Z.get(r);
//            System.out.println("item: " + item);
//            System.out.println("slack: " + packedBin.getRemainingCapacity());
//            System.out.println("slack: " + optimallyPackedBin.getRemainingCapacity());
            if (item <= getSlack(packedBin)){
                packedBin.addToBin(item);
//                System.out.println("packedBin: "+ packedBin);
                MBS(r+1);
//                System.out.println("begin recursive stack processing");
                packedBin.removeAllItem(item);
//                System.out.println("packedBin after removal: "+ packedBin);
                if (getSlack(optimallyPackedBin) == 0){
                    return;
                }
            }
        }
        if (getSlack(packedBin) < getSlack(optimallyPackedBin)){
//            System.out.println("packedBin slack better than optimal");
            //making deep copy of packed bin into optimally packed bin
//            optimallyPackedBin.emptyBin();
            for (int index=0; index < packedBin.size(); index++) {
                optimallyPackedBin.addToBin(packedBin.getItem(index));
            }
            packedBin.emptyBin();
        }
    }

    public ArrayList<Bin> performBinPacking(){
        binList = new ArrayList<>();
        // check if itemlist is not empty
        while (!Z.isEmpty()){
//            packedBin.remainingCapacity = BIN_CAPACITY;
//            optimallyPackedBin.remainingCapacity = BIN_CAPACITY;
            packedBin = new Bin();
            optimallyPackedBin = new Bin();
//            System.out.println("Z (descending items): "+Z);
            bin = new Bin();
            slack = bin.getRemainingCapacity();
            //call mbs
            MBS(0);
            //assign bin with optimally packed bin
            for (int index = 0; index < optimallyPackedBin.size(); index++){
                bin.addToBin(optimallyPackedBin.getItem(index));
            }
            //append bin to bin array
            binList.add(bin);
//            bin.printBinContents();
            for (Integer weight : bin.itemsInBin){
//                System.out.println("removing "+weight+" from Z");
                Z.remove(weight);
            }
            n = Z.size();
        }
        // exits loop when itemlist is empty meaning all bins are packed
        return binList;
    }
}
