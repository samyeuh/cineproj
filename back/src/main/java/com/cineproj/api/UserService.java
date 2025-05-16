package com.cineproj.api;

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

import com.cineproj.dao.UserDAO;
import com.cineproj.model.User;
import com.cineproj.request.UserRequest;
import com.cineproj.utils.TokenService;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/users")
@Produces("application/json")
@Consumes("application/json")
public class UserService {
	
	public UserDAO userDAO = new UserDAO();
	
	@PUT
	@Path("/{id}")
	public Response editUser(@PathParam("id") String id, User user, @HeaderParam("Authorization") String authHeader) {
		try {
			
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			String userIdFromToken = TokenService.getUserId(token);
			boolean isAdmin = TokenService.verifyTokenIsAdmin(token, false);

			if (!isAdmin && !userIdFromToken.equals(id)) {
			    return Response.status(Response.Status.FORBIDDEN)
			                   .entity("{\"error\":\"Unauthorized to edit this user\"}")
			                   .build();
			}
			
			User existingUser = userDAO.getUserById(id);
			if (existingUser == null) {
	            return Response.status(Response.Status.NOT_FOUND)
	                           .entity("{\"error\":\"User not found\"}")
	                           .build();
	        }
	
			if (user.getUsername() != null) existingUser.setUsername(user.getUsername());
			if (user.getPassword() != null) existingUser.setPassword(user.getPassword());
			if (user.isCinema() != null) existingUser.setCinema(user.isCinema());
			if (user.isAdmin() != null) existingUser.setAdmin(user.isAdmin());
			if (user.getEmail() != null) existingUser.setEmail(user.getEmail());
			if (user.getCreated_at() != null) existingUser.setCreated_at(user.getCreated_at());
			
			userDAO.updateUser(existingUser);
			
			return Response.ok()
						.entity(existingUser)
						.build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\""+e.getMessage()+"\"}")
					.build();
		}
	}
	
	@DELETE
	@Path("/{id}")
	public Response deleteUser(@PathParam("id") String id, @HeaderParam("Authorization") String authHeader) {
		try {
			
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			String userIdFromToken = TokenService.getUserId(token);
			boolean isAdmin = TokenService.verifyTokenIsAdmin(token, false);

			if (!isAdmin && !userIdFromToken.equals(id)) {
			    return Response.status(Response.Status.FORBIDDEN)
			                   .entity("{\"error\":\"Unauthorized to edit this user\"}")
			                   .build();
			}
			
			User user = userDAO.getUserById(id);
			if (user == null) {
				return Response.status(Response.Status.NOT_FOUND)
                        .entity("{\"error\":\"Film not found\"}")
                        .build();
			}
			
			userDAO.deleteUser(id);
			
			return Response.ok()
					.entity("{\"message\":\"User deleted successfully\"}")
					.build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\""+e.getMessage()+"\"}")
					.build();
		}
	}
	
	@GET
	@Path("/{id}")
	public Response getUserById(@PathParam("id") String id) {
		try {
			User user = userDAO.getUserById(id);
			
			if (user == null) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"User not found\"}")
						.build();
			}
			
			return Response.ok().entity(user).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"" + e.getMessage() + "\"}")
					.build();
		}
	}
	
	@POST
	public Response searchUsers(UserRequest request) {
		try {
			List<User> users = userDAO.searchUser(
					request.getId(), 
					request.getUsername(), 
					request.isCinema(), 
					request.isAdmin(), 
					request.getEmail(), 
					request.getCreatedBefore(), 
					request.getCreatedAfter()
				);
			
			if (users.isEmpty()) {
				return Response.status(Response.Status.NOT_FOUND)
						.entity("{\"error\":\"No user found\"}")
						.build();
			}
			
			ObjectMapper mapper = new ObjectMapper();
			Map<String, Object> response = new HashMap<>();
			
			response.put("count", users.size());
			response.put("results", users);
			String json = mapper.writeValueAsString(response);
			
			return Response.ok().entity(json).build();
			
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"" + e.getMessage() + "\"}")
					.build();
		}
	}
	
	@GET
	@Path("/me")
	public Response getMyUser(@HeaderParam("Authorization") String authHeader) {
		try {
	        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	            return Response.status(Response.Status.UNAUTHORIZED)
	                    .entity("{\"error\":\"Authorization header missing\"}")
	                    .build();
	        }
	        
			String token = authHeader.substring("Bearer".length()).trim();
			String userIdFromToken = TokenService.getUserId(token);
			
			User user = userDAO.getUserById(userIdFromToken);
			if (user == null) {
			    return Response.status(Response.Status.NOT_FOUND)
			                   .entity("{\"error\":\"Utilisateur introuvable\"}")
			                   .build();
			}
			
			return Response.ok().entity(user).build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.status(Response.Status.BAD_REQUEST)
					.entity("{\"error\":\"" + e.getMessage() + "\"}")
					.build();
		}
	}

}
