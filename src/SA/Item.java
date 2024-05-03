package SA;
class Item {
    private int weight;
    private Bin bin;
    private int setIndex; // To identify which set this item belongs to

    public Item(int weight, int setIndex) {
        this.weight = weight;
        this.setIndex = setIndex;
    }

    public int getWeight() {
        return weight;
    }
    public Item(Item otherItem) {
        this.weight = otherItem.weight;
        this.setIndex = otherItem.setIndex;
        // The bin reference is intentionally not copied as it will be set when the item is added to a bin
    }

    // Method to update the set index of an item
    public void setSetIndex(int setIndex) {
        this.setIndex = setIndex;
    }

    public int getSetIndex() {
        return setIndex;
    }

}
