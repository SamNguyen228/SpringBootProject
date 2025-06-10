/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.example.ecommerce.service;

/**
 *
 * @author fptshop
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class TestConnection {
    public static void main(String[] args) {
        String jdbcURL = "jdbc:sqlserver://localhost:1433;databaseName=Website;instanceName=SQLEXPRESS;encrypt=true;trustServerCertificate=true";
        String username = "sa";
        String password = "SamIT6";

        try {
            // Load driver
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");

            // Create connection
            Connection connection = DriverManager.getConnection(jdbcURL, username, password);

            System.out.println("Connect Successful!");
            connection.close();
        } catch (ClassNotFoundException e) {
            System.err.println("Do not find driver for JDBC SQL Server.");
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("Error to connect: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
