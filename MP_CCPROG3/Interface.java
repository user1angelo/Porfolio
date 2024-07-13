import java.util.Scanner;
import java.util.*;

/**
 * The Interface class represents the user interface for interacting with the vending machine.
 */
public class Interface {
    private VendingMachine currentMachine;

    public Interface(VendingMachine vendingMachine) {
        this.currentMachine = vendingMachine;
    }

    /**
     * Creates a new vending machine by prompting the user for the number of slots and adding items to the slots.
     */
    public void createVendingMachine() {
        Scanner scanner = new Scanner(System.in);
        this.currentMachine = new VendingMachine();
        int slotCount = 0;
        while (true) {
            try {
                System.out.println("Please enter the number of slots in the vending machine: ");
                slotCount = scanner.nextInt();
                scanner.nextLine();
                if (this.currentMachine.setSlots(slotCount)) {
                    break;
                } else {
                    System.out.println("Invalid number of slots, please enter a number greater than or equal to 8.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine();
            }
        }


        System.out.println("Add item to the slots of the vending machine");
        while (true) {
            String name = null;
            while (true) {
                try {
                    System.out.print("Please enter the name of the item: ");
                    name = scanner.nextLine();
                    if (name.matches("^[a-zA-Z]+$")) {
                        break; // Valid name, exit the loop
                    } else {
                        throw new InputMismatchException();
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid name (letters only).");
                }
            }

            int calories = 0;
            while (true) {
                try {
                    System.out.println("Please enter the calories of the item:");
                    calories = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline character
                    break; // Exit the loop if a valid input is provided
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine(); // Consume the invalid input
                }
            }

            double price = 0;
            while (true) {
                try {
                    System.out.println("Please enter the price of the item:");
                    price = scanner.nextDouble();
                    scanner.nextLine(); // Consume the newline character
                    break; // Exit the loop if a valid input is provided
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Please enter a valid number.");
                    scanner.nextLine(); // Consume the invalid input
                }
            }

            if (!this.currentMachine.addItem(name, calories, price)) {
                System.out.println("Item could not be added because it is either a duplicate item or the vending machine is full, please try again.");
            } else {
                System.out.println("Item added successfully!");
            }
            System.out.println("Would you like to add another item? (y/n) ");
            String input = scanner.nextLine();
            if (input.equals("n")) {
                break;
            }
        }

        System.out.println("Please enter the quantity of each item in the vending machine:");
        for (int i = 0; i < this.currentMachine.getSlots().size(); i++) {
            String itemName = this.currentMachine.getSlots().get(i).getItem().getName();
            while (true) {
                System.out.println("Please enter the quantity of " + itemName + ":");
                try {
                    int quantity = Integer.parseInt(scanner.nextLine());
                    if (quantity >= 0) {
                        if (this.currentMachine.setQuantity(itemName, quantity)) {
                            break;
                        } else {
                            System.out.println("Invalid quantity, please try again.");
                        }
                    } else {
                        System.out.println("Invalid quantity, please enter a number greater than or equal to 0.");
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Please enter a valid integer.");
                }
            }
        }
    }

    /**
     * Tests the features of the vending machine by allowing the user to purchase items.
     */
    public void testFeatures() {
        Scanner scanner = new Scanner(System.in);
        String input;

        while (true) {
            System.out.println("Please select an option:");
            System.out.println("1. Purchase an item from the vending machine");
            System.out.println("2. Exit");
            input = scanner.nextLine();

            if (input.equals("1")) {
                this.currentMachine.displayItems();
                System.out.println("Please enter the name of the item you would like to purchase (Press 1 to exit):");
                String name = scanner.nextLine();

                while (!this.currentMachine.doesItemExist(name) && !name.equals("1")) {
                    System.out.println("Invalid item name, please try again:");
                    name = scanner.nextLine();
                }

                if (name.equals("1")) {
                    continue;
                }

                double price = this.currentMachine.getItemPrice(name);
                System.out.println("Cost of " + name + ": " + price);

                int payment;
                boolean paymentValid = false;

                while (!paymentValid) {
                    try {
                        System.out.println("Please enter your payment amount: ");
                        payment = scanner.nextInt();
                        scanner.nextLine();
                        if (payment < price) {
                            System.out.println("Insufficient funds, please enter a payment amount greater than or equal to " + price + ":");
                        } else {
                            paymentValid = true;
                            if (this.currentMachine.purchaseItem(name, payment)) {
                                System.out.println("Purchase successful! Enjoy your " + name + ".");
                                int change = payment - (int)this.currentMachine.getItemPrice(name);
                                ArrayList<Integer> changeList = this.currentMachine.getChange(change);
                                ArrayList<Integer> denominations = this.currentMachine.getMoneyBox().getAvailableDenominations();
                                //check if changeList mapped to denominations is equal to change
                                int total = 0;
                                for (int i = 0; i < changeList.size(); i++){
                                    total += changeList.get(i) * denominations.get(i);
                                }
                                if (total == change){
                                    System.out.println("Your change is: ");
                                    for (int i = 0; i < changeList.size(); i++){
                                        if(changeList.get(i) != 0){
                                            System.out.println(changeList.get(i) + " " + denominations.get(i) + " peso bills");
                                        }
                                    }
                                }
                            } else {
                                System.out.println("Insufficient funds. Please enter a payment amount greater than or equal to " + price + ":");
                                paymentValid = false;
                            }
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input. Please enter a valid payment amount.");
                        scanner.nextLine(); // Consume the invalid input to avoid infinite loop
                    }
                }
            } else if (input.equals("2")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }

    /**
     * Provides the maintenance options for the vending machine.
     */
    public void maintenance() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("Please select an option:");
            System.out.println("1. Restock items");
            System.out.println("2. Set price of item");
            System.out.println("3. Collecting payment / money");
            System.out.println("4. Replenish denominations");
            System.out.println("5. Print summary of transactions");
            System.out.println("6. Exit");
            String input = scanner.nextLine();
            if (input.equals("1")) {
                this.currentMachine.displayItems();
                System.out.println("Please enter the name of the item you would like to restock (Press 1 to exit):");
                String name = scanner.nextLine();
                while (!this.currentMachine.doesItemExist(name) && !name.equals("1")) {
                    System.out.println("Invalid item name, please try again:");
                    name = scanner.nextLine();
                }
                if (name.equals("1")) {
                    continue;
                } else {
                    System.out.println("Please enter the quantity of " + name + ":");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    while (quantity < 0) {
                        System.out.println("Invalid quantity, please enter a number greater than or equal to 0:");
                        quantity = scanner.nextInt();
                        scanner.nextLine();
                    }
                    this.currentMachine.restockItem(name, quantity);
                }
            } else if (input.equals("2")) {
                this.currentMachine.displayItems();
                System.out.println("Please enter the name of the item you would like to change the price of (Press 1 to exit):");
                String name = scanner.nextLine();
                while (!this.currentMachine.doesItemExist(name) && !name.equals("1")) {
                    System.out.println("Invalid item name, please try again:");
                    name = scanner.nextLine();
                }
                if (name.equals("1")) {
                    continue;
                } else {
                    System.out.println("Please enter the new price of " + name + ":");
                    double price = scanner.nextDouble();
                    scanner.nextLine();
                    while (price < 0) {
                        System.out.println("Invalid price, please enter a price greater than or equal to 0:");
                        price = scanner.nextDouble();
                        scanner.nextLine();
                    }
                    this.currentMachine.setItemPrice(name, price);
                }
            } else if (input.equals("3")) {
                this.currentMachine.displayMoneyBox();
                System.out.println("Please enter the amount of money you would like to collect (Press 0 to exit):");
                int amount = scanner.nextInt();
                scanner.nextLine();
                while (amount < 0) {
                    System.out.println("Invalid amount, please enter an amount greater than or equal to 0:");
                    amount = scanner.nextInt();
                    scanner.nextLine();
                }
                if (amount == 0) {
                    continue;
                } else {
                    this.currentMachine.collectMoney(amount);
                }

            } else if (input.equals("4")) {
                this.currentMachine.displayMoneyBox();
                System.out.println("Please enter the denomination you would like to replenish (Press 0 to exit):");
                int denomination = scanner.nextInt();
                scanner.nextLine();
                ArrayList<Integer> availableDenominationsExceptOne = new ArrayList<>(this.currentMachine.getMoneyBox().getAvailableDenominations());
                availableDenominationsExceptOne.remove(Integer.valueOf(1));

                while (!availableDenominationsExceptOne.contains(denomination) && denomination != 0) {
                    System.out.println("Invalid denomination, please enter a valid denomination:");
                    denomination = scanner.nextInt();
                    scanner.nextLine();
                }
                if (denomination == 0) {
                    continue;
                } else {
                    System.out.println("Please enter the quantity of " + denomination + " you would like to replenish:");
                    int quantity = scanner.nextInt();
                    scanner.nextLine();
                    while (quantity < 0) {
                        System.out.println("Invalid quantity, please enter a number greater than or equal to 0:");
                        quantity = scanner.nextInt();
                        scanner.nextLine();
                    }
                    this.currentMachine.replenishDenomination(denomination, quantity);
                }
            } else if (input.equals("5")) {
                this.currentMachine.displayTransactionSummary();
            } else if (input.equals("6")) {
                System.out.println("Goodbye!");
                break;
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }


    /**
     * Starts the main menu of the vending machine interface.
     */
    public void startMenu(){
        Scanner scanner = new Scanner(System.in);
        System.out.println("Welcome to the Vending Machine Interface!");
        while (true){
            System.out.println("Please select an option:");
            System.out.println("1. Create a new vending machine");
            System.out.println("2. Test a vending machine");
            System.out.println("3. Maintenance");
            System.out.println("4. Exit");
            System.out.println("5. test current machine");
            String input = scanner.nextLine();
            if (input.equals("1")){
                if(!(this.currentMachine == null)) {
                    System.out.println("A vending machine has already been created.");
                } else {
                    this.createVendingMachine();
                }
            }
            else if (input.equals("2")){
                if (this.currentMachine == null){
                    System.out.println("No vending machine has been created yet.");
                }
                else{
                    this.testFeatures();
                }
            }
            else if (input.equals("3")){
                if (this.currentMachine == null){
                    System.out.println("No vending machine has been created yet.");
                }
                else{
                    this.maintenance();
                }
            }else if(input.equals("4")){
                System.out.println("Goodbye!");
                break;
            } else if (input.equals("5")) {
                this.currentMachine = new VendingMachine();
            } else {
                System.out.println("Invalid input, please try again.");
            }
        }
    }
}