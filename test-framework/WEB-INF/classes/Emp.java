package etu1923.framework.entity;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Calendar;

import etu1923.framework.annotation.AppRoute;
import etu1923.framework.annotation.Singleton;
import etu1923.framework.ModelView;

@Singleton()
public class Emp {
	String nom;
	String prenom;
	Date age;
	int appel = 0;
	
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
	@AppRoute(url = "/emp-get-age")
	public Date getAge() {
		return age;
	}
	@AppRoute(url = "/emp-set-age")
	public void setAge(Date age) {
		this.age = age;
	}
	@AppRoute(url = "/emp-get-appel")
	public int getAppel() {
		return appel;
	}
	@AppRoute(url = "/emp-set-appel")
	public void setAppel(int appel) {
		this.appel = appel;
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
		modelView.addItem("test2",this.getPrenom());
		modelView.addItem("test3",this.getAge());
		return modelView;
	}
	@AppRoute(url = "/emp-HaveSon")
	public ModelView haveSon(String nombre){
		String result = "Le nombre d'enfant est "+nombre;
		ModelView modelView = new ModelView("Welcome.jsp");
		modelView.addItem("test",result);
		return modelView;
	}
	@AppRoute(url= "/emp-Appel")
	public ModelView appel(){
		ModelView modelView = new ModelView("singleton.jsp");
		this.appel = this.appel+1;
		modelView.addItem("nbr",this.appel);
		return modelView;
	}
}
