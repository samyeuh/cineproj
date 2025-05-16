package com.cineproj.api;

import com.cineproj.dao.AuthDAO;
import com.cineproj.model.User;
import com.cineproj.request.AuthRequest;
import com.cineproj.utils.TokenService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.security.crypto.bcrypt.BCrypt;

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AuthService {
	
	public AuthDAO userDAO = new AuthDAO();

    @POST
    @Path("/login")
    public Response login(AuthRequest request) throws JsonProcessingException {
    	User userLogged;
    	try {
    		User user = new User();
    		user.setUsername(request.getUsername());
    		user.setPassword(request.getPassword());
    		userLogged = userDAO.login(user);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return Response.status(Response.Status.BAD_REQUEST)
    				.entity("{\"error\":\"" + e.getMessage() + "\"}")
    				.build();
    	}
    	
    	try {
    	
    	ObjectMapper mapper = new ObjectMapper();
    	Map<String, Object> response = new HashMap<>();
    	
    	response.put("message", "Login successful");
    	response.put("token", TokenService.generateToken(userLogged.getId().toString(), userLogged.isAdmin(), userLogged.isCinema()));
    	String json = mapper.writeValueAsString(response);
    	
    	return Response.ok().entity(json).build();
    	
    	} catch (Exception e) {
    		e.printStackTrace();
    		return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
    				.entity("{\"error\":\"" + e.getMessage() + "\"}")
    				.build();
    	}
    }

    @POST
    @Path("/register")
    public Response register(AuthRequest request) {
        try {
        	User user = new User();
        	user.setUsername(request.getUsername());
        	
        	String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        	user.setPassword(hashedPassword);
        	user.setEmail(request.getEmail());
        	
        	userDAO.register(user);
        } catch (Exception e) {
        	e.printStackTrace();
        	return Response.status(Response.Status.BAD_REQUEST)
        			.entity("{\"error\":\"" + e.getMessage() + "\"}")
	                   .build();
        }
        
        return Response.status(Response.Status.CREATED)
                       .entity("{\"message\":\"User registered\"}")
                       .build();
    }
}
