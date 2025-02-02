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

    private final String directory = "data"; // Директория для хранения файла
    private final String filePath = directory + "/library.csv"; // Путь к файлу
    private final File file = new File(filePath); // Создаем уже файл

    public Library() {
        this.books = new ArrayList<Book>();
        System.out.println("Библиотека успешно создана.");
    }

    public void createLibrary() throws IOException {

        Files.createDirectories(Paths.get(directory)); // Создаём директорию, если её нет

        if (!file.exists()) { // Проверяем, есть ли файл (в контексте этого условия - не существует ли)
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write("ID;Название;Автор;Год\n"); // Записываем заголовки
            }
            System.out.println("Файл " + filePath + " создан успешно.");
        } else {
            System.out.println("Файл " + filePath + " уже существует.");
        }
    }

    public void addBook(Book book) throws IOException {
        // Проверка, есть ли такая книга в файле
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            reader.readLine(); // Пропускаем заголовок
            String line; // Строка
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length == 4) { // Убедимся, что строка содержит все данные
                    String exNameString = data[1];
                    String exAuthorString = data[2];
                    int exYearInt = Integer.parseInt(data[3]);

                    if (book.getName().equals(exNameString)
                            && book.getAuthor().equals(exAuthorString)
                            && book.getYear() == exYearInt) {

                        System.out.println("Книга с названием \"" + book.getName() + "\" уже существует в библиотеке.");
                        return; // Не добавит дубликат
                    }
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка при чтении файла: " + e.getMessage());
        } catch (NumberFormatException ex) {
            System.out.println("Ошибка преобразования числа: " + ex.getMessage());
        }
        // Если книга не найдена в файле, записываем её
        String csvLine = book.getId() + ";" + book.getName() + ";" + book.getAuthor() + ";" + book.getYear();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, true))) {
            writer.write(csvLine);
            writer.newLine();
            System.out.println("Книга \"" + book.getName() + "\" была успешно добавлена в библиотеку.");
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
        LibraryExcp.checkLibraryNotEmpty(file);
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

    public void listBooksByAuthor(String author) throws BookException, LibraryExcp, IOException {
        try {
            LibraryExcp.checkLibraryNotEmpty(file);
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
