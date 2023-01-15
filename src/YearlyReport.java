import java.util.ArrayList;
import java.util.List;

public class YearlyReport {
    String month;
    int amount;
    boolean is_expense;

    public YearlyReport(String month, int amount, boolean is_expense) {
        this.month = month;
        this.amount = amount;
        this.is_expense = is_expense;
    }

    public static List<YearlyReport> getItemsOnExpense(List<YearlyReport> yearlyReports, boolean is_expense) {
        List<YearlyReport> yearlyReportCopy = new ArrayList<>(yearlyReports);
        for(int i = yearlyReportCopy.size() - 1; i >= 0; i--) {
            if(yearlyReportCopy.get(i).is_expense != is_expense) {
                yearlyReportCopy.remove(i);
            }
        }
        return yearlyReportCopy;
    }
    public static int getTotalSumOnExpense(List<YearlyReport> yearlyReports, boolean is_expense) {
        int toalSum = 0;
        for(YearlyReport yearlyReport : yearlyReports) {
            if(yearlyReport.is_expense == is_expense) {
                toalSum += yearlyReport.amount;
            }
        }
        return toalSum;
    }
}
