package etu1923.framework.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import etu1923.framework.annotation.AppRoute;
import etu1923.framework.ModelView;

public class Emp {
	String nom;
	String prenom;
	Date dateNaissance;
	
	@AppRoute(url = "/emp-get-nom")
	public String getNom() {
		return nom;
	}
	@AppRoute(url = "/emp-set-nom")
	public void setNom(String nom) {
		this.nom = nom;
	}
	@AppRoute(url = "/emp-get-prenom")
	public String getPrenom() {
		return prenom;
	}
	@AppRoute(url = "/emp-set-prenom")
	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}
	@AppRoute(url = "/emp-get-dateNaissance")
	public Date getDateNaissance() {
		return dateNaissance;
	}
	@AppRoute(url = "/emp-set-dateNaissance")
	public void setDateNaissance(Date dateNaissance) {
		this.dateNaissance = dateNaissance;
	}
	@AppRoute(url = "/emp-get-age")
	public int getAge() {
		Date nowDate = Date.valueOf(LocalDate.now());
		
		Calendar calendar = Calendar.getInstance();
		Calendar calendar2 = Calendar.getInstance();
		
		calendar.setTime(this.getDateNaissance());
		calendar2.setTime(nowDate);
		
		return calendar2.get(Calendar.YEAR) - calendar.get(Calendar.YEAR);
	}
	@AppRoute(url = "/emp-getAll")
	public ModelView getAllEmp(){
		ModelView modelView = new ModelView("TongaSoa.jsp");
		String testData = "Mande";
		modelView.addItem("test", testData);
		return modelView;
	}
	@AppRoute(url = "/emp-printNom")
	public ModelView printName(){
		ModelView modelView = new ModelView("Veloma.jsp");
		modelView.addItem("test",this.getNom());
		return modelView;
	}
}
