package SA;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

class Bin {
    private static int nextId = 1; // Static counter to assign unique IDs
    private int id;
    private int capacity;
    private List<Item> items;
    private Set<Integer> setIndices;

    public Bin(int capacity) {
        if (capacity <= 0) {
            throw new IllegalArgumentException("Capacity must be greater than 0.");
        }
        this.id = nextId++;
        this.capacity = capacity;
        this.items = new ArrayList<>();
        this.setIndices = new HashSet<>();
    }
    public int getCapacity() {
        return capacity;
    }
    public boolean canAddItem(int itemWeight) {
        return currentLoad() + itemWeight <= capacity;
    }
    public int getId() {
        return id;
    }


    // Prints items in a bin
    public void printItems() {
        items.forEach(item -> System.out.print(item.getWeight() + " "));
        System.out.println();
    }

    // Gets all items in the bin
    public List<Item> getItems() {
        return items;
    }

    public boolean containsItem(Item item) {
        return items.contains(item);
    }

    public int currentLoad() {
        return items.stream().mapToInt(Item::getWeight).sum();
    }

    public boolean addItem(Item item) {
        if (canAddItem(item.getWeight()) && !setIndices.contains(item.getSetIndex())) {
            items.add(item);
            setIndices.add(item.getSetIndex()); // Track the set index
            return true;
        } else {
            return false; // Item cannot be added
        }
    }

    public boolean removeItem(Item item) {
        boolean removed = items.remove(item);
        if (removed) {
            setIndices.remove(item.getSetIndex()); // Remove the set index
        }
        return removed;
    }

    public boolean canAcceptItemFromSet(int setIndex) {
        return !setIndices.contains(setIndex);
    }
}