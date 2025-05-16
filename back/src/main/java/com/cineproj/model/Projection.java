package com.cineproj.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class Projection {
	
	private UUID id;
	private Film film;
	private Cinema cinema;
	
	private String dateDebut;

	private String dateFin;
	
	private Map<String, List<String>> calendrier;
	
	public Projection() {}
	
	public Projection(UUID id, Film film, Cinema cinema, String dateDebut, String dateFin,
			Map<String, List<String>> calendrier) {
		super();
		this.id = id;
		this.film = film;
		this.cinema = cinema;
		this.dateDebut = dateDebut;
		this.dateFin = dateFin;
		this.calendrier = calendrier;
	}

	public UUID getId() {
		return id;
	}

	public void setId(UUID id) {
		this.id = id;
	}

	public Film getFilm() {
		return film;
	}

	public void setFilm(Film film) {
		this.film = film;
	}

	public Cinema getCinema() {
		return cinema;
	}

	public void setCinema(Cinema cinema) {
		this.cinema = cinema;
	}

	public String getDateDebut() {
		return dateDebut == null ? null : dateDebut.toString();
	}

	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}

	public String getDateFin() {
		return dateFin == null ? null : dateFin.toString();
	}

	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}

	public Map<String, List<String>> getCalendrier() {
	    if (calendrier == null) return null;

	    Map<String, List<String>> calendrierFormatte = new HashMap<>();

	    for (Map.Entry<String, List<String>> entry : calendrier.entrySet()) {
	        List<String> horaires = new ArrayList<>();
	        for (String time : entry.getValue()) {
	            horaires.add(time.toString().substring(0,5)); // Ex: "18:00"
	        }
	        calendrierFormatte.put(entry.getKey(), horaires);
	    }

	    return calendrierFormatte;
	}
	
	@JsonIgnore
	public Map<String, List<String>> getRawCalendrier(){
		return calendrier;
	}

	public void setCalendrier(Map<String, List<String>> calendrier) {
		this.calendrier = calendrier;
	}
	
	
	

}
