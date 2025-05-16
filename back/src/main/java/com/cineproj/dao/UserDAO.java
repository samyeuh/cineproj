package com.cineproj.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.cineproj.model.User;
import com.cineproj.utils.Database;

public class UserDAO {
	
	public void updateUser(User user) throws SQLException {
		String sql = "UPDATE users SET username = ?, password = ?, is_cinema = ?, is_admin = ?, email = ?, created_at = ? WHERE id = ?";
		
		try(Connection conn = Database.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql)){
		
		    String password = user.getPassword();
		    if (password == null) {
		      
		        try(PreparedStatement selectStmt = conn.prepareStatement("SELECT password FROM users WHERE id = ?")){
		        	selectStmt.setObject(1, user.getId());
			        try(ResultSet rs = selectStmt.executeQuery()){
				        if (rs.next()) {
				            password = rs.getString("password");
				        }
			        }
		        }
		    }
			
			stmt.setString(1, user.getUsername());
			stmt.setString(2, password);
			stmt.setBoolean(3, user.isCinema());
			stmt.setBoolean(4, user.isAdmin());
			stmt.setString(5, user.getEmail());
	
			if (user.getCreated_at() != null) {
				String createdAtFixed = user.getCreated_at().replace(' ', 'T');
		        LocalDateTime ldt = LocalDateTime.parse(createdAtFixed);
		        stmt.setTimestamp(6, Timestamp.valueOf(ldt));
		    } else {
		        stmt.setTimestamp(6, null); 
		    }
			
			stmt.setObject(7, user.getId());
			stmt.executeUpdate();
		}
	}
	
	public void deleteUser(String id) throws SQLException {
		String sql = "DELETE FROM users WHERE id = ?";
		
		try(Connection conn = Database.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql)){
			stmt.setObject(1, UUID.fromString(id.trim()));
			stmt.executeUpdate();
		}
		
		
	}
	
	public User getUserById(String id) throws SQLException {
		List<User> users = searchUser(id, null, null, null, null, null, null);
		return users.isEmpty() ? null : users.get(0); 
	}
	
	public List<User> searchUser(String id, String username, Boolean isCinema, Boolean isAdmin, String email, String created_before, String created_after) throws SQLException {
		List<User> users = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
		
		if (id != null && !id.trim().isEmpty()) sql.append(" AND id = ?");
		if (username != null && !username.trim().isEmpty()) sql.append(" AND username = ?");
		if (isCinema != null) sql.append(" AND is_cinema = ?");
		if (isAdmin != null) sql.append(" AND is_admin = ?");
		if (email != null && !email.trim().isEmpty()) sql.append(" AND email = ?");
		if (created_before != null && !created_before.trim().isEmpty()) sql.append(" AND created_date < ?");
		if (created_after != null && !created_after.trim().isEmpty()) sql.append(" AND created_after > ?");
		
		try(Connection conn = Database.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql.toString())){
			int i = 1;
			if (id != null && !id.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(id.trim()));
			if (username != null && !username.trim().isEmpty()) stmt.setString(i++, username.trim());
			if (isCinema != null) stmt.setBoolean(i++, isCinema);
			if (isAdmin != null) stmt.setBoolean(i++, isAdmin);
			if (email != null && !email.trim().isEmpty()) stmt.setString(i++, email.trim());
			if (created_before != null && !created_before.trim().isEmpty()) stmt.setString(i++, created_before.trim());
			if (created_after != null && !created_after.trim().isEmpty()) stmt.setString(i++, created_after.trim());
			
			ResultSet rs = stmt.executeQuery();
			while (rs.next()) {
				User user = new User();
				user.setId(UUID.fromString(rs.getString("id")));
				user.setUsername(rs.getString("username"));
				user.setCinema(rs.getBoolean("is_cinema"));
				user.setAdmin(rs.getBoolean("is_admin"));
				user.setEmail(rs.getString("email"));
				user.setCreated_at(rs.getString("created_at"));
				
				users.add(user);
			}
			
			return users;
		}
	}
}
