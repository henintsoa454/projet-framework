package etu1923.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu1923.framework.Mapping;

public class FrontServlet extends HttpServlet{
	HashMap<String, Mapping> mappingUrls;
	
	public HashMap<String, Mapping> getMappingUrls() {
		return mappingUrls;
	}

	public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
		this.mappingUrls = mappingUrls;
	}
	
	public static ArrayList<Class<?>> checkClasses(File directory, String packageName) throws ClassNotFoundException {
        ArrayList<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(checkClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }
	
	@Override
	public void init() throws ServletException {
		 File f = null;
	        try{
	            f = new File("../webapps/Framework/WEB-INF/classes/etu1923");
	            ArrayList<Class<?>> classes = FrontServlet.checkClasses(f,"etu1923");
	            for(int i = 0;i<classes.size();i++){
	                Class<?> classe = classes.get(i);
	                Method[] methods = classe.getDeclaredMethods();
	                for (Method method : methods) {
	                    if (method.isAnnotationPresent(etu1923.framework.annotation.AppRoute.class)) {	
	                        String url = method.getAnnotation(etu1923.framework.annotation.AppRoute.class).url();
	                        Mapping newmap = new Mapping();
	                        System.out.println(classe.getName());
	                        System.out.println(method.getName());
	                        newmap.setClassName(classe.getName());
	                        newmap.setMethod(method.getName());
	                        this.getMappingUrls().put(url,newmap);
	                    }
	                }
	            } 
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {		
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {     
        	String url = request.getRequestURL().toString()+"?"+request.getQueryString();
        	out.println("URL: "+url);            
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {
        processRequest(request, response);
    }

    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }
}
