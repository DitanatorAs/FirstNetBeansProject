package com.warehousedb.postavka;

import java.io.UnsupportedEncodingException;
import java.sql.CallableStatement;
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.Scanner;

public class Postavka {
    
    // Объявляем переменных для авторизации в psql
    private Connection con;
    private String url;
    private String login;
    private String password;
    
    public static void main(String[] args) throws UnsupportedEncodingException  {
        Postavka m = new Postavka();
         // Установка кодировки
        System.setOut(new java.io.PrintStream(System.out, true, "UTF-8"));
        
        // Устанавливаем соединение с БД
        if (m.connectionDB()) {
            // Выполняем основной цикл программы
            m.mainMenu();
            
            // Закрываем соединение при завершении
            m.closeConnection();
        } else {
            System.out.println("Не удалось подключиться к базе данных!");
        }
    }
    
    // Метод для подключения к базе данных
    private boolean connectionDB(){
        try {
           
            
            // Загрузка драйвера
            Class.forName("org.postgresql.Driver");
            System.out.println("Драйвер подключен");
            
            // Параметры подключения
            url = "jdbc:postgresql://localhost:5432/mainDB?useUnicode=true&characterEncoding=UTF-8";
            login = "postgres";
            password = "admin";
            
            // Устанавливаем соединение
            con = DriverManager.getConnection(url, login, password);
            System.out.println("Соединение установлено");
            System.out.println("================================");
            
            return true;
            
        } catch (ClassNotFoundException e) {
            System.err.println("Драйвер PostgreSQL не найден!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.err.println("Ошибка подключения к базе данных!");
            e.printStackTrace();
            return false;
        }
    }
    
    // Метод для закрытия соединения
    private void closeConnection() {
        try {
            if (con != null && !con.isClosed()) {
                con.close();
                System.out.println("Соединение с базой данных закрыто");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    // Главное меню программы
    private void mainMenu() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            // Выполняем выборку данных
            selectSup();
            
            System.out.println("\n================================");
            System.out.print("Хотите добавить новую поставку? (Y/n): ");
            
            String choice = scanner.nextLine().trim().toUpperCase();
            
            if (choice.equals("Y")) {
                // Очищаем консоль
                clearTerminal();
                
                // Запрашиваем данные у пользователя
                System.out.println("=== Добавление новой поставки ===\n");
                
                try {
                    System.out.print("Введите ID поставщика: ");
                    int idPostavshchika = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Введите ID номенклатуры: ");
                    int idNomenclature = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Введите количество: ");
                    int kolichestvo = Integer.parseInt(scanner.nextLine());
                    
                    System.out.print("Введите цену: ");
                    int cena = Integer.parseInt(scanner.nextLine());
                    
                    // Вызываем процедуру добавления поставки
                    callDeliveryProcedure(idPostavshchika, idNomenclature, kolichestvo, cena);
                    
                    System.out.println("\nНажмите Enter для продолжения...");
                    scanner.nextLine();
                    
                    // Очищаем консоль перед следующим циклом
                    clearTerminal();
                    
                } catch (NumberFormatException e) {
                    System.err.println("Ошибка: введите корректные числовые значения!");
                    System.out.println("\nНажмите Enter для продолжения...");
                    scanner.nextLine();
                    clearTerminal();
                }
                
            } else if (choice.equals("N")) {
                System.out.println("Программа завершена. До свидания!");
                running = false;
            } else {
                System.out.println("Неверный ввод. Пожалуйста, введите Y или n");
                System.out.println("\nНажмите Enter для продолжения...");
                scanner.nextLine();
                clearTerminal();
            }
        }
        
        scanner.close();
    }
    
    // Метод для очистки терминала
    private void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } 
        } catch (Exception e) {
            // Если очистка не удалась, просто выводим много пустых строк
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
    
    // Метод для запроса данных из таблицы Поставки
    private void selectSup() {
        Statement stmt = null;
        ResultSet rs = null;
        
        try {
            System.out.println("===== Список поставок =====");
            System.out.println("----------P------------------");
            
            stmt = con.createStatement();
            
            // Формирование строки запроса select
            String sel = "SELECT * FROM \"public\".\"Поставки\" ORDER BY \"IDпоставки\"";
            
            // Выполнение запроса select
            rs = stmt.executeQuery(sel);
            
            // Проверяем, есть ли данные
            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                // Получаем содержимое полей текущей записи
                String idPostavki = rs.getString("IDпоставки");
                String idPostavshchika = rs.getString(2);
                String idNomenclature = rs.getString(3);
                String kolichestvo = rs.getString(4);
                String data = rs.getString(5);
                String cena = rs.getString(6);
                
                System.out.println("┌─────────────────────────────────────────┐");
                System.out.printf("│ ID поставки: %-30s│\n", idPostavki);
                System.out.printf("│ ID поставщика: %-27s│\n", idPostavshchika);
                System.out.printf("│ ID номенклатуры: %-26s│\n", idNomenclature);
                System.out.printf("│ Количество: %-30s│\n", kolichestvo);
                System.out.printf("│ Дата: %-36s│\n", data);
                System.out.printf("│ Цена: %-35s│\n", cena);
                System.out.println("└─────────────────────────────────────────┘");
                System.out.println();
            }
            
            if (!hasData) {
                System.out.println("В таблице 'Поставки' нет данных.");
            }
            
        } catch (SQLException e) {
            System.err.println("Ошибка при выполнении запроса SELECT:");
            e.printStackTrace();
        } finally {
            // Закрываем ресурсы в обратном порядке
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    
    // Метод для вызова процедуры delivery
    private void callDeliveryProcedure(int idPost, int idN, 
                                       int quantity,  int price) {
        CallableStatement cstmt = null;
        
        try {
            // Проверяем, не закрыто ли соединение
            if (con == null || con.isClosed()) {
                System.err.println("Соединение с базой данных отсутствует!");
                return;
            }
            
            // Формируем вызов процедуры с позиционными параметрами
             String sql = "call delivery(?, ?, ?, ?, ?, ?, ?)";
            
         cstmt = con.prepareCall(sql);
        
          // Устанавливаем входные параметры 
        cstmt.setInt(1, idPost);    // p_id_postavshchika
        cstmt.setInt(2, idN);      // p_id_nomenclature
        cstmt.setInt(3, quantity);         // p_kolichestvo
        cstmt.setDate(4, java.sql.Date.valueOf(java.time.LocalDate.now())); // p_data 
        cstmt.setInt(5, (int) price);          // p_cena (INT)
   
        cstmt.registerOutParameter(6, Types.INTEGER);  // p_id_postavki
        cstmt.registerOutParameter(7, Types.VARCHAR);  // p_message
        
            
            // Выполняем процедуру
            cstmt.execute();
            
            System.out.println("\n Процедура delivery выполнена успешно!");
            System.out.printf("Добавлена поставка: Поставщик ID=%d, Товар ID=%d, Количество=%d, Цена=%d\n", 
                            idPost, idN, quantity, price);
            
        } catch (SQLException e) {
            System.err.println("\n❌ Ошибка при вызове процедуры delivery:");
            System.err.println("Код ошибки: " + e.getErrorCode());
            System.err.println("Сообщение: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Закрываем CallableStatement
            try {
                if (cstmt != null) cstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}