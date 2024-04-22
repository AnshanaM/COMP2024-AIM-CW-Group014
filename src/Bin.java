import java.util.ArrayList;
import java.util.Collections;

public class Bin {
    public static int BIN_FULL_CAPACITY = 60;
    public int remainingCapacity;
    public ArrayList<Integer> itemsInBin = new ArrayList<>();

    public Bin(){
        this.remainingCapacity = BIN_FULL_CAPACITY;
    }

    public void addToBin(int itemWeight){
        this.itemsInBin.add(itemWeight);
        this.remainingCapacity -= itemWeight;
        System.out.println("remaining capacity: "+this.remainingCapacity);
    }

    public int getRemainingCapacity(){
        return remainingCapacity;
    }

    public void printBinContents(){
        System.out.println(itemsInBin);
    }

    public void removeAllItem(int item) {
        itemsInBin.removeAll(Collections.singleton(item));
    }

    public void emptyBin() {
        itemsInBin.clear();
    }

    public int size() {
        return itemsInBin.size();
    }

    public int getItem(int index) {
        return itemsInBin.get(index);
    }
}