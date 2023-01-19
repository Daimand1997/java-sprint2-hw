import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YearlyReport {
    String month;
    int amount;
    boolean isExpense;

    public YearlyReport(String month, int amount, boolean isExpense) {
        this.month = month;
        this.amount = amount;
        this.isExpense = isExpense;
    }

    public static List<YearlyReport> getItemsOnExpense(List<YearlyReport> yearlyReports, boolean isExpense) {
        List<YearlyReport> yearlyReportCopy = new ArrayList<>(yearlyReports);
        for(int i = yearlyReportCopy.size() - 1; i >= 0; i--) {
            if(yearlyReportCopy.get(i).isExpense != isExpense) {
                yearlyReportCopy.remove(i);
            }
        }
        return yearlyReportCopy;
    }
    public static int getTotalSumOnExpense(List<YearlyReport> yearlyReports, boolean isExpense) {
        int toalSum = 0;
        for(YearlyReport yearlyReport : yearlyReports) {
            if(yearlyReport.isExpense == isExpense) {
                toalSum += yearlyReport.amount;
            }
        }
        return toalSum;
    }

    public static void printYearlyReport(HashMap<String, List<YearlyReport>> yearlyReport) {
        if(yearlyReport.size() > 0) {
            for(String nameYear : yearlyReport.keySet()) { // Перебираем полученные данных из годовых отчётов
                System.out.println("Рассматриваемый год: " + nameYear);
                printIncomeOnMonth(yearlyReport.get(nameYear));
                System.out.println("Средний расход за все месяцы в году: "
                        + (YearlyReport.getTotalSumOnExpense(yearlyReport.get(nameYear), true)
                        / YearlyReport.getItemsOnExpense(yearlyReport.get(nameYear), true).size()));
                System.out.println("Средний доход за все месяцы в году: "
                        + (YearlyReport.getTotalSumOnExpense(yearlyReport.get(nameYear), false)
                        / YearlyReport.getItemsOnExpense(yearlyReport.get(nameYear), false).size()));;
            }
            System.out.println("Выполнение операции успешно завершено");
        }
        else {
            System.out.println("Сначала необходимо считать все годовые отчёты (п.2)");
        }
    }

    static void printIncomeOnMonth(List<YearlyReport> yearlyReports) {
        System.out.println("Прибыль по каждому месяцу:");
        HashMap<String, Integer> incomeInMonth = new HashMap<>();
        for(int i = 0; i < yearlyReports.size(); i++) {
            if(!yearlyReports.get(i).isExpense) {
                incomeInMonth.put(yearlyReports.get(i).month, yearlyReports.get(i).amount);
            }
            for(int j = 0; j < yearlyReports.size(); j++) {
                if(yearlyReports.get(i).month.equals(yearlyReports.get(j).month) &&
                        !yearlyReports.get(i).isExpense &&
                        yearlyReports.get(j).isExpense) {
                    incomeInMonth.put(yearlyReports.get(i).month, yearlyReports.get(i).amount - yearlyReports.get(j).amount);
                }
            }
        }
        for(String nameMonth : incomeInMonth.keySet()) {
            System.out.println(Main.NAME_MONTH[Integer.parseInt(nameMonth) - 1] + ": " + incomeInMonth.get(nameMonth));
        }
    }

    public static void compareYearlyAndMonthly(HashMap<String, List<YearlyReport>> yearlyReport,
                                        HashMap<String, HashMap<String, HashMap<Boolean, Integer>>> totalSumMonthWithYear) {
        // Пробегаю по ключу годов у Мапы с объектами yearlyReport
        for (String nameYear : yearlyReport.keySet()) {
            // Пробегаю по ключу годов у Мапы с объектами totalSumMonthWithYear
            for (String nameYearInMonth : totalSumMonthWithYear.keySet()) {
                // Если в годовом отчёте не совпадает год месячного отчёта
                if (!nameYearInMonth.equals(nameYear)) {
                    continue;
                }
                // Получаю объект определённого года
                for (YearlyReport yearlyCurrentReport : yearlyReport.get(nameYear)) {
                    // Получаю номер месяца который будем сверять
                    for (String nameMonth : totalSumMonthWithYear.get(nameYearInMonth).keySet()) {
                        // Если месяца в годовом отчёте совпадают с месячным
                        if (yearlyCurrentReport.month.equals(nameMonth)) {
                            // Получаю и ключ и значение у Мапы
                            for (Map.Entry<Boolean, Integer> entry : totalSumMonthWithYear.get(nameYearInMonth).get(nameMonth).entrySet()) {
                                // Проверяю чтобы это были либо доходы, либо расходы у двух объектов
                                if (yearlyCurrentReport.isExpense == entry.getKey()
                                        && yearlyCurrentReport.amount != entry.getValue()) {
                                    System.out.println("Обнаружено несоответствие в отчётах у месяца: " +
                                            Main.NAME_MONTH[Integer.parseInt(nameMonth) - 1]); // Получаю из справочника имя
                                }
                            }
                        }
                    }
                }
            }
        }
        System.out.println("Выполнение операции успешно завершено");
    }

    public static List<YearlyReport> filingYearlyReportFromFile(List<String> readFile) {                    // Если успешно считали
        List<YearlyReport> currentYear = new ArrayList<>(); // Создаю список текущего года, который был в файле
        if (readFile != null && readFile.size() > 1) {
            // Пробегаюсь по строкам .csv файла
            for (int i = 1; i < readFile.size(); i++) {
                String[] attributes = readFile.get(i).split(",");
                YearlyReport currentItem = new YearlyReport(
                        attributes[0],
                        Integer.parseInt(attributes[1]),
                        Boolean.parseBoolean(attributes[2]));
                currentYear.add(currentItem); // Добавляю в список построчно объекты
            }
        }
        return currentYear;
    }
}
