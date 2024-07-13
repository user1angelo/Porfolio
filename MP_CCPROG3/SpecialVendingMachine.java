

import java.util.ArrayList;
import java.util.Scanner;

public class SpecialVendingMachine extends VendingMachine {
    public ArrayList<Item> displayItems;
    private ArrayList<Smoothie> customizableProducts;
    private ArrayList<Fruit> customizableIngredients;
    private Scanner scanner = new Scanner(System.in);
    private VendingMachine vendingMachine;


    public SpecialVendingMachine() {
        super();

        addItem("Apple", 95, 1.0);
        addItem("Orange", 45, 1.0);
        addItem("Strawberry", 60, 1.0);
        // Initialize the customizable ingredients
        customizableIngredients = new ArrayList<>();
        customizableIngredients.add(new Fruit("Apple", 95, 1.0));
        customizableIngredients.add(new Fruit("Orange", 45, 1.0));
        customizableIngredients.add(new Fruit("Strawberry", 60, 1.0));

        // Initialize the customizable products
        customizableProducts = new ArrayList<>();
        customizableProducts.add(new Smoothie("Apple Smoothie", 50, 20));
        customizableProducts.add(new Smoothie("Orange Smoothie", 60, 35));
        customizableProducts.add(new Smoothie("Strawberry Smoothie", 80, 50));
    }

    // Function to add a customizable product to the vending machine
    
    public void addCustomizableProduct(Smoothie product) {
        customizableProducts.add(product);
    }

    // Function to remove a customizable product from the vending machine
    public void removeCustomizableProduct(Smoothie product) {
        customizableProducts.remove(product);
    }

    // Function to add a customizable ingredient to the vending machine
    public void addCustomizableIngredient(Fruit ingredient) {
        customizableIngredients.add(ingredient);
    }

    // Function to remove a customizable ingredient from the vending machine
    public void removeCustomizableIngredient(Fruit ingredient) {
        customizableIngredients.remove(ingredient);
    }

    // Function to display all available customizable products
    public void displayCustomizableProducts() {
        System.out.println("Customizable Products:");
        for (int i = 0; i < customizableProducts.size(); i++) {
            Smoothie product = customizableProducts.get(i);
            System.out.println((i + 1) + ". " + product.getName() + " (" + product.getCalories() + " Calories, Price: $" + product.getPrice() + ")");
        }
    }

    // Function to display all available customizable ingredients
    public void displayCustomizableIngredients() {
        System.out.println("Customizable Ingredients:");
        for (int i = 0; i < customizableIngredients.size(); i++) {
            Fruit ingredient = customizableIngredients.get(i);
            System.out.println((i + 1) + ". " + ingredient.getName() + " (Calories: " + ingredient.getCalories() + ", Price: $" + ingredient.getPrice() + ")");
        }
    }

    public void delay(int seconds) {
        try {
            Thread.sleep(seconds * 1000);
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // Function to prepare a customizable product
    public boolean prepareCustomizableProduct(int productIndex, ArrayList<Integer> chosenFruits, int payment) {
        Smoothie product = this.customizableProducts.get(productIndex - 1);

        System.out.println("Preparing " + product.getName() + "...");
        delay(1);
        System.out.println("adding ice to blender...");
        delay(1);
        System.out.println("adding milk to blender...");
        delay(1);

        Smoothie smoothie = new Smoothie(product.getName(), product.getCalories(), product.getPrice());
        for (Integer fruitIndex : chosenFruits) {
            Fruit fruit = this.customizableIngredients.get(fruitIndex - 1);
            System.out.println("adding " + fruit.getName() + " to blender...");
            delay(1);
            smoothie.addFruit(fruit);
        }

        // Calculate the total calories and price of the smoothie
        int totalCalories = smoothie.getTotalCalories();
        double totalPrice = smoothie.getTotalPrice();

        // Rest of the code remains the same
        System.out.println("Blending...");
        delay(1);
        System.out.println(smoothie.getName() + " Smoothie Done!");
        System.out.print("Add-ons: ");
        for (int i = 0; i < chosenFruits.size(); i++) {
            if (i > 0) {
                System.out.print(", ");
            }
            System.out.print(this.customizableIngredients.get(chosenFruits.get(i) - 1).getName());
        }
        System.out.println();

        System.out.println("Total Price: " + totalPrice);
        if (payment < totalPrice) {
            System.out.println("Insufficient payment, please enter a higher amount.");
        } else {
            System.out.println("Smoothie prepared successfully!");

            // Calculate change
            double change = payment - totalPrice;
            ArrayList<Integer> changeList = this.getChange((int) change);
            ArrayList<Integer> denominations = this.getMoneyBox().getAvailableDenominations();
            int totalChange = 0;

            System.out.println("Your change is: ");
            for (int i = 0; i < changeList.size(); i++) {
                if (changeList.get(i) != 0) {
                    System.out.println(changeList.get(i) + " " + denominations.get(i) + " peso bills");
                    totalChange += changeList.get(i) * denominations.get(i);
                }
            }

            if (totalChange < change) {
                System.out.println((change - totalChange) + " peso in coins");
            }
        }
        return false;
    }


    // Overriding purchaseItem function to handle customizable products
    @Override
    public boolean purchaseItem(String name, int payment) {
        if (name.equals("1")) {
            System.out.println("Goodbye!");
            return true;
        } else if (name.matches("\\d+")) {
            int productIndex = Integer.parseInt(name);
            if (productIndex >= 1 && productIndex <= customizableProducts.size()) {
                displayCustomizableIngredients();
                System.out.println("Please enter the indices of the ingredients you want to include (separated by commas):");
                String input = scanner.nextLine();
                String[] chosenIndices = input.split(",");
                ArrayList<Integer> chosenIngredients = new ArrayList<>();
                for (String index : chosenIndices) {
                    int ingredientIndex = Integer.parseInt(index.trim());
                    if (ingredientIndex >= 1 && ingredientIndex <= customizableIngredients.size()) {
                        chosenIngredients.add(ingredientIndex);
                    } else {
                        System.out.println("Invalid ingredient index: " + index);
                    }
                }
                if (chosenIngredients.size() > 0) {
                    prepareCustomizableProduct(productIndex, chosenIngredients, payment);
                    return true;
                } else {
                    System.out.println("No valid ingredients chosen.");
                    return false;
                }
            } else {
                System.out.println("Invalid product index.");
                return false;
            }
        } else {
            return super.purchaseItem(name, payment);
        }
    }

    public ArrayList<Fruit> getCustomizableIngredients() {
        return customizableIngredients;
    }

    public ArrayList<Smoothie> getCustomizableProducts() {
        return customizableProducts;
    }
}