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
                        List<MonthlyReport> currentMonth = MonthlyReport.filingMonthlyReportFromFile(readFile);
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
                    List<YearlyReport> currentYear = YearlyReport.filingYearlyReportFromFile(readFile);
                    yearlyReport.put(nameFile.substring(nameFile.length() - 8, nameFile.length() - 4), currentYear); // Записываю в Мапу где ключ является год из файла
                }
                System.out.println("Выполнение операции успешно завершено");
            } else if (command.equals("3")) {
                // Если первый и второй пункт успешно были вызваны
                if (monthlyReport.size() > 0 && yearlyReport.size() > 0) {
                        YearlyReport.compareYearlyAndMonthly(yearlyReport, MonthlyReport.getTotalSumMonth(monthlyReport));
                } else {
                    System.out.println("Сначала необходимо считать все месячные (п.1) и годовые (п.2) отчёты.");
                }
            } else if (command.equals("4")) {
                MonthlyReport.printMonthlyReports(monthlyReport);
            } else if (command.equals("5")) {
                YearlyReport.printYearlyReport(yearlyReport);
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
            return Files.readAllLines(Path.of(path + File.separator + nameFile));
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
}

