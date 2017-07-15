package com.dekses.jersey.docker.demo;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QueryEngine {
	public static int getCounter() {
		Statement st = null;
		int counter = -1;
		try {
			Connection conn = DBConenctionManager.getConnection();
			if (conn == null) {
				System.out.println("Unable to connect to DB");
			} else {
				String query = "SELECT * FROM tab1";
				st = conn.createStatement();
				ResultSet rs = st.executeQuery(query);
				rs.last();
				counter = rs.getInt("col1");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return counter;
	}

	public static int incrementCounter() {
		Statement st = null;
		int counter = getCounter();
		try {
			Connection conn = DBConenctionManager.getConnection();
			if (conn == null) {
				System.out.println("Unable to connect to DB");
			} else {
				String query = "INSERT into tab1(col1) values (" + ++counter + ")";
				st = conn.createStatement();
				st.execute(query);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return counter;
	}
}
