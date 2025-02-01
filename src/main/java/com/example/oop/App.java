package com.example.oop;


import com.example.oop.oop_classes.Book;
import com.example.oop.oop_classes.Library;

public class App {
    public static void main(String[] args) {
        try {
            Library library = new Library();
            library.createLibrary();
            Book book = new Book("1", "2", 2025);
            library.addBook(book);
            library.listAllBooks();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
  }

