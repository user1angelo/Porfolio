/**
 * The Slot class represents a slot in a vending machine that holds an item and its quantity.
 */
public class Slot {
    private Item item;
    private int quantity;

    /**
     * Constructs a Slot object with the specified item, quantity, and other details.
     * @param name     the name of the item
     * @param calories the calories of the item
     * @param price    the price of the item
     * @param quantity the initial quantity of the item in the slot
     */
    public Slot(String name, int calories, double price, int quantity) {
        this.item = new Item(name, calories, price);
        this.quantity = quantity;
    }

    /**
     * Retrieves the item in the slot.
     * @return the item in the slot
     */
    public Item getItem() {
        return item;
    }

    /**
     * Retrieves the quantity of the item in the slot.
     * @return the quantity of the item
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item in the slot.
     * @param quantity the new quantity of the item
     */
    public void setQuantity(int quantity) {
        if (quantity >= 0) {
            this.quantity = quantity;
        }
    }

    public boolean isEmpty() {
        if (item == null) {
            return true;
        } else {
            return false;
        }
    }
}
