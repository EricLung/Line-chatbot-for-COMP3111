package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		Connection con=getConnection();
		String result = null;
		try {
			PreparedStatement stmt = con.prepareStatement(
			"SELECT * FROM testdb");
			ResultSet rs = stmt.executeQuery();
			while (result == null && rs.next()) {
				if (text.toLowerCase().contains(rs.getString(1).toLowerCase())) {
					result = rs.getString(2);
				}
			}
			rs.close();
			stmt.close();
		} catch (Exception e) {
			log.info("Exception while reading db", e.toString());
		} finally {
			try {
				if(con != null)
					con.close();
			} catch (Exception ex) {
				log.info("Exception while closing connection", ex.toString());
			}
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
    }
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
