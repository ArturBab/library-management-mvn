package com.example.oop.oop_classes;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import com.example.oop.oop_classes.Book.BookException;

import lombok.Getter;

public class Library {

    @Getter
    private List<Book> books;

    public Library() {
        this.books = new ArrayList<Book>();
        System.out.println("Библиотека успешно создана.");
    }

    public void createLibrary() throws IOException {
        String directory = "data"; // Папка для хранения файла
        Files.createDirectories(Paths.get(directory)); // Создаём папку, если её нет

        String filePath = directory + "/library.csv"; // Путь
        File file = new File(filePath); // Создаем уже файл

        if (!file.exists()) { // Проверяем, есть ли файл
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID;Название;Автор;Год\n"); // Записываем заголовки
            }
            System.out.println("Файл " + filePath + " создан успешно.");
        } else {
            System.out.println("Файл " + filePath + " уже существует.");
        }
    }

    public void addBook(Book book) throws IOException {
        getBooks().add(book);
        createLibrary();

        String csvLine = book.getId() + ";" + book.getName() + ";" + book.getAuthor() + ";" + book.getYear();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter("data/library.csv", true))) {
            writer.write(csvLine);
            writer.newLine();
            System.out.println("Книга с названием: " + book.getName() + "\n" + "БЫЛА УСПЕШНО ДОБАВЛЕНА В БИБЛИОТЕКУ.");
        } catch (IOException e) {
            System.out.println("Ошибка при записи в CSV-файл: " + e.getMessage());
        }
    }

    public void removeBookByID(long id) {
        try {
            BookException.checkID(id, getBooks());
            getBooks().removeIf(book -> book.getId() == id); // Удаляет книгу, у которой ID совпадает
            System.out.println("Книга с ID " + id + " успешно удалена.");
        } catch (BookException e) {
            System.out.println(e);
        }
    }

    public void listAllBooks() throws LibraryExcp, IOException {
        LibraryExcp.checkLibraryNotEmpty(books);

        System.out.println("--- СПИСОК КНИГ ---");
        try (BufferedReader read = new BufferedReader(new FileReader("data/library.csv"))) {
            read.readLine(); // Пропускает строку. В этом случае заголовок.

            String line; // строка
            while ((line = read.readLine()) != null) {
                String[] data = line.split(";"); // Разделяется по ";"
                System.out.println("ID: " + data[0] +
                        "\nНазвание: " + data[1] +
                        "\nАвтор: " + data[2] +
                        "\nГод: " + data[3]);
            }

        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        System.out.println("--- ---");
    }

    public void listBooksByAthor(String author) throws BookException, LibraryExcp {
        try {
            LibraryExcp.checkLibraryNotEmpty(books);
            LibraryExcp.checkAuthor(author, books);
            for (Book book : books) {
                if (book.getAuthor().equals(author)) {
                    System.out.println(book);
                }
            }
        } catch (LibraryExcp e) {
            System.out.println(e);
        }
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

        public static void checkLibraryNotEmpty(List<Book> books) throws LibraryExcp {
            if (books.isEmpty()) {
                throw new LibraryExcp("В библиотеке нет книг. Добавьте хотя бы одну книгу", INVALID_BOOKS);
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
