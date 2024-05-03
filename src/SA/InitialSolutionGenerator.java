package SA;
import java.util.List;

public class InitialSolutionGenerator {
    private List<Bin> bins;
    private List<List<Item>> sets;
    private problemInstance problem;

    public InitialSolutionGenerator(List<Bin> bins, List<List<Item>> sets, problemInstance problem) {
        this.bins = bins;
        this.sets = sets;
        this.problem = problem;
    }

    public void generate() {
        bins.clear(); // Clear existing bins

        int[] nextItemIndexPerSet = new int[sets.size()];
        boolean itemsRemaining = true;
        boolean allItemsPlaced = true;

        while (itemsRemaining) {
            itemsRemaining = false;

            for (int i = 0; i < sets.size(); i++) {
                if (nextItemIndexPerSet[i] < sets.get(i).size()) {
                    Item item = sets.get(i).get(nextItemIndexPerSet[i]);
                    boolean itemPlaced = false;

                    for (Bin bin : bins) {
                        if (bin.canAddItem(item.getWeight()) && bin.canAcceptItemFromSet(i)) {
                            if (bin.addItem(item)) {
                                nextItemIndexPerSet[i]++;
                                itemPlaced = true;
                                itemsRemaining = true;
                                break;
                            } else {
                                System.out.println("Unable to add item to bin. Bin is full.");
                                allItemsPlaced = false;
                            }
                        }
                    }

                    if (!itemPlaced) {
                        Bin newBin = new Bin(problem.binCapacity);
                        if (newBin.addItem(item)) {
                            bins.add(newBin);
                            nextItemIndexPerSet[i]++;
                            itemsRemaining = true;
                        } else {
                            System.out.println("Unexpected error: new bin is full upon creation.");
                            allItemsPlaced = false;
                        }
                    }
                }
            }
        }

        if (!allItemsPlaced) {
            System.out.println("Not all items could be placed into bins.");
        }

    }
}
