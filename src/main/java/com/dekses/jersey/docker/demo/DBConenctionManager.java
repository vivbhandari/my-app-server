package com.dekses.jersey.docker.demo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConenctionManager {
	private static Connection con = null;

	public static Connection getConnection() {

		if (con == null) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			try {
				String mysqlHost = Main.LOCALHOST;
				if(!Main.CONTAINER.equals(Main.LOCALHOST)){
					mysqlHost = "mysql1";
				}
				con = DriverManager.getConnection("jdbc:mysql://" + mysqlHost + ":3306/test", "root", "root");
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return con;
	}

}
