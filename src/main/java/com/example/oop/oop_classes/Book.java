package com.example.oop.oop_classes;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

import lombok.Getter;

@Getter
public class Book {

    private static long counter = 0;

    static { // Статический блок для загрузки ID при запуске программы
        counter = loadMaxId();
    }

    private long idCounter() {
        return ++counter; // Теперь ID корректно увеличивается
    }

    private final long id = idCounter();
    private String name, author;
    private int year;

    public static long loadMaxId() { // Метод загрузки максимального ID
        long maxId = 0;
        File file = new File("data/library.csv");

        if (!file.exists()) {
            return 0;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line;
            while ((line = reader.readLine()) != null) { // Читаем ВСЕ строки
                String[] data = line.split(";");
                if (data.length >= 1) {
                    long id = Long.parseLong(data[0].trim());
                    maxId = Math.max(maxId, id); // Находим максимальный ID
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Ошибка загрузки ID: " + e.getMessage());
        }
        return maxId; // Возвращаем последний ID
    }

    public Book() {
        this.name = "unnamed";
        this.author = "unknown";
        this.year = 0;
    }

    public Book(String name, String author, int year) throws BookException {
        BookException.validateName(name);
        BookException.validateAuthor(author);
        BookException.validateYear(year);
        this.author = author;
        this.name = name;
        this.year = year;
    }

    public void setName(String name) throws BookException {
        BookException.validateName(name);
        this.name = name;
    }

    public void setAuthor(String author) throws BookException {
        BookException.validateAuthor(author);
        this.author = author;
    }

    public void setYear(int year) throws BookException {
        BookException.validateYear(year);
        this.year = year;
    }

    @Override
    public String toString() {
        return "ID: " + getId() +
                "\nКНИГА: " + getName() +
                "\nАВТОР: " + getAuthor() +
                "\nГОД ИЗДАНИЯ: " + getYear();
    }

    @Getter
    public static class BookException extends Exception {

        private int errorCode;

        public static final int INVALID_NAME = 101;
        public static final int INVALID_AUTHOR = 102;
        public static final int INVALID_YEAR = 103;
        public static final int BOOK_NOT_FOUND = 201;

        public BookException(String message, int errorCode) {
            super(message);
            this.errorCode = errorCode;
        }

        // Статические методы проверки
        public static void validateName(String name) throws BookException {
            if (name == null || name.isBlank()) {
                throw new BookException("Название книги не может быть пустым", INVALID_NAME);
            }
        }

        public static void validateAuthor(String name) throws BookException {
            if (name == null || name.isBlank()) {
                throw new BookException("Название автора книги не может быть пустым", INVALID_AUTHOR);
            }
        }

        public static void validateYear(int year) throws BookException {
            if (year < 0 || year > LocalDate.now().getYear()) {
                throw new BookException("Некорректный год издания книги", INVALID_YEAR);
            }
        }

        public static void checkID(long id, List<Book> books) throws BookException {
            if (books.stream()
                    .noneMatch(b -> b.getId() == id)) {
                throw new BookException("Книга с ID " + id + " не найдена.", BookException.BOOK_NOT_FOUND);
            }
        }

        @Override
        public String toString() {
            return "КОД ОШИБКИ: " + getErrorCode() +
                    "\nСООБЩЕНИЕ: " + getMessage();
        }
    }
}
