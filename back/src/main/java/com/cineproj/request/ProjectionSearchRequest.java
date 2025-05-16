package com.cineproj.request;

import java.util.List;

public class ProjectionSearchRequest {
	public String id;
	public String film;
	public String cinema;
	public String dateDebut;
	public String dateFin;
	public List<String> daysOfWeek;
	public String hour;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFilm() {
		return film;
	}
	public void setFilm(String film) {
		this.film = film;
	}
	public String getCinema() {
		return cinema;
	}
	public void setCinema(String cinema) {
		this.cinema = cinema;
	}
	public String getDateDebut() {
		return dateDebut;
	}
	public void setDateDebut(String dateDebut) {
		this.dateDebut = dateDebut;
	}
	public String getDateFin() {
		return dateFin;
	}
	public void setDateFin(String dateFin) {
		this.dateFin = dateFin;
	}
	public List<String> getDaysOfWeek() {
		return daysOfWeek;
	}
	public void setDaysOfWeek(List<String> daysOfWeek) {
		this.daysOfWeek = daysOfWeek;
	}
	public String getHours() {
		return hour;
	}
	public void setHours(String hour) {
		this.hour = hour;
	}
	
	
	
	
	
	
}
