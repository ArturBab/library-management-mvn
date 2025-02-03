package com.example.oop.oop_classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import com.example.oop.oop_classes.Book.BookException;

import lombok.Getter;

public class Library {

    @Getter
    private List<Book> books;
    private final String directory = "data";
    private final String filePath = directory + "/library.csv";
    private final File file = new File(filePath);
    private final Scanner scanner = new Scanner(System.in, "CP1251"); // Scanner один на весь класс

    public Library() {
        this.books = new ArrayList<>();
        System.out.println("Библиотека успешно создана.");
    }

    public void createLibrary() throws IOException {
        Files.createDirectories(Paths.get(directory));
        if (!file.exists()) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID;Название;Автор;Год\n");
            }
            System.out.println("Файл " + filePath + " создан успешно.");
        } else {
            System.out.println("Файл " + filePath + " уже существует.");
        }
    }

    public void addBook(Book book) throws IOException {
        System.out.println(" Проверка: добавление книги началось");

        // Проверяем, есть ли такая книга в файле
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 4) {
                    if (book.getName().equals(data[1].trim()) &&
                        book.getAuthor().equals(data[2].trim()) &&
                        book.getYear() == Integer.parseInt(data[3].trim())) {
                        
                        System.out.println("⚠ Книга уже существует!");
                        return; 
                    }
                }
            }
        }

        System.out.println(" Проверка: книга не найдена, идёт запись");

        // Записываем в файл
        String csvLine = book.getId() + ";" + book.getName() + ";" + book.getAuthor() + ";" + book.getYear();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(csvLine);
            writer.newLine();
            System.out.println(" Книга \"" + book.getName() + "\" добавлена.");
        }
    }
    public void removeBookByID(long id) throws LibraryExcp, IOException {
        LibraryExcp.checkLibraryNotEmpty(file);

        boolean found = false;
        File tempFile = new File(directory + "/temp.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(file));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String header = reader.readLine();
            writer.write(header);
            writer.newLine();

            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 4 && Long.parseLong(data[0]) == id) {
                    found = true;
                    continue; // Пропускаем удалённую строку
                }
                writer.write(line);
                writer.newLine();
            }
        }

        if (!found) {
            System.out.println("Книга с ID " + id + " не найдена.");
            return;
        }

        file.delete();
        tempFile.renameTo(file);
        System.out.println("Книга с ID " + id + " удалена.");
    }

    public void updateBook(Long id) throws LibraryExcp, IOException, BookException {
        LibraryExcp.checkLibraryNotEmpty(file);

        boolean foundID = false;
        boolean statusUpdate = false;
        List<String> upBooks = new ArrayList<>();

        try (BufferedReader checkRead = new BufferedReader(new FileReader(file))) {
            String header = checkRead.readLine();
            upBooks.add(header);

            String line;
            while ((line = checkRead.readLine()) != null) {
                String[] data = line.split(";");

                if (data.length == 4) {
                    Long exIdString = Long.parseLong(data[0]);
                    if (exIdString.equals(id)) {
                        foundID = true;
                        String exName = data[1];
                        String exAuthor = data[2];
                        int exYear = Integer.parseInt(data[3]);

                        System.out.println("Что поменять? (название/автор/год)");
                        String choice = scanner.nextLine().trim().toLowerCase();

                        switch (choice) {
                            case "название":
                                System.out.print("Новое название: ");
                                exName = scanner.nextLine();
                                statusUpdate = true;
                                break;
                            case "автор":
                                System.out.print("Новый автор: ");
                                exAuthor = scanner.nextLine();
                                statusUpdate = true;
                                break;
                            case "год":
                                System.out.print("Новый год: ");
                                exYear = scanner.nextInt();
                                Book.BookException.validateYear(exYear);
                                scanner.nextLine(); // Очистка буфера
                                statusUpdate = true;
                                break;
                            default:
                                System.out.println("Некорректный ввод. Изменения не внесены.");
                        }

                        upBooks.add(exIdString + ";" + exName + ";" + exAuthor + ";" + exYear);
                        continue;
                    }
                }
                upBooks.add(line);
            }
        }

        if (!statusUpdate || !foundID) {
            System.out.println("Книга с ID " + id + " не была обновлена.");
            return;
        }

        try (BufferedWriter write = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8))) {
            for (String string : upBooks) {
                write.write(string);
                write.newLine();
            }
        }
        System.out.println("Книга с ID " + id + " обновлена.");
    }

    public void listAllBooks() throws LibraryExcp, IOException {
        if (!file.exists() || file.length() == 0) {
            throw new LibraryExcp("Файл библиотеки пуст", LibraryExcp.INVALID_BOOKS);
        }

        System.out.println("--- СПИСОК КНИГ ---");
        try (BufferedReader read = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            read.readLine();

            String line;
            while ((line = read.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length != 4) {
                    System.out.println("⚠ Ошибка в строке: " + line);
                    continue;
                }

                try {
                    long id = Long.parseLong(data[0].trim());
                    String name = data[1].trim();
                    String author = data[2].trim();
                    int year = Integer.parseInt(data[3].trim());

                    System.out.println("\nID: " + id +
                            "\nНазвание: " + name +
                            "\nАвтор: " + author +
                            "\nГод: " + year);
                } catch (NumberFormatException e) {
                    System.out.println("⚠ Ошибка чтения данных: " + line);
                }
            }
        }
        System.out.println("--- ---");
    }

    @Getter
    public static class LibraryExcp extends Exception {

        private int errorCode;

        public static final int INVALID_BOOKS = 301;
        public static final int AUTHOR_EXISTS = 302;

        public LibraryExcp(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        public static void checkLibraryNotEmpty(File file) throws LibraryExcp, IOException {
            if (!file.exists() || file.length() == 0) { // Если файла нет или он пуст
                throw new LibraryExcp("Файл библиотеки отсутствует или пуст.", LibraryExcp.INVALID_BOOKS);
            }
            try (BufferedReader read = new BufferedReader(new FileReader("data/library.csv"))) {
                read.readLine(); // Пропускаем заголовок
                if (read.readLine() == null) { // Если нет ни одной книги
                    throw new LibraryExcp("В библиотеке нет книг!", LibraryExcp.INVALID_BOOKS);
                }
            }
        }

        public static void checkAuthor(String author, List<Book> books) throws BookException, LibraryExcp {
            BookException.validateAuthor(author);
            if (books.stream()
                    .noneMatch(book -> book.getAuthor().equals(author))) {
                throw new LibraryExcp("Книга с автором: " + author + " не найдена.", LibraryExcp.AUTHOR_EXISTS);
            }
        }

        @Override
        public String toString() {
            return "КОД ОШИБКИ: " + getErrorCode() +
                    "\nСООБЩЕНИЕ: " + getMessage();
        }
    }
}

