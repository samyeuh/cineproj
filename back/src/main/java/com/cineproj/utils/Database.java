package com.cineproj.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class Database {
    
    private static String URL;

    static {
        try {
            Dotenv dotenv = Dotenv.configure()
                                  .ignoreIfMissing()
                                  .load();

            URL = System.getenv("DB_URL");
            if (URL == null || URL.isEmpty()) {
                URL = dotenv.get("DB_URL");
            }

            if (URL == null || URL.isEmpty()) {
                throw new IllegalStateException("DB_URL n'est défini ni dans l'env système ni dans .env");
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Erreur lors de l'initialisation de l'URL DB", e);
        }
    }

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }
}