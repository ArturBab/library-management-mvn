package com.example.oop.oop_classes;

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

    public void addBook(Book book) {
        getBooks().add(book);
        System.out.println("Книга с названием: " + book.getName() + "\n" + "БЫЛА УСПЕШНО ДОБАВЛЕНА В БИБЛИОТЕКУ.");
    }

    public void removeBookByID(long id) {
        try {
            BookException.checkID(id, books);
            books.removeIf(book -> book.getId() == id); // Удаляет книгу, у которой ID совпадает
            System.out.println("КНИГА УСПЕШНО УДАЛЕНА.");
        } catch (BookException e) {
            System.out.println(e);
        }
    }

    public void listAllBooks() {
        if (getBooks().isEmpty()) {
            System.out.println("В библиотеке нет книг.");
        } else {
            System.out.println("--- СПИСОК КНИГ ---");
            for (Book book : getBooks()) {
                System.out.println(book);
            }
            System.out.println("--- ---");
        }
    }
}
