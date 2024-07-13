import java.util.*;

/**
 * The MoneyBox class represents a money box in a vending machine that holds different denominations of currency.
 * It provides methods to add money, get the available denominations, and calculate optimal change for a given amount.
 */
public class MoneyBox {
    private ArrayList<ArrayList<Integer>> denominationBalance;
    private ArrayList<Integer> availableDenominations = new ArrayList<Integer>();

    /**
     * Constructs a MoneyBox object with initial denomination balances and available denominations.
     */
    public MoneyBox(){
        this.denominationBalance = new ArrayList<ArrayList<Integer>>();
        int denoms[] = {1, 5, 10, 20, 50, 100, 500, 1000};
        for (int i = 0; i < denoms.length; i++){
            ArrayList<Integer> denomination = new ArrayList<Integer>();
            denomination.add(denoms[i]);
            denomination.add(0);
            this.denominationBalance.add(denomination);
            this.availableDenominations.add(denoms[i]);
        }
    }

    /**
     * Retrieves the balance of denominations in the money box.
     * @return the balance of denominations
     */
    public ArrayList<ArrayList<Integer>> getDenominationArray(){
        return this.denominationBalance;
    }

    /**
     * Retrieves the list of available denominations.
     * @return the list of available denominations
     */
    public ArrayList<Integer> getAvailableDenominations(){
        return this.availableDenominations;
    }

    /**
     * Adds money to the money box with the specified denomination and amount.
     * @param denomination the denomination of the money to add
     * @param amount the amount of money to add
     */
    public void addMoney(int denomination, int amount){
        if (this.availableDenominations.contains(denomination)){
            for (int i = 0; i < this.denominationBalance.size(); i++){
                if (this.denominationBalance.get(i).get(0) == denomination){
                    this.denominationBalance.get(i).set(1, this.denominationBalance.get(i).get(1) + amount);
                }
            }
        }
    }

    /**
     * Calculates the optimal change for a given amount from the money box.
     * @param amount the amount for which to calculate the change
     * @return the list of denominations representing the optimal change, or null if change cannot be made
     */
    public ArrayList<Integer> getOptimalChange(int amount){
        ArrayList<Integer> optimalChange = new ArrayList<Integer>();
        int remainingAmount = amount;
        for (int i = this.denominationBalance.size() - 1; i >= 0; i--){
            int denomination = this.denominationBalance.get(i).get(0);
            int denominationCount = this.denominationBalance.get(i).get(1);
            if (remainingAmount >= denomination){
                int denominationAmount = remainingAmount / denomination;
                if (denominationAmount > denominationCount){
                    denominationAmount = denominationCount;
                }
                remainingAmount -= denominationAmount * denomination;
                optimalChange.add(denominationAmount);
            }
            else{
                optimalChange.add(0);
            }
        }
        Collections.reverse(optimalChange);
        if (remainingAmount == 0){
            return optimalChange;
        }
        else{
            return null;
        }
    }

    /**
     * Retrieves the quantity of the specified denomination in the money box.
     * @param denomination the denomination of the money
     * @return the quantity of the specified denomination
     */
    public int getQuantity(int denomination) {
        for (int i = 0; i < this.denominationBalance.size(); i++) {
            if (this.denominationBalance.get(i).get(0) == denomination) {
                return this.denominationBalance.get(i).get(1);
            }
        }
        return 0;
    }

    /**
     * Collects money from the money box.
     * @param amount the amount to collect
     */
    public void collectMoney(int amount) {
        ArrayList<Integer> denominations = this.getAvailableDenominations();
        ArrayList<Integer> change = this.getOptimalChange(amount);

        if (change != null) {
            for (int i = 0; i < denominations.size(); i++) {
                int denomination = denominations.get(i);
                int changeQuantity = change.get(i);
                int currentQuantity = this.getQuantity(denomination);
                if (changeQuantity > 0) {
                    this.setQuantity(denomination, currentQuantity - changeQuantity);
                }
            }
        } else {
            System.out.println("Unable to collect money. Not enough change in the money box.");
        }
    }

    void setQuantity(int denomination, int i) {
        for (int j = 0; j < this.denominationBalance.size(); j++) {
            if (this.denominationBalance.get(j).get(0) == denomination) {
                this.denominationBalance.get(j).set(1, i);
            }
        }
    }

    /**
     * Replenishes the money box with the specified denomination and quantity.
     * @param denomination the denomination of the money
     * @param quantity the quantity of the money
     */
    public void replenishDenomination(int denomination, int quantity) {
        for (int i = 0; i < this.denominationBalance.size(); i++) {
            if (this.denominationBalance.get(i).get(0) == denomination) {
                this.denominationBalance.get(i).set(1, quantity);
            }
        }
    }

    /**
     * Adds money to the money box.
     * @param price the price of the item
     */
    public void addMoney(int price) {
        ArrayList<Integer> denominations = this.getAvailableDenominations();
        ArrayList<Integer> change = this.getOptimalChange(price);

        if (change != null) {
            for (int i = 0; i < denominations.size(); i++) {
                int denomination = denominations.get(i);
                int changeQuantity = change.get(i);
                int currentQuantity = this.getQuantity(denomination);
                if (changeQuantity > 0) {
                    this.setQuantity(denomination, currentQuantity + changeQuantity);
                }
            }
        } else {
            System.out.println("Unable to add money. Not enough change in the money box.");
        }
    }

    public void subtractChange(ArrayList<Integer> changeList) {
        ArrayList<Integer> denominations = this.getAvailableDenominations();
        for (int i = 0; i < denominations.size(); i++) {
            int denomination = denominations.get(i);
            int changeQuantity = changeList.get(i);
            int currentQuantity = this.getQuantity(denomination);
            if (changeQuantity > 0) {
                this.setQuantity(denomination, currentQuantity - changeQuantity);
            }
        }
    }
}