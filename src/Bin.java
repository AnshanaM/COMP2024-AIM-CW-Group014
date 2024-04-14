import java.util.ArrayList;

public class Bin {
    public static int BIN_FULL_CAPACITY = 10000;
    public int currentCapacity;
    public ArrayList<Integer> itemsInBin = new ArrayList<>();

    public Bin(){
        this.currentCapacity = 0;
    }

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



//    public boolean addToBin(int item){
//        int futureCapacity = this.currentCapacity + item;
//        if (futureCapacity < BIN_FULL_CAPACITY) {
//            this.itemsInBin.add(item);
//            this.currentCapacity = futureCapacity;
//            return true;
//        }else{
//            return false;
//        }
//    }





    public void printBinContents(){
        System.out.print(itemsInBin);
    }
}