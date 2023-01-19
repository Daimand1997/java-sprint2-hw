import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MonthlyReport {
    String itemName;
    boolean isExpense;
    int quantity;

    int sumOfOne;

    public MonthlyReport(String itemName, boolean isExpense, int quantity, int sumOfOne) {
        this.itemName = itemName;
        this.isExpense = isExpense;
        this.quantity = quantity;
        this.sumOfOne = sumOfOne;
    }

    public static MonthlyReport getMaxItem(List<MonthlyReport> monthlyReports) {
        MonthlyReport maxMonthlyReport = monthlyReports.get(0);
        for(MonthlyReport monthlyReport : monthlyReports) {
            if(maxMonthlyReport.sumOfOne * monthlyReport.quantity
                    < monthlyReport.sumOfOne * monthlyReport.quantity) {
                maxMonthlyReport = monthlyReport;
            }
        }
        return maxMonthlyReport;
    }

    public static List<MonthlyReport> getItemsOnExpense(List<MonthlyReport> monthlyReports, boolean isExpense) {
        List<MonthlyReport> monthlyReportsCopy = new ArrayList<>(monthlyReports);
        for(int i = monthlyReportsCopy.size() - 1; i >= 0; i--) {
            if(monthlyReportsCopy.get(i).isExpense != isExpense) {
                monthlyReportsCopy.remove(i);
            }
        }
        return monthlyReportsCopy;
    }

    public static int getTotalSumOnExpense(List<MonthlyReport> monthlyReports, boolean isExpense) {
        int toalSum = 0;
        for(MonthlyReport monthlyReport : monthlyReports) {
            if(monthlyReport.isExpense == isExpense) {
                toalSum += monthlyReport.sumOfOne * monthlyReport.quantity;
            }
        }
        return toalSum;
    }

    public static void printMonthlyReports(HashMap<String, HashMap<String, List<MonthlyReport>>> monthlyReport) {
        if (monthlyReport.size() > 0) {
            for (String nameYear : monthlyReport.keySet()) { // Перебираем года среди месячных отчётов
                for (String nameMonth : monthlyReport.get(nameYear).keySet()) { // Перебираем месяца среди месячных отчётов
                    List<MonthlyReport> currentMonthReport = monthlyReport.get(nameYear).get(nameMonth); // Сохраняем отдельно все строки в месячном отчёте из файла
                    MonthlyReport maxIncomeItem = MonthlyReport.getMaxItem(MonthlyReport.getItemsOnExpense(currentMonthReport, false)); // Получаем объект с максимальным доходом
                    MonthlyReport maxExpenseItem = MonthlyReport.getMaxItem(MonthlyReport.getItemsOnExpense(currentMonthReport, true)); // Получаем объект с максимальным расходом
                    System.out.printf("Название месяца: %s,%nCамый прибыльный товар: %s c общим доходом %d,%nСамый неприбыльный товар: %s с общей тратой %d%n",
                            Main.NAME_MONTH[Integer.parseInt(nameMonth) - 1],
                            maxIncomeItem.itemName,
                            (maxIncomeItem.sumOfOne * maxIncomeItem.quantity),
                            maxExpenseItem.itemName,
                            (maxExpenseItem.sumOfOne * maxExpenseItem.quantity));
                }
            }
            System.out.println("Выполнение операции успешно завершено");
        } else {
            System.out.println("Сначала необходимо считать все месячные отчёты (п.1)");
        }
    }

    public static HashMap<String, HashMap<String, HashMap<Boolean, Integer>>> getTotalSumMonth(HashMap<String, HashMap<String, List<MonthlyReport>>> monthlyReport) {
        HashMap<String, HashMap<String, HashMap<Boolean, Integer>>> totalSumMonthWithYear = new HashMap<>();
        // Пробегаюсь по годам из MonthlyReport
        for (String nameYear : monthlyReport.keySet()) {
            HashMap<String, HashMap<Boolean, Integer>> totalSumMonth = new HashMap<>(); // Создаю Мапу в которую буду записывать расходы и доходы определённого месяца в году
            // Получаю наименование месяца в определённом году
            for (String nameMonth : monthlyReport.get(nameYear).keySet()) {
                HashMap<Boolean, Integer> currentSumMonth = new HashMap<>(); // Создаю Мапу в которой хранится общая сумма доходов и расходов у в определённом месяца
                currentSumMonth.put(true, MonthlyReport.getTotalSumOnExpense(monthlyReport.get(nameYear).get(nameMonth), true));
                currentSumMonth.put(false, MonthlyReport.getTotalSumOnExpense(monthlyReport.get(nameYear).get(nameMonth), false));
                totalSumMonth.put(nameMonth, currentSumMonth);
            }
            totalSumMonthWithYear.put(nameYear, totalSumMonth);
        }
        return totalSumMonthWithYear;
    }

    public static List<MonthlyReport> filingMonthlyReportFromFile(List<String> readFile) {                    // Если успешно считали
        List<MonthlyReport> currentMonth = new ArrayList<>(); // Создаём список объектов для определённого месяца
        // Пробегаемся по каждой строчке файла
        for (int i = 1; i < readFile.size(); i++) {
            String[] attributes = readFile.get(i).split(",");
            // Создаём объект по полученным данным из файла и передаём их в конструктор
            MonthlyReport currentItem = new MonthlyReport(
                    attributes[0],
                    Boolean.parseBoolean(attributes[1]),
                    Integer.parseInt(attributes[2]),
                    Integer.parseInt(attributes[3]));
            currentMonth.add(currentItem); // Добавляем в список объектов текущую строку
        }
        return currentMonth;
    }
}