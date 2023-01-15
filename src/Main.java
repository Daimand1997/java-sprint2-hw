import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    final static String[] NAME_MONTH = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    public static void main(String[] args) {
        final String PATH_WITH_REPORT = "resources";
        Scanner scanner = new Scanner(System.in);
        HashMap<String, HashMap<String, List<MonthlyReport>>> monthlyReport = new HashMap<>();
        HashMap<String, List<YearlyReport>> yearlyReport = new HashMap<>();

        while (true) {
            printMenu();
            String command = scanner.nextLine().trim();
            if (command.equals("1")) {
                List<String> nameFiles = getNameFileInFolder(PATH_WITH_REPORT, "m.");
                HashMap<String, List<MonthlyReport>> currentMonthInYear = new HashMap<>();
                for (String nameFile : nameFiles) {
                    List<String> readFile = readFileContentsWithStart(PATH_WITH_REPORT, nameFile);
                    if (readFile != null && readFile.size() > 1) {
                        List<MonthlyReport> currentMonth = new ArrayList<>();
                        for (int i = 1; i < readFile.size(); i++) {
                            String[] attributes = readFile.get(i).split(",");
                            MonthlyReport currentItem = new MonthlyReport(
                                    attributes[0],
                                    Boolean.parseBoolean(attributes[1]),
                                    Integer.parseInt(attributes[2]),
                                    Integer.parseInt(attributes[3]));
                            currentMonth.add(currentItem);
                        }
                        currentMonthInYear.put(nameFile.substring(nameFile.length() - 6, nameFile.length() - 4), currentMonth);
                        monthlyReport.put(nameFile.substring(nameFile.length() - 10, nameFile.length() - 6), currentMonthInYear);
                    }
                }
            } else if (command.equals("2")) {
                List<String> nameFiles = getNameFileInFolder(PATH_WITH_REPORT, "y.");
                for (String nameFile : nameFiles) {
                    List<String> readFile = readFileContentsWithStart(PATH_WITH_REPORT, nameFile);
                    if (readFile != null && readFile.size() > 1) {
                        List<YearlyReport> currentYear = new ArrayList<>();
                        for (int i = 1; i < readFile.size(); i++) {
                            String[] attributes = readFile.get(i).split(",");
                            YearlyReport currentItem = new YearlyReport(
                                    attributes[0],
                                    Integer.parseInt(attributes[1]),
                                    Boolean.parseBoolean(attributes[2]));
                            currentYear.add(currentItem);
                        }
                        yearlyReport.put(nameFile.substring(nameFile.length() - 8, nameFile.length() - 4), currentYear);
                    }
                }
                System.out.println("Выполнение операции успешно завершено");
            } else if (command.equals("3")) {
                if (monthlyReport.size() > 0 && yearlyReport.size() > 0) {
                    HashMap<String, HashMap<String, HashMap<Boolean, Integer>>> totalSumMonthWithYear = new HashMap<>();
                    for (String nameYear : monthlyReport.keySet()) {
                        HashMap<String, HashMap<Boolean, Integer>> totalSumMonth = new HashMap<>();
                        for (String nameMonth : monthlyReport.get(nameYear).keySet()) {
                            HashMap<Boolean, Integer> currentSumMonth = new HashMap<>();
                            currentSumMonth.put(true, MonthlyReport.getTotalSumOnExpense(monthlyReport.get(nameYear).get(nameMonth), true));
                            currentSumMonth.put(false, MonthlyReport.getTotalSumOnExpense(monthlyReport.get(nameYear).get(nameMonth), false));
                            totalSumMonth.put(nameMonth, currentSumMonth);
                        }
                        totalSumMonthWithYear.put(nameYear, totalSumMonth);
                    }
                    for (String nameYear : yearlyReport.keySet()) {
                        for (String nameYearInMonth : totalSumMonthWithYear.keySet()) {
                            if (!nameYearInMonth.equals(nameYear)) {
                                continue;
                            }
                            for (YearlyReport yearlyCurrentReport : yearlyReport.get(nameYear)) {
                                for (String nameMonth : totalSumMonthWithYear.get(nameYearInMonth).keySet()) {
                                    if (yearlyCurrentReport.month.equals(nameMonth)) {
                                        for (Map.Entry<Boolean, Integer> entry : totalSumMonthWithYear.get(nameYearInMonth).get(nameMonth).entrySet()) {
                                            if (yearlyCurrentReport.is_expense == entry.getKey()
                                                    && yearlyCurrentReport.amount != entry.getValue()) {
                                                System.out.println("Обнаружено несоответствие в отчётах у месяца: " +
                                                        NAME_MONTH[Integer.parseInt(nameMonth) - 1]);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Выполнение операции успешно завершено");
                } else {
                    System.out.println("Сначла необходимо считать все месячные (п.1) и годовые (п.2) отчёты.");
                }
            } else if (command.equals("4")) {
                if (monthlyReport.size() > 0) {
                    for (String nameYear : monthlyReport.keySet()) {
                        for (String nameMonth : monthlyReport.get(nameYear).keySet()) {
                            List<MonthlyReport> currentMonthReport = monthlyReport.get(nameYear).get(nameMonth);
                            MonthlyReport maxIncomeItem = MonthlyReport.getMaxItem(MonthlyReport.getItemsOnExpense(currentMonthReport, false));
                            MonthlyReport maxExpenseItem = MonthlyReport.getMaxItem(MonthlyReport.getItemsOnExpense(currentMonthReport, true));
                            System.out.printf("Название месяца: %s,%nCамый прибыльный товар: %s c общим доходом %d,%nСамый неприбыльный товар: %s с общей тратой %d%n",
                                    NAME_MONTH[Integer.parseInt(nameMonth) - 1],
                                    maxIncomeItem.item_name,
                                    (maxIncomeItem.sum_of_one * maxIncomeItem.quantity),
                                    maxExpenseItem.item_name,
                                    (maxExpenseItem.sum_of_one * maxExpenseItem.quantity));
                        }
                    }
                    System.out.println("Выполнение операции успешно завершено");
                } else {
                    System.out.println("Сначла необходимо считать все месячные отчёты (п.1)");
                }
            } else if (command.equals("5")) {
                if(yearlyReport.size() > 0) {
                    for(String nameYear : yearlyReport.keySet()) {
                        System.out.println("Рассматриваемый год: " + nameYear);
                        printIncomeOnMonth(yearlyReport.get(nameYear));
                        System.out.println("Средний расход за все месяцы в году: "
                                + (YearlyReport.getTotalSumOnExpense(yearlyReport.get(nameYear), true)
                                / YearlyReport.getItemsOnExpense(yearlyReport.get(nameYear), true).size()));
                        System.out.println("Средний доход за все месяцы в году: "
                                + (YearlyReport.getTotalSumOnExpense(yearlyReport.get(nameYear), false)
                                / YearlyReport.getItemsOnExpense(yearlyReport.get(nameYear), false).size()));;
                    }
                }
                else {
                    System.out.println("Сначла необходимо считать все годовые отчёты (п.2)");
                }
            } else {
                break;
            }
        }
    }

    static void printMenu() {
        System.out.println("Выберите желаемый пункт:");
        System.out.println("1 - Считать все месячные отчёты");
        System.out.println("2 - Считать годовой отчёт");
        System.out.println("3 - Сверить отчёты");
        System.out.println("4 - Вывести информацию о всех месячных отчётах");
        System.out.println("5 - Вывести информацию о годовом отчёте");
        System.out.println("Любой другой символ - Выйти из программы");
    }

    static List<String> readFileContentsWithStart(String path, String nameFile) {
        try {
            return Files.readAllLines(Path.of(path + "\\" + nameFile));
        } catch (IOException e) {
            System.out.printf("Невозможно прочитать файл %s по пути %s. " +
                    "Возможно файл не находится в нужной директории.%n", nameFile, path);
            return Collections.emptyList();
        }
    }

    static List<String> getNameFileInFolder(String path, String startName) {
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        List<String> nameFiles = new ArrayList<>();
        if (listOfFiles != null) {
            for (File listOfFile : listOfFiles) {
                if (listOfFile.isFile() && listOfFile.getName().startsWith(startName)) {
                    nameFiles.add(listOfFile.getName());
                }
            }
        }
        return nameFiles;
    }

    static void printIncomeOnMonth(List<YearlyReport> yearlyReports) {
        System.out.println("Прибыль по каждому месяцу:");
        HashMap<String, Integer> incomeInMonth = new HashMap<>();
        for(int i = 0; i < yearlyReports.size(); i++) {
            if(!yearlyReports.get(i).is_expense) {
                incomeInMonth.put(yearlyReports.get(i).month, yearlyReports.get(i).amount);
            }
            for(int j = 0; j < yearlyReports.size(); j++) {
                if(yearlyReports.get(i).month.equals(yearlyReports.get(j).month) &&
                        !yearlyReports.get(i).is_expense &&
                        yearlyReports.get(j).is_expense) {
                    incomeInMonth.put(yearlyReports.get(i).month, yearlyReports.get(i).amount - yearlyReports.get(j).amount);
                }
            }
        }
        for(String nameMonth : incomeInMonth.keySet()) {
            System.out.println(NAME_MONTH[Integer.parseInt(nameMonth) - 1] + ": " + incomeInMonth.get(nameMonth));
        }
    }
}

