package com.example.oop;

import java.util.Scanner;

import com.example.oop.oop_classes.Book;
import com.example.oop.oop_classes.Library;

public class App {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);
        try {
            Library library = new Library();
            Book.createBook(scan);

        } catch (Exception e) {
            System.out.println(e);
        }
    }
  }

