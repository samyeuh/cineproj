package com.cineproj.model;

import java.util.List;
import java.util.UUID;

public class Film {
    private UUID id;
    private String titre;
    private Integer dureeEnMinute;
    private String lang;
    private String soustitres;
    private String realisateur;
    private List<String> acteurs;
    private Integer ageMin;
    
    public Film() {}
    
	public UUID getId() {
		return id;
	}
	public void setId(UUID id) {
		this.id = id;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public Integer getDureeEnMinute() {
		return dureeEnMinute;
	}
	public void setDureeEnMinute(Integer dureeEnMinute) {
		this.dureeEnMinute = dureeEnMinute;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getSoustitres() {
		return soustitres;
	}
	public void setSoustitres(String soustitres) {
		this.soustitres = soustitres;
	}
	public String getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}
	public List<String> getActeurs() {
		return acteurs;
	}
	public void setActeurs(List<String> acteurs) {
		this.acteurs = acteurs;
	}
	public Integer getAgeMin() {
		return ageMin;
	}
	public void setAgeMin(Integer ageMin) {
		this.ageMin = ageMin;
	}

}