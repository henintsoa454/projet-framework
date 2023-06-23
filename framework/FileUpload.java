
package etu1923.framework.fileUpload;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.Part;

public class FileUpload{
	String nom;
	byte[] byte_tab;
	private String savePath = "";
	
	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public byte[] getByte_tab() {
		return byte_tab;
	}

	public void setByte_tab(byte[] byte_tab) {
		this.byte_tab = byte_tab;
	}

	public String getSavePath() {
		return savePath;
	}

	public void setSavePath(String savePath) {
		this.savePath = savePath;
	}	
}
