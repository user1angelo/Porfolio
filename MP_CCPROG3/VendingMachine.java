import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * The VendingMachine class represents a vending machine that contains slots to hold items,
 * a money box to handle transactions, and various methods to manage and interact with the machine.
 */
public class VendingMachine {
    private ArrayList<Slot> slots;
    private int slotCount;
    private MoneyBox moneyBox;
    private TransactionManager manager;

    /**
     * Constructs a VendingMachine object with an initial slot count of 0 and an empty list of slots.
     */
    public VendingMachine() {
        this.moneyBox = new MoneyBox();
        moneyBox.addMoney(1, 10);
        moneyBox.addMoney(5, 10);
        moneyBox.addMoney(10, 10);
        moneyBox.addMoney(20, 10);
        moneyBox.addMoney(50, 10);
        moneyBox.addMoney(100, 10);
        moneyBox.addMoney(500, 10);
        moneyBox.addMoney(1000, 10);
        this.slotCount = 0;
        this.slots = new ArrayList<Slot>();
        this.manager = new TransactionManager();
    }

    /**
     * Sets the number of slots in the vending machine.
     *
     * @param slotCount the desired slot count
     * @return true if the slot count is valid (at least 8) and successfully set, false otherwise
     */
    public boolean setSlots(int slotCount) {
        if (slotCount >= 8) {
            this.slotCount = slotCount;

            return true;
        } else {
            return false;
        }
    }

    /**
     * Retrieves the list of slots in the vending machine.
     *
     * @return the list of slots
     */
    public ArrayList<Slot> getSlots() {
        return this.slots;
    }

    /**
     * Adds an item to the vending machine.
     *
     * @param name     the name of the item
     * @param calories the calories of the item
     * @param price    the price of the item
     * @return true if the item is added successfully, false if the item already exists or the vending machine is full
     */
    public boolean addItem(String name, int calories, double price) {
        for (int i = 0; i < this.slots.size(); i++) {
            Item item = this.slots.get(i).getItem();
            if (item != null && item.getName().equals(name)) {
                return false; // Item already exists in the vending machine.
            }
        }
        if (this.slots.size() < this.slotCount) {
            Slot newSlot = new Slot(name, calories, price, 0);
            this.slots.add(newSlot);
            return true; // Added successfully.
        } else {
            return false; // Vending machine is full.
        }
    }

    /**
     * Sets the quantity of an item in the vending machine.
     *
     * @param name     the name of the item
     * @param quantity the new quantity of the item
     * @return true if the quantity is set successfully, false if the item does not exist
     */
    public boolean setQuantity(String name, int quantity) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItem().getName().equals(name)) {
                this.slots.get(i).setQuantity(quantity);
                return true;
            }
        }
        return false;
    }

    /**
     * Purchases an item from the vending machine.
     *
     * @param name the name of the item to purchase
     * @return
     */
    public boolean purchaseItem(String name, int payment) {
        double price = this.getItemPrice(name);
        if (price > 0 && payment >= price) {
            int change = payment - (int) price;

            if (change >= 0) {
                ArrayList<Integer> changeList = this.getChange(change);
                ArrayList<Integer> denominations = this.moneyBox.getAvailableDenominations();

                // Check if changeList mapped to denominations is equal to change
                int total = 0;
                for (int i = 0; i < changeList.size(); i++) {
                    total += changeList.get(i) * denominations.get(i);
                }

                if (total == change) {
                    // Update the money box by adding the payment amount
                    this.moneyBox.addMoney(payment);
                    for (int i = 0; i < this.slots.size(); i++) {
                        if (this.slots.get(i).getItem().getName().equals(name)) {
                            if (this.slots.get(i).getQuantity() > 0) {
                                this.slots.get(i).setQuantity(this.slots.get(i).getQuantity() - 1);
                                LocalDateTime purchaseTime = LocalDateTime.now();
                                manager.addTransaction(name, price, purchaseTime);
                                // Update the money box by collecting the change amount
                                this.moneyBox.subtractChange(changeList);
                                return true;
                            } else {
                                System.out.println("Item is out of stock.");
                            }
                        }
                    }
                }
            } else {
                System.out.println("Insufficient payment, please enter a higher amount.");
            }
        } else {
            System.out.println("Invalid payment amount. Please enter an amount greater than or equal to the item price.");
        }
        return false;
    }

    /**
     * Checks if an item exists in the vending machine.
     *
     * @param itemName the name of the item to check
     * @return true if the item exists, false otherwise
     */
    public boolean doesItemExist(String itemName) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItem().getName().equals(itemName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Displays the items in the vending machine with their details.
     *
     * @return
     */
    public ArrayList<Item> displayItems() {
        for (int i = 0; i < this.slots.size(); i++) {
            System.out.println("Slot " + (i + 1) + ": " + this.slots.get(i).getItem().getName() + " Calories: " + this.slots.get(i).getItem().getCalories() + " Price: " + this.slots.get(i).getItem().getPrice() + " Quantity: " + this.slots.get(i).getQuantity());
        }
        return null;
    }

    /**
     * Retrieves the price of an item in the vending machine.
     *
     * @param itemName the name of the item
     * @return the price of the item, or -1 if the item does not exist
     */
    public double getItemPrice(String itemName) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItem().getName().equals(itemName)) {
                return this.slots.get(i).getItem().getPrice();
            }
        }
        return -1;
    }

    /**
     * Retrieves the optimal change for a given amount from the money box.
     *
     * @param amount the amount for which to get the change
     * @return the list of denominations representing the change
     */
    public ArrayList<Integer> getChange(int amount) {
        ArrayList<Integer> changeList = this.moneyBox.getOptimalChange(amount);
        return (changeList != null) ? changeList : new ArrayList<>();
    }

    /**
     * Retrieves the money box of the vending machine.
     *
     * @return the money box
     */
    public MoneyBox getMoneyBox() {
        return this.moneyBox;
    }

    //Maintenance

    /**
     * Restocks an item in the vending machine.
     *
     * @param name     the name of the item
     * @param quantity the new quantity of the item
     */
    public void restockItem(String name, int quantity) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItem().getName().equals(name)) {
                this.slots.get(i).setQuantity(quantity);
            }
        }
    }

    /**
     * Sets the price of an item in the vending machine.
     *
     * @param name  the name of the item
     * @param price the new price of the item
     */
    public void setItemPrice(String name, double price) {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i).getItem().getName().equals(name)) {
                this.slots.get(i).getItem().setPrice(price);
            }
        }
    }

    /**
     * Displays the money box of the vending machine.
     */
    public void displayMoneyBox() {
        System.out.println("Money Box Balance:");
        ArrayList<Integer> denominations = this.moneyBox.getAvailableDenominations();
        for (int i = 0; i < denominations.size(); i++) {
            int denomination = denominations.get(i);
            int quantity = this.moneyBox.getQuantity(denomination);
            System.out.println(denomination + " peso bills: " + quantity);
        }
    }

    /**
     * Collects money from the vending machine.
     *
     * @param amount the amount to collect
     */
    public void collectMoney(int amount) {
        this.moneyBox.collectMoney(amount);
    }

    /**
     * Replenishes the money box of the vending machine.
     *
     * @param denomination the denomination to replenish
     * @param quantity     the quantity to replenish
     */
    public void replenishDenomination(int denomination, int quantity) {
        this.moneyBox.replenishDenomination(denomination, quantity);
    }

    /**
     * Displays the transaction summary of the vending machine.
     */
    public String displayTransactionSummary() {
        return manager.displayTransactionSummary();
    }

    public TransactionManager getTransactionManager() {
        return this.manager;
    }
}