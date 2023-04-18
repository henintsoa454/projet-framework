package etu1923.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu1923.framework.Mapping;
import etu1923.framework.ModeleView;

public class FrontServlet extends HttpServlet{
	HashMap<String,Mapping> mappingsUrl = new HashMap<>();
	
	public HashMap<String, Mapping> getMappingsUrl() {
		return mappingsUrl;
	}

	public void setMappingsUrl(HashMap<String, Mapping> mappingsUrl) {
		this.mappingsUrl = mappingsUrl;
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
    public void init() throws ServletException{
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
                        this.getMappingsUrl().put(url,newmap);
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
        	String doList = "";
            if(request.getParameter("doList") != null){
                doList = request.getParameter("doList");
            }
            else{
                out.println("Not found");
            }
            if(doList.compareTo("true") == 0){
                out.println(this.getMappingsUrl());
                for (String key : this.getMappingsUrl().keySet()) {
                    Mapping mapping = this.getMappingsUrl().get(key);
                    out.println("Url:"+key+" ClassName:"+mapping.getClassName()+" Method:"+mapping.getMethod());
                }
            }
            if(mappingsUrl.containsKey(request.getQueryString())) {
            	Mapping mapping = mappingsUrl.get(request.getQueryString());
            	
            	Object object = Class.forName(mapping.getClassName()).getConstructor().newInstance();
            	
            	ModeleView modeleView = (ModeleView) object.getClass().getMethod(mapping.getMethod()).invoke(object);
            	
            	if (modeleView.getData()!=null) {
            		for (String key : modeleView.getData().keySet()) {
            			request.setAttribute(key, modeleView.getData().get(key));
					}
				}
            	
            	RequestDispatcher requestDispatcher = request.getRequestDispatcher(modeleView.getView());
            	requestDispatcher.forward(request, response);
            }
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
