/**
 * The Item class represents an item in a vending machine.
 */
public class Item {

    private String name;
    protected int calories;
    private double price;

    /**
     * Constructs an Item object with the specified name, calories, and price.
     * @param name     the name of the item
     * @param calories the calories of the item
     * @param price    the price of the item
     */
    public Item(String name, int calories, double price) {
        this.name = name;
        this.calories = calories;
        this.price = price;
    }

    /**
     * Retrieves the name of the item.
     * @return the name of the item
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the calories of the item.
     * @return the calories of the item
     */
    public int getCalories() {
        return calories;
    }

    /**
     * Retrieves the price of the item.
     * @return the price of the item
     */
    public double getPrice() {
        return price;
    }

    /**
     * Sets the price of the item.
     * @param price the new price of the item
     */
    public void setPrice(double price) {
        this.price = price;
    }
}