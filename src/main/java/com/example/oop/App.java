package com.example.oop;

import java.io.IOException;
import java.util.Scanner;

import com.example.oop.oop_classes.Book;
import com.example.oop.oop_classes.Library;
import com.example.oop.oop_classes.Book.BookException;
import com.example.oop.oop_classes.Library.LibraryExcp;

public class App {
    static Scanner scanner = new Scanner(System.in, "CP1251");

    public static void main(String[] args) throws IOException, LibraryExcp, BookException {
        Library library = new Library();
        library.createLibrary();

        while (true) {
            clearConsole();
            System.out.println(" **МЕНЮ БИБЛИОТЕКИ**:");
            System.out.println("1 Добавить книгу");
            System.out.println("2️ Удалить книгу");
            System.out.println("3️ Обновить книгу");
            System.out.println("4️ Показать все книги");
            System.out.println("0️ Выйти");

            System.out.print("Выберите действие: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    System.out.print("Введите название книги: ");
                    String name = scanner.nextLine();

                    System.out.print("Введите автора: ");
                    String author = scanner.nextLine();

                    System.out.print("Введите год издания: ");
                    int year;
                    try {
                        year = Integer.parseInt(scanner.nextLine());
                        Book.BookException.validateYear(year);
                    } catch (NumberFormatException | Book.BookException e) {
                        System.out.println(" Ошибка: Некорректный ввод года.");
                        continue;
                    }

                    Book book = new Book(name, author, year);
                    library.addBook(book);
                    break;

                case "2":
                    System.out.print("Введите ID книги для удаления: ");
                    try {
                        long idToRemove = Long.parseLong(scanner.nextLine());
                        library.removeBookByID(idToRemove);
                    } catch (NumberFormatException e) {
                        System.out.println(" Ошибка: Некорректный ID.");
                    }
                    break;

                case "3":
                    System.out.print("Введите ID книги для обновления: ");
                    try {
                        long idToUpdate = Long.parseLong(scanner.nextLine());
                        library.updateBook(idToUpdate);
                    } catch (NumberFormatException e) {
                        System.out.println(" Ошибка: Некорректный ID.");
                    }
                    break;

                case "4":
                    library.listAllBooks();
                    break;

                case "0":
                    System.out.println(" Работа завершена. До встречи!");
                    scanner.close();
                    return;

                default:
                    System.out.println(" Ошибка: Некорректный выбор. Попробуйте снова.");
            }
            System.out.println("\nНажмите Enter, чтобы продолжить...");
            scanner.nextLine(); // Только один `nextLine()`
        }
    }

    public static void clearConsole() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (IOException | InterruptedException ex) {
            System.out.println(" Не удалось очистить консоль");
        }
    }
}