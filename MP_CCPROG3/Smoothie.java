import java.util.ArrayList;

public class Smoothie {
    private double price;
    private int calories;
    private String name;
    private ArrayList<Fruit> fruits;

    public Smoothie(String name, int calories, double price) {
        this.name = name;
        this.fruits = new ArrayList<>();
        this.calories = calories;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Fruit> getFruits() {
        return fruits;
    }

    // Method to add a fruit to the smoothie
    public void addFruit(Fruit fruit) {
        fruits.add(fruit);
    }

    // Method to calculate the total calories of the smoothie
    public int getTotalCalories() {
        int totalCalories = 0;
        for (Fruit fruit : fruits) {
            totalCalories += fruit.getCalories();
        }
        return totalCalories;
    }

    // Method to calculate the total price of the smoothie
    public double getTotalPrice() {
        double totalPrice = 0;
        for (Fruit fruit : fruits) {
            totalPrice += fruit.getPrice();
        }
        return totalPrice;
    }

    public int getCalories() {
        return calories;
    }

    public double getPrice() {
        return price;
    }
}