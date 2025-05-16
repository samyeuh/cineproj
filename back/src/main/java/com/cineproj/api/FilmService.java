package com.cineproj.api;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import com.cineproj.dao.FilmDAO;
import com.cineproj.model.Film;
import com.cineproj.request.FilmSearchRequest;
import com.cineproj.utils.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/films")
@Produces("application/json")
@Consumes("application/json")
public class FilmService {
	
	private FilmDAO filmDAO = new FilmDAO();

	@POST
	@Path("/add")
	public Response addFilm(Film film, @HeaderParam("Authorization") String authHeader) {
		try {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			boolean isAdmin = TokenService.verifyTokenIsAdmin(token, false);

			if (!isAdmin) {
			    return Response.status(Response.Status.FORBIDDEN)
			                   .entity("{\"error\":\"Unauthorized to edit this user\"}")
			                   .build();
			}
			
			
			filmDAO.insertFilm(film);
			return Response.status(Response.Status.CREATED).entity(film).build();
		} catch (Exception e) {
		    e.printStackTrace();
		    return Response.status(Response.Status.BAD_REQUEST)
		                   .entity("{\"error\":\"" + e.getMessage() + "\"}")
		                   .build();
		}
	    
	    
	}
	
	@PUT
	@Path("/{id}")
	public Response editFilm(@PathParam("id") String id, Film film, @HeaderParam("Authorization") String authHeader) {
	    try {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			boolean isAdmin = TokenService.verifyTokenIsAdmin(token, false);

			if (!isAdmin) {
			    return Response.status(Response.Status.FORBIDDEN)
			                   .entity("{\"error\":\"Unauthorized to edit this user\"}")
			                   .build();
			}
	    	
	    	
	        Film existingFilm = filmDAO.getFilmById(id);
	        if (existingFilm == null) {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("{\"error\":\"Film not found\"}")
	                           .build();
	        }

	        if (film.getTitre() != null) existingFilm.setTitre(film.getTitre());
	        if (film.getDureeEnMinute() != null) existingFilm.setDureeEnMinute(film.getDureeEnMinute());
	        if (film.getLang() != null) existingFilm.setLang(film.getLang());
	        if (film.getSoustitres() != null) existingFilm.setSoustitres(film.getSoustitres());
	        if (film.getRealisateur() != null) existingFilm.setRealisateur(film.getRealisateur());
	        if (film.getActeurs() != null) existingFilm.setActeurs(film.getActeurs());
	        if (film.getAgeMin() != null) existingFilm.setAgeMin(film.getAgeMin());

	        filmDAO.updateFilm(existingFilm);

	        return Response.ok()
	                       .entity(existingFilm)
	                       .build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.BAD_REQUEST)
	                       .entity("{\"error\":\"" + e.getMessage() + "\"}")
	                       .build();
	    }
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteFilm(@PathParam("id") String id, @HeaderParam("Authorization") String authHeader) {
	    try {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			boolean isAdmin = TokenService.verifyTokenIsAdmin(token, false);

			if (!isAdmin) {
			    return Response.status(Response.Status.FORBIDDEN)
			                   .entity("{\"error\":\"Unauthorized to edit this user\"}")
			                   .build();
			}
	    	
	        Film film = filmDAO.getFilmById(id);
	        if (film == null) {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("{\"error\":\"Film not found\"}")
	                           .build();
	        }

	        filmDAO.deleteFilm(id);

	        return Response.ok()
	                       .entity("{\"message\":\"Film deleted successfully\"}")
	                       .build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.BAD_REQUEST)
	                       .entity("{\"error\":\"" + e.getMessage() + "\"}")
	                       .build();
	    }
	}


	
	@GET
	@Path("/{id}")
	public Response getFilmById(@PathParam("id") String filmId) throws SQLException {
	    try {
	        Film film = filmDAO.getFilmById(filmId);
	        
	        if (film == null) {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("{\"error\":\"Film not found\"}")
	                           .build();
	        }

	        return Response.ok().entity(film).build();
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.BAD_REQUEST)
	                       .entity("{\"error\":\"" + e.getMessage() + "\"}")
	                       .build();
	    }
	}
	
	@POST
	public Response searchFilms(FilmSearchRequest request) {
	    try {
	        List<Film> films = filmDAO.searchFilms(
	            request.id,
	            request.titre,
	            request.lang,
	            request.realisateur,
	            request.ageMin,
	            request.ageMax,
	            request.soustitres,
	            request.dureeMin,
	            request.dureeMax
	        );
	        if (films.isEmpty()) {
		    	return Response.status(Response.Status.NOT_FOUND)
		                   .entity("{\"error\":\"No film found\"}")
		                   .build();
	        }
	        
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> response = new HashMap<>();
			
			response.put("count", films.size());
			response.put("results", films);
			String json = mapper.writeValueAsString(response);

			return Response.ok()
			    .entity(json)
			    .build();
			
	    } catch (Exception e) {
	        e.printStackTrace();
	        return Response.status(Response.Status.BAD_REQUEST)
	                       .entity("{\"error\":\"" + e.getMessage() + "\"}")
	                       .build();
	    }
	}


}
