import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Main {
    // Справочиник наименования месяцов
    final static String[] NAME_MONTH = {"Январь", "Февраль", "Март", "Апрель", "Май", "Июнь", "Июль", "Август", "Сентябрь", "Октябрь", "Ноябрь", "Декабрь"};

    public static void main(String[] args) {
        final String PATH_WITH_REPORT = "resources"; // Путь папки где хранятся .csv
        Scanner scanner = new Scanner(System.in);
        HashMap<String, HashMap<String, List<MonthlyReport>>> monthlyReport = new HashMap<>(); // Мапа где ключ Год, в котором Мапа где ключ Месяц и со значением списка объектов
        HashMap<String, List<YearlyReport>> yearlyReport = new HashMap<>(); // Мапа где ключ Год и со значением списком объектов

        while (true) {
            printMenu(); // Вывод меню выбора
            String command = scanner.nextLine().trim(); // Считывание выбранного пункта
            if (command.equals("1")) {
                List<String> nameFiles = getNameFileInFolder(PATH_WITH_REPORT, "m."); // Получаем список файлов которые начинаются c m. по пути resources
                HashMap<String, List<MonthlyReport>> currentMonthInYear = new HashMap<>(); // Создаём Мапу в которой будет хранится в ключе номер месяца, в значения список объектов
                // Пробегаемся по полученным файлам
                for (String nameFile : nameFiles) {
                    List<String> readFile = readFileContentsWithStart(PATH_WITH_REPORT, nameFile); // Получаем все строки из конкретного файла
                    // Если успешно считали
                    if (readFile != null && readFile.size() > 1) {
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
                        currentMonthInYear.put(nameFile.substring(nameFile.length() - 6, nameFile.length() - 4), currentMonth); // Записываю в Мапу месяц с которого считывал данные
                        monthlyReport.put(nameFile.substring(nameFile.length() - 10, nameFile.length() - 6), currentMonthInYear); // Записываю в Мапу год с которого считывал данные из файла
                    }
                }
                System.out.println("Выполнение операции успешно завершено");
            } else if (command.equals("2")) {
                List<String> nameFiles = getNameFileInFolder(PATH_WITH_REPORT, "y."); // Получить список файлов которые начинаются с y.
                // Пробегаюсь по файлам
                for (String nameFile : nameFiles) {
                    List<String> readFile = readFileContentsWithStart(PATH_WITH_REPORT, nameFile); // Считываю определённый файл
                    // Если успешно считали
                    if (readFile != null && readFile.size() > 1) {
                        List<YearlyReport> currentYear = new ArrayList<>(); // Создаю список текущего года, который был в файле
                        // Пробегаюсь по строкам .csv файла
                        for (int i = 1; i < readFile.size(); i++) {
                            String[] attributes = readFile.get(i).split(",");
                            YearlyReport currentItem = new YearlyReport(
                                    attributes[0],
                                    Integer.parseInt(attributes[1]),
                                    Boolean.parseBoolean(attributes[2]));
                            currentYear.add(currentItem); // Добавляю в список построчно объекты
                        }
                        yearlyReport.put(nameFile.substring(nameFile.length() - 8, nameFile.length() - 4), currentYear); // Записываю в Мапу где ключ является год из файла
                    }
                }
                System.out.println("Выполнение операции успешно завершено");
            } else if (command.equals("3")) {
                // Если первый и второй пункт успешно были вызваны
                if (monthlyReport.size() > 0 && yearlyReport.size() > 0) {
                    HashMap<String, HashMap<String, HashMap<Boolean, Integer>>> totalSumMonthWithYear = new HashMap<>(); // Создаю Мапу где Ключ будет Год, в которой хранится Мапа где Ключ месяц
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
                                            // Проверяю чтобы это были либо доходы либо расходы у двух объектов
                                            if (yearlyCurrentReport.is_expense == entry.getKey()
                                                    && yearlyCurrentReport.amount != entry.getValue()) {
                                                System.out.println("Обнаружено несоответствие в отчётах у месяца: " +
                                                        NAME_MONTH[Integer.parseInt(nameMonth) - 1]); // Получаю из справочника имя
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    System.out.println("Выполнение операции успешно завершено");
                } else {
                    System.out.println("Сначала необходимо считать все месячные (п.1) и годовые (п.2) отчёты.");
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
                    System.out.println("Сначала необходимо считать все месячные отчёты (п.1)");
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
                    System.out.println("Выполнение операции успешно завершено");
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

