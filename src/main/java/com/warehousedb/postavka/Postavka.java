package com.warehousedb.postavka; 

import java.sql.DriverManager; 
import java.sql.Connection; 
import java.sql.ResultSet; 
import java.sql.Statement; 

public class Postavka { 
 public static void main(String[] args) { 
  Postavka m = new Postavka(); 
  m.testDB_operation(); 
 } 
private void testDB_operation() { 
 try {  
  Class.forName("org.postgresql.Driver"); 
  System.out.println("Драйвер подключен");             
  String url = "jdbc:postgresql://localhost:5432/mainDB"; 
  String login = "postgres"; 
  String password = "admin"; 
  Connection con = DriverManager.getConnection(url,  
                   login, password); 
  System.out.println("Соединение установлено"); 
   try { 
    System.out.println("                          "); 
    System.out.println("===== Раздел Statement ====="); 
    System.out.println("----------------------------"); 
    Statement stmt = con.createStatement(); 
    //=============== Операция select =============== 
    System.out.println("                          "); 
    System.out.println("Операция выборки"); 
    // Шаг 1. Формирование строки запроса select  
    String sel =  
     "SELECT * FROM \"public\".\"Поставки\" "; 
    // Шаг 2. Выполнение запроса select  
    ResultSet rs = stmt.executeQuery(sel); 
    // Шаг 3. Считывание записей переменной rsSel 
    System.out.println("Вывод результатов выборки"); 
    while (rs.next()) { 
     //Получаем содержимое 4-х полей текущей записи 
     String str = rs.getString("IDпоставки") +  
      " IDпоставщика - " + rs.getString(2) +   
      " IDн - " + rs.getString(3) +  
      " Количество - " + rs.getString(4) +  
      " Дата - " + rs.getString(5) +  
      " Цена - " + rs.getString(6); 
     System.out.println("Номер поставки: " + str); 
    } 
    // Шаг 4. Закрытие переменных 
    rs.close(); 
    stmt.close(); 
   } finally { con.close(); } 
  } catch (Exception e) { e.printStackTrace(); } 
}} 
