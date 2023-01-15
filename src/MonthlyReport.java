import java.util.ArrayList;
import java.util.List;

public class MonthlyReport {
    String item_name;
    boolean is_expense;
    int quantity;

    int sum_of_one;

    public MonthlyReport(String item_name, boolean is_expense, int quantity, int sum_of_one) {
        this.item_name = item_name;
        this.is_expense = is_expense;
        this.quantity = quantity;
        this.sum_of_one = sum_of_one;
    }

    public static MonthlyReport getMaxItem(List<MonthlyReport> monthlyReports) {
        MonthlyReport maxMonthlyReport = monthlyReports.get(0);
        for(MonthlyReport monthlyReport : monthlyReports) {
            if(maxMonthlyReport.sum_of_one * monthlyReport.quantity
                    < monthlyReport.sum_of_one * monthlyReport.quantity) {
                maxMonthlyReport = monthlyReport;
            }
        }
        return maxMonthlyReport;
    }

    public static List<MonthlyReport> getItemsOnExpense(List<MonthlyReport> monthlyReports, boolean is_expense) {
        List<MonthlyReport> monthlyReportsCopy = new ArrayList<>(monthlyReports);
        for(int i = monthlyReportsCopy.size() - 1; i >= 0; i--) {
            if(monthlyReportsCopy.get(i).is_expense != is_expense) {
                monthlyReportsCopy.remove(i);
            }
        }
        return monthlyReportsCopy;
    }

    public static int getTotalSumOnExpense(List<MonthlyReport> monthlyReports, boolean is_expense) {
        int toalSum = 0;
        for(MonthlyReport monthlyReport : monthlyReports) {
            if(monthlyReport.is_expense == is_expense) {
                toalSum += monthlyReport.sum_of_one * monthlyReport.quantity;
            }
        }
        return toalSum;
    }
}