package MBS;

import java.util.*;

public class ProblemInstance {
    public String problemInstanceName; // 'TEST0049'
    public int numberOfItemWeights; // number m of different item weights
    public int binCapacity; // capacity C of bins
    public int[] itemWeight; // array of all the item weights
    public int[] noOfItems; // number of items with that item weight
    public ArrayList<Integer> itemList = new ArrayList<>();


    public ProblemInstance(String pi, int niw, int bc){
        problemInstanceName = pi;
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

    // generate a list of items based on item weights and their counts
    public void generateItemList() {
        for (int i = 0; i < itemWeight.length; i++) {
            for (int j = 0; j < noOfItems[i]; j++) {
                itemList.add(itemWeight[i]);
            }
        }
        System.out.println("Item List: "+ itemList);
    }

    // sorting the list of items in descending order
    public void sortItemListDescending() {
        itemList.sort(new Comparator<Integer>() {
            @Override
            public int compare(Integer o1, Integer o2) {
                return o2.compareTo(o1);
            }
        });
    }

    public void printInfo() {
        System.out.println("Problem Instance Name: " + problemInstanceName);
        System.out.println("Number of Item Weights: " + numberOfItemWeights);
        System.out.println("Bin Capacity: " + binCapacity+"\n");
        System.out.println("Item Weights and Number of Items:");
        for (int i = 0; i < numberOfItemWeights; i++) {
            System.out.println("Item Weight: " + itemWeight[i] + ", Number of Items: " + noOfItems[i]);
        }
        System.out.println("\n");
    }
}