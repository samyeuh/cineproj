package com.cineproj.dao;

import com.cineproj.model.Projection;
import com.cineproj.utils.Database;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import org.postgresql.util.PGobject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProjectionDAO {

	private ObjectMapper mapper = new ObjectMapper();
    public FilmDAO filmDAO = new FilmDAO();
    public CinemaDAO cinemaDAO = new CinemaDAO();

    public void insertProjection(Projection projection) throws SQLException {
        String sql = "INSERT INTO projections (film_id, cinema_id, start_date, end_date) VALUES (?, ?, ?, ?) RETURNING id";

        try(Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setObject(1, projection.getFilm().getId());
	        stmt.setObject(2, projection.getCinema().getId());
	        stmt.setDate(3, java.sql.Date.valueOf(projection.getDateDebut()));
	        stmt.setDate(4, java.sql.Date.valueOf(projection.getDateFin()));
	
	        try(ResultSet rs = stmt.executeQuery()){
		        if (rs.next()) {
		            projection.setId(UUID.fromString(rs.getString("id")));
		        }
	        }
	
	        // Insertion dans projection_schedule
	        if (projection.getRawCalendrier() != null) {
	            String scheduleSql = "INSERT INTO projection_schedule (projection_id, day_of_week, hour) VALUES (?, ?, ?)";
	            try(PreparedStatement ps = conn.prepareStatement(scheduleSql)){
	
		            for (Map.Entry<String, List<String>> entry : projection.getRawCalendrier().entrySet()) {
		                for (String time : entry.getValue()) {
		                    ps.setObject(1, projection.getId());
		                    ps.setString(2, entry.getKey().toLowerCase());
		                    LocalTime t = LocalTime.parse(time);
		                    ps.setTime(3, Time.valueOf(t));
		                    ps.addBatch();
		                }
		            }
		            ps.executeBatch();
		        }
	        }
        }
    }
    
    public void updateProjection(Projection projection) throws SQLException, JsonProcessingException {
        Connection conn = Database.getConnection();

        try(PreparedStatement filmCheckStmt = conn.prepareStatement("SELECT id FROM films WHERE id = ?")) {
        	filmCheckStmt.setObject(1, projection.getFilm().getId());
	        try(ResultSet filmCheckRs = filmCheckStmt.executeQuery()){
		        if (!filmCheckRs.next()) {
		            throw new IllegalArgumentException("Film ID does not exist in the database.");
		        }
	        }
        }

        // Vérifier si le cinema existe
        try(PreparedStatement cinemaCheckStmt = conn.prepareStatement("SELECT id FROM cinemas WHERE id = ?")){
        	cinemaCheckStmt.setObject(1, projection.getCinema().getId());
	        try(ResultSet cinemaCheckRs = cinemaCheckStmt.executeQuery()){
		        if (!cinemaCheckRs.next()) {
		            throw new IllegalArgumentException("Cinema ID does not exist in the database.");
		        }
	        }
        }

        
        String updateProjectionSql = "UPDATE projections SET film_id = ?, cinema_id = ?, start_date = ?, end_date = ? WHERE id = ?";
        try(PreparedStatement stmt = conn.prepareStatement(updateProjectionSql)){
	        stmt.setObject(1, projection.getFilm().getId());
	        stmt.setObject(2, projection.getCinema().getId());
	        stmt.setString(3, projection.getDateDebut());
	        stmt.setString(4, projection.getDateFin());
	        stmt.setObject(5, projection.getId());
	        stmt.executeUpdate();
        }

        
        try(PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM projection_schedule WHERE projection_id = ?")){
	        stmt2.setObject(1, projection.getId());
	        stmt2.executeUpdate();
        }

        
        if (projection.getCalendrier() != null && !projection.getCalendrier().isEmpty()) {
            String insertScheduleSql = "INSERT INTO projection_schedule (projection_id, day_of_week, hour) VALUES (?, ?, ?)";
            try(PreparedStatement stmt3 = conn.prepareStatement(insertScheduleSql)) {
	            for (Map.Entry<String, List<String>> entry : projection.getRawCalendrier().entrySet()) {
	                String day = entry.getKey();
	                for (String time : entry.getValue()) {
	                    if (time != null) {
	                        stmt3.setObject(1, projection.getId());
	                        stmt3.setString(2, day.toLowerCase());
	                        stmt3.setString(3, time);
	                        stmt3.addBatch();
	                    }
	                }
	            }
	            stmt3.executeBatch();
            }
        }
    }

    
    public void deleteProjection(String id) throws SQLException {
        Connection conn = Database.getConnection();
        
        try(PreparedStatement stmt1 = conn.prepareStatement("DELETE FROM projection_schedule WHERE projection_id = ?")){
        	stmt1.setObject(1, UUID.fromString(id.trim()));
            stmt1.executeUpdate();
        }
        

        
        try(PreparedStatement stmt2 = conn.prepareStatement("DELETE FROM projections WHERE id = ?")){
            stmt2.setObject(1, UUID.fromString(id.trim()));
            stmt2.executeUpdate();
        }

        
    }

    public Projection getProjectionById(String id) throws SQLException {
    	List<Projection> projections = searchProjections(id, null, null, null, null, null, null);
    	return projections.isEmpty() ? null : projections.get(0);
    }
    
    
    public List<Projection> searchProjections(String id, String film, String cinema, String dateDebut, String dateFin, List<String> daysOfWeek, String hour) throws SQLException {
    	Map<UUID, Projection> projectionMap = new LinkedHashMap<>();
    	StringBuilder sql = new StringBuilder("SELECT projections.*, projection_schedule.day_of_week, projection_schedule.hour\r\n" + 
    			"FROM projections\r\n" + 
    			"INNER JOIN projection_schedule ON projections.id = projection_schedule.projection_id WHERE 1=1");
    	
    	if (id != null && !id.trim().isEmpty()) sql.append(" AND projections.id = ?");
    	if (film != null && !film.trim().isEmpty()) sql.append(" AND projections.film_id = ?");
    	if (cinema != null && !cinema.trim().isEmpty()) sql.append(" AND projections.cinema_id = ?");
    	if (dateDebut != null && !dateDebut.trim().isEmpty()) sql.append(" AND projections.start_date = ?");
    	if (dateFin != null && !dateFin.trim().isEmpty()) sql.append(" AND projections.end_date = ?");
    	if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
    	    sql.append(" AND LOWER(projection_schedule.day_of_week) IN (");
    	    sql.append(String.join(",", Collections.nCopies(daysOfWeek.size(), "LOWER(?)")));
    	    sql.append(")");
    	}
    	if (hour != null && !hour.trim().isEmpty()) sql.append(" AND projection_schedule.hour = ?");
    	
    	try(Connection conn = Database.getConnection();
    	PreparedStatement stmt = conn.prepareStatement(sql.toString())){
    		int i = 1;
        	if (id != null && !id.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(id));
        	if (film != null && !film.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(film));
        	if (cinema != null && !cinema.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(cinema));
        	if (dateDebut != null && !dateDebut.trim().isEmpty()) stmt.setDate(i++, java.sql.Date.valueOf(dateDebut));
        	if (dateFin != null && !dateFin.trim().isEmpty()) stmt.setDate(i++, java.sql.Date.valueOf(dateFin));
        	if (daysOfWeek != null && !daysOfWeek.isEmpty()) {
        	    for (String day : daysOfWeek) {
        	        stmt.setString(i++, day);
        	    }
        	}
        	if (hour != null && !hour.trim().isEmpty()) stmt.setTime(i++, java.sql.Time.valueOf(hour));
        	
        	try(ResultSet rs = stmt.executeQuery()){
	    		while (rs.next()) {
	    	        UUID projId = UUID.fromString(rs.getString("id"));
	    	        Projection projection = projectionMap.get(projId);
	    	        if (projection == null) {
	    	            projection = new Projection();
	    	            projection.setId(projId);
	    	            projection.setFilm(filmDAO.getFilmById(rs.getString("film_id")));
	    	            projection.setCinema(cinemaDAO.getCinemaById(rs.getString("cinema_id")));
	    	            projection.setDateDebut(rs.getString("start_date"));
	    	            projection.setDateFin(rs.getString("end_date"));
	    	            projection.setCalendrier(new HashMap<>());
	    	            projectionMap.put(projId, projection);
	    	        }
	    	        
	    		    String day = rs.getString("day_of_week");
	    		    String timeStr = rs.getString("hour");
	    		    if (timeStr != null) {
	    		        projection.getRawCalendrier()
	    		                  .computeIfAbsent(day, k -> new ArrayList<>())
	    		                  .add(timeStr);
	    		   }
	    		}
        	}
    		
    		return new ArrayList<>(projectionMap.values()); 	
    	}
    }
}

