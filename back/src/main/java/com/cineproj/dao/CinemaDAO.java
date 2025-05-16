package com.cineproj.dao;

import com.cineproj.model.Cinema;
import com.cineproj.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CinemaDAO {

    public void insertCinema(Cinema cinema) throws SQLException {
        String sql = "INSERT INTO cinemas (name, address, city, owner_id) VALUES (?, ?, ?, ?) RETURNING id";

        try(Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setString(1, cinema.getName());
	        stmt.setString(2, cinema.getAddress());
	        stmt.setString(3, cinema.getCity());
	        stmt.setObject(4, cinema.getOwner_id());

        	try(ResultSet rs = stmt.executeQuery()) {
        		if (rs.next()) {
        			cinema.setId(UUID.fromString(rs.getString("id")));
        		}
        	}
        }
    }
    
    public void updateCinema(Cinema cinema) throws SQLException {
        String sql = "UPDATE cinemas SET name = ?, address = ?, city = ?, owner_id = ? WHERE id = ?";

        try(Connection conn = Database.getConnection();
	    PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, cinema.getName());
	        stmt.setString(2, cinema.getAddress());
	        stmt.setString(3, cinema.getCity());
	        stmt.setObject(4, cinema.getOwner_id());
	        stmt.setObject(5, cinema.getId());
	
	        stmt.executeUpdate();
        }
    }

    public void deleteCinema(String id) throws SQLException {
        String sql = "DELETE FROM cinemas WHERE id = ?";

        try(Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)){

	        stmt.setObject(1, UUID.fromString(id.trim()));
	        stmt.executeUpdate();
        }
    }

    
    public Cinema getCinemaById(String id) throws SQLException {
    	List<Cinema> cinemas = searchCinemas(id, null, null, null, null);
    	return cinemas.get(0);
    }

	public List<Cinema> searchCinemas(String id, String name, String address, String city, String owner_id) throws SQLException {
		List<Cinema> cinemas = new ArrayList<>();
		StringBuilder sql = new StringBuilder("SELECT * FROM cinemas WHERE 1=1");
		
		if (id != null && !id.trim().isEmpty()) sql.append(" AND id = ?");
		if (name != null && !name.trim().isEmpty()) sql.append(" AND LOWER(name) LIKE LOWER(?)");
		if (address != null && !address.trim().isEmpty()) sql.append( " AND LOWER(address) LIKE LOWER(?)");
		if (city != null && !city.trim().isEmpty()) sql.append(" AND LOWER(city) LIKE LOWER(?)");
		if (owner_id != null && !owner_id.trim().isEmpty()) sql.append(" AND owner_id = ?");
		
		try (Connection conn = Database.getConnection();
		PreparedStatement stmt = conn.prepareStatement(sql.toString())) {
		
		int i = 1;
		if (id != null && !id.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(id.trim()));
		if (name != null && !name.trim().isEmpty()) stmt.setString(i++, "%" + name.trim() + "%");
		if (address != null && !address.trim().isEmpty()) stmt.setString(i++, "%" + address.trim() + "%");
		if (city != null && !city.trim().isEmpty()) stmt.setString(i++, city);
		if (owner_id != null && !owner_id.trim().isEmpty()) {
			stmt.setObject(i++, UUID.fromString(owner_id.trim()));
		}

		try(ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				System.out.println(rs);
				Cinema cinema = new Cinema();
				cinema.setId(UUID.fromString(rs.getString("id")));
				cinema.setName(rs.getString("name"));
				cinema.setAddress(rs.getString("address"));
				cinema.setCity(rs.getString("city"));
				cinema.setOwner_id(UUID.fromString(rs.getString("owner_id")));
				
				cinemas.add(cinema);
			}
		}
		
		return cinemas;
	}
	}
}
