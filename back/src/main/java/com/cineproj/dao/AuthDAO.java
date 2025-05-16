package com.cineproj.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

import org.springframework.security.crypto.bcrypt.BCrypt;

import com.cineproj.model.User;
import com.cineproj.utils.Database;

public class AuthDAO {
	
	public void register(User user) throws SQLException {
		String sql = "INSERT INTO users (username, password, is_cinema, is_admin, email, created_at)" +
					"VALUES (?, ?, false, false, ?, ?) RETURNING id";
		
		try(Connection conn = Database.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql)){
		
			stmt.setString(1, user.getUsername().toLowerCase());
			stmt.setString(2, user.getPassword());
			stmt.setString(3, user.getEmail());
			stmt.setTimestamp(4, Timestamp.from(Instant.now()));
			
			
			try(ResultSet rs = stmt.executeQuery()) {
			
				if (rs.next()) {
					user.setId(UUID.fromString(rs.getString("id")));
				}
			}
		}
	}
	
	public User login(User user) throws Exception {
	    String sql = "SELECT id, password, is_admin, is_cinema FROM users WHERE username = ?";
	    
	    try(Connection conn = Database.getConnection();
	    PreparedStatement stmt = conn.prepareStatement(sql)) {
	    
	    	stmt.setString(1, user.getUsername().toLowerCase());
	    
		    try(ResultSet rs = stmt.executeQuery()) {
		    
			    if (rs.next()) {
			    	String hashedPassword = rs.getString("password");
			    	if (BCrypt.checkpw(user.getPassword(), hashedPassword)) {
				        user.setId(UUID.fromString(rs.getString("id")));
				        user.setAdmin(rs.getBoolean("is_admin"));
				        user.setCinema(rs.getBoolean("is_cinema"));
				        return user;
			    	}
			    }
		    }
	    }
	    
	    throw new Exception("Login incorrect");
	}

}
