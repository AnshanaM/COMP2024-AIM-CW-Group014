package SA;
public class problemInstance {
    public String problemInstanceName; // 'TEST0049'
    public int numberOfItemWeights; // number m of different item weights
    public int binCapacity; // capacity C of bins
    public int[] itemWeight; // array of all the item weights
    public int[] noOfItems; // number of items with that item weight

    public problemInstance(String pi, int niw, int bc){
        problemInstanceName = pi;//problem instance
        numberOfItemWeights = niw;
        binCapacity = bc;
        itemWeight = new int[niw];
        noOfItems = new int[niw];
    }
    public void addItemWeight(int weight, int index){
        itemWeight[index]=weight;
    }
    public void addNumItemWeight(int numberOfWeights, int index){
        noOfItems[index]=numberOfWeights;
    }
    public void printInfo() {
        System.out.println("Problem Instance Name: " + problemInstanceName);
        System.out.println("Number of Item Weights: " + numberOfItemWeights);
        System.out.println("Bin Capacity: " + binCapacity);
        System.out.println("Item Weights and Number of Items:");

        for (int i = 0; i < numberOfItemWeights; i++) {
            System.out.println("   Item Weight: " + itemWeight[i] + ", Number of Items: " + noOfItems[i]);
        }
    }





}
