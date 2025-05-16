package com.cineproj.dao;

import com.cineproj.model.Film;
import com.cineproj.utils.Database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class FilmDAO {

    public void insertFilm(Film film) throws SQLException {
        String sql = "INSERT INTO films (titre, duree_en_minute, lang, soustitres, realisateur, acteurs, age_min) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?) RETURNING id";

        try(Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

	        stmt.setString(1, film.getTitre());
	        stmt.setInt(2, film.getDureeEnMinute());
	        stmt.setString(3, film.getLang());
	        stmt.setString(4, film.getSoustitres());
	        stmt.setString(5, film.getRealisateur());
	        stmt.setString(6, String.join(",", film.getActeurs()));
	        stmt.setInt(7, film.getAgeMin());
	
	        try(ResultSet rs = stmt.executeQuery()) {
		        if (rs.next()) {
		            UUID generatedId = UUID.fromString(rs.getString("id"));
		            film.setId(generatedId);
		        }
	        }
        }
    }
    
    public void updateFilm(Film film) throws SQLException {
        String sql = "UPDATE films SET titre = ?, duree_en_minute = ?, lang = ?, soustitres = ?, realisateur = ?, acteurs = ?, age_min = ? WHERE id = ?";

		try(Connection conn = Database.getConnection();
	    PreparedStatement stmt = conn.prepareStatement(sql)) {
		
		    stmt.setString(1, film.getTitre());
		    stmt.setInt(2, film.getDureeEnMinute());
		    stmt.setString(3, film.getLang());
		    stmt.setString(4, film.getSoustitres());
		    stmt.setString(5, film.getRealisateur());
		    stmt.setString(6, String.join(",", film.getActeurs()));
			stmt.setInt(7, film.getAgeMin());
			stmt.setObject(8, film.getId());
			
			stmt.executeUpdate();
		}
    }
    
    public void deleteFilm(String id) throws SQLException {
        String sql = "DELETE FROM films WHERE id = ?";

        try(Connection conn = Database.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql)) {

        	stmt.setObject(1, UUID.fromString(id.trim()));
        	stmt.executeUpdate();
        }
    }


    
    public Film getFilmById(String id) throws SQLException {
		List<Film> films = searchFilms(id, null, null, null, null, null, null, null, null);
		return films.get(0);
    }
    
	public List<Film> searchFilms(String id, String titre, String lang, String realisateur, Integer ageMin, Integer ageMax, String soustitres, Integer dureeMin, Integer dureeMax) throws SQLException {
	    List<Film> films = new ArrayList<>();
	    StringBuilder sql = new StringBuilder("SELECT * FROM films WHERE 1=1");
	    
	    if (id != null && !id.trim().isEmpty()) sql.append(" AND id = ?");
	    if (titre != null && !titre.trim().isEmpty()) sql.append(" AND LOWER(titre) LIKE LOWER(?)");
	    if (lang != null && !lang.trim().isEmpty()) sql.append(" AND LOWER(lang) = LOWER(?)");
	    if (realisateur != null && !realisateur.trim().isEmpty()) sql.append(" AND LOWER(realisateur) LIKE LOWER(?)");
	    if (ageMin != null) sql.append(" AND age_min >= ?");
	    if (ageMax != null) sql.append(" AND age_min <= ?");
	    if (soustitres != null && !soustitres.trim().isEmpty()) sql.append(" AND LOWER(soustitres) = LOWER(?)");
	    if (dureeMin != null) sql.append(" AND duree_en_minute >= ?");
	    if (dureeMax != null) sql.append(" AND duree_en_minute <= ?");

	    try(Connection conn = Database.getConnection();
	    PreparedStatement stmt = conn.prepareStatement(sql.toString())) {

		    int i = 1;
		    if (id != null && !id.trim().isEmpty()) stmt.setObject(i++, UUID.fromString(id.trim()));
		    if (titre != null && !titre.trim().isEmpty()) stmt.setString(i++, "%" + titre.trim() + "%");
		    if (lang != null && !lang.trim().isEmpty()) stmt.setString(i++, lang);
		    if (realisateur != null && !realisateur.trim().isEmpty()) stmt.setString(i++, "%" + realisateur + "%");
		    if (ageMin != null) stmt.setInt(i++, ageMin);
		    if (ageMax != null) stmt.setInt(i++, ageMax);
		    if (soustitres != null && !soustitres.trim().isEmpty()) stmt.setString(i++, soustitres);
		    if (dureeMin != null) stmt.setInt(i++, dureeMin);
		    if (dureeMax != null) stmt.setInt(i++, dureeMax);
	
		    try(ResultSet rs = stmt.executeQuery()) {
			    while (rs.next()) {
			        Film film = new Film();
			        film.setId(UUID.fromString(rs.getString("id")));
			        film.setTitre(rs.getString("titre"));
			        film.setDureeEnMinute(rs.getInt("duree_en_minute"));
			        film.setLang(rs.getString("lang"));
			        film.setSoustitres(rs.getString("soustitres"));
			        film.setRealisateur(rs.getString("realisateur"));
			        String acteursStr = rs.getString("acteurs");
			        film.setActeurs(Arrays.asList(acteursStr.split(",")));
			        film.setAgeMin(rs.getInt("age_min"));
		
			        films.add(film);
			    }
			    	
			    return films;
			}
	    }
	}
}
