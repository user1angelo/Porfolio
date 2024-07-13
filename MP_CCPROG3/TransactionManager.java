import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * TransactionManager
 */
public class TransactionManager {
    private List<Transaction> transactions;


    public TransactionManager() {
        this.transactions = new ArrayList<Transaction>();
    }

    public void addTransaction(String item, double price,  LocalDateTime time) {
        Transaction transaction = new Transaction(item, price, time);
        this.transactions.add(transaction);
    }

    public List<Transaction> getTransactions() {
        return this.transactions;
    }

    public String displayTransactionSummary() {
        StringBuilder summaryBuilder = new StringBuilder();
        summaryBuilder.append("Transaction Summary\n");
        summaryBuilder.append("===================\n");

        for (Transaction transaction : transactions) {
            summaryBuilder.append(transaction.getItem())
                    .append(" - ")
                    .append(transaction.getPrice())
                    .append(" - ")
                    .append(transaction.getTime())
                    .append("\n");
        }

        summaryBuilder.append("\n");

        return summaryBuilder.toString();
    }

}