package it.polito.tdp.baseball.model;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

public class People {

    private String playerID;
    private String birthCountry;
    private String birthCity;
    private String deathCountry;
    private String deathCity;
    private String nameFirst;
    private String nameLast;
    private Integer weight;
    private Integer height;
    private String bats;
    private String throwString;
    private LocalDateTime birthDate;
    private LocalDateTime debutDate;
    private LocalDateTime finalgameDate;
    private LocalDateTime deathDate;
    private double salario;

    

    public People(String playerID, String birthCountry, String birthCity, String deathCountry, String deathCity,
			String nameFirst, String nameLast, Integer weight, Integer height, String bats, String throwString,
			LocalDateTime birthDate, LocalDateTime debutDate, LocalDateTime finalgameDate, LocalDateTime deathDate) {
		super();
		this.playerID = playerID;
		this.birthCountry = birthCountry;
		this.birthCity = birthCity;
		this.deathCountry = deathCountry;
		this.deathCity = deathCity;
		this.nameFirst = nameFirst;
		this.nameLast = nameLast;
		this.weight = weight;
		this.height = height;
		this.bats = bats;
		this.throwString = throwString;
		this.birthDate = birthDate;
		this.debutDate = debutDate;
		this.finalgameDate = finalgameDate;
		this.deathDate = deathDate;
	}

    
	public double getSalario() {
		return salario;
	}


	public void setSalario(double salario) {
		this.salario = salario;
	}


	public String getPlayerID(){
        return playerID;
    }

    public void setPlayerID(String playerID){
        this.playerID=playerID;
    }

    public String getBirthCountry(){
        return birthCountry;
    }

    public void setBirthCountry(String birthCountry){
        this.birthCountry=birthCountry;
    }

    public String getBirthCity(){
        return birthCity;
    }

    public void setBirthCity(String birthCity){
        this.birthCity=birthCity;
    }

    public String getDeathCountry(){
        return deathCountry;
    }

    public void setDeathCountry(String deathCountry){
        this.deathCountry=deathCountry;
    }

    public String getDeathCity(){
        return deathCity;
    }

    public void setDeathCity(String deathCity){
        this.deathCity=deathCity;
    }

    public String getNameFirst(){
        return nameFirst;
    }

    public void setNameFirst(String nameFirst){
        this.nameFirst=nameFirst;
    }

    public String getNameLast(){
        return nameLast;
    }

    public void setNameLast(String nameLast){
        this.nameLast=nameLast;
    }

    public Integer getWeight(){
        return weight;
    }

    public void setWeight(Integer weight){
        this.weight=weight;
    }

    public Integer getHeight(){
        return height;
    }

    public void setHeight(Integer height){
        this.height=height;
    }

    public String getBats(){
        return bats;
    }

    public void setBats(String bats){
        this.bats=bats;
    }

    public String getThrows(){
        return throwString;
    }

    public void setThrows(String throwString){
        this.throwString=throwString;
    }

    public LocalDateTime getBirthDate(){
        return birthDate;
    }

    public void setBirthDate(LocalDateTime birthDate){
        this.birthDate=birthDate;
    }

    public LocalDateTime getDebutDate(){
        return debutDate;
    }

    public void setDebutDate(LocalDateTime debutDate){
        this.debutDate=debutDate;
    }

    public LocalDateTime getFinalgameDate(){
        return finalgameDate;
    }

    public void setFinalgameDate(LocalDateTime finalgameDate){
        this.finalgameDate=finalgameDate;
    }

    public LocalDateTime getDeathDate(){
        return deathDate;
    }

    public void setDeathDate(LocalDateTime deathDate){
        this.deathDate=deathDate;
    }

	@Override
	public int hashCode() {
		return Objects.hash(playerID);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		People other = (People) obj;
		return Objects.equals(playerID, other.playerID);
	}

	@Override
	public String toString() {
		return nameFirst + " " + nameLast;
	}
    
    

}

