package com.cineproj.request;

public class FilmSearchRequest {
	public String id;
	public String titre;
	public String lang;
	public String realisateur;
	public Integer ageMin;
	public Integer ageMax;
	public String soustitres;
	public Integer dureeMin;
	public Integer dureeMax;
	

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitre() {
		return titre;
	}
	public void setTitre(String titre) {
		this.titre = titre;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getRealisateur() {
		return realisateur;
	}
	public void setRealisateur(String realisateur) {
		this.realisateur = realisateur;
	}
	public Integer getAgeMin() {
		return ageMin;
	}
	public void setAgeMin(Integer ageMin) {
		this.ageMin = ageMin;
	}
	public Integer getAgeMax() {
		return ageMax;
	}
	public void setAgeMax(Integer ageMax) {
		this.ageMax = ageMax;
	}
	public String getSoustitres() {
		return soustitres;
	}
	public void setSoustitres(String soustitres) {
		this.soustitres = soustitres;
	}
	public Integer getDureeMin() {
		return dureeMin;
	}
	public void setDureeMin(Integer dureeMin) {
		this.dureeMin = dureeMin;
	}
	public Integer getDureeMax() {
		return dureeMax;
	}
	public void setDureeMax(Integer dureeMax) {
		this.dureeMax = dureeMax;
	}
}
