package etu1923.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import etu1923.framework.Mapping;
import etu1923.framework.ModelView;

public class FrontServlet extends HttpServlet{
	HashMap<String, Mapping> mappingUrls;
	
	public HashMap<String, Mapping> getMappingUrls() {
		return mappingUrls;
	}

	public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
		this.mappingUrls = mappingUrls;
	}
	
	public static ArrayList<Class<?>> checkClasses(File directory, String packageName) throws Exception {
        ArrayList<Class<?>> classes = new ArrayList<>();
        // if (!directory.exists()) {
        //     return classes;
        // }
		String path = packageName.replaceAll("[.]","/");
		URL packageUrl = Thread.currentThread().getContextClassLoader().getResource(path);
		directory = new File(packageUrl.toURI());
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                classes.addAll(checkClasses( file , packageName + "." + file.getName() ));
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }
        return classes;
    }
	
	public static Object castObject(Object object, Class<?> castType) {
        try {
            if (castType.equals(Integer.TYPE) || castType.equals(Integer.class)) {
                return Integer.parseInt(object.toString());
            } else if (castType.equals(Double.TYPE) || castType.equals(Double.class)) {
                return Double.parseDouble(object.toString());
            } else if (castType.equals(Boolean.TYPE) || castType.equals(Boolean.class)) {
                return Boolean.parseBoolean(object.toString());
            } else if (castType.equals(Date.class)) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date date = df.parse(object.toString());
                return date;
            } else if (castType.equals(Date.class)) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
                Date utilDate = df.parse(object.toString());
                return new java.sql.Date(utilDate.getTime());
            } else {
                return object;
            }
        } catch (Exception e) {
            return null;
        }
    }
	
	public static boolean checkIfExist(Enumeration<String> enumeration, Field field) {
		for (;enumeration.hasMoreElements();) {
			if(field.getName().compareTo(enumeration.nextElement())==0) {
				return true;
			}
		}
		return false;
	}
	
	public static String capitalizedName(String name) {
		return name.substring(0, 1).toUpperCase() + name.substring(1);
	}
	
	@Override
	public void init() throws ServletException {
		 File f = null;
	        try{
	            f = new File("../webapps/Framework/WEB-INF/classes/etu1923");
	            ArrayList<Class<?>> classes = FrontServlet.checkClasses(f,"etu1923");
                this.setMappingUrls(new HashMap<>());
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
			String doList = "";
            if(request.getParameter("doList") != null){
                doList = request.getParameter("doList");
            }
                out.println(this.getMappingUrls());
                for (String key : this.getMappingUrls().keySet()) {
                    Mapping mapping = this.getMappingUrls().get(key);
                    out.println("Url:"+key+" ClassName:"+mapping.getClassName()+" Method:"+mapping.getMethod());
                }
                
                String urlString = request.getRequestURI().substring(request.getContextPath().length());
                out.println("URLSTRING: "+urlString);
                if(this.getMappingUrls().containsKey(urlString)) {
                	Mapping mapping = this.getMappingUrls().get(urlString);
                	Class clazz = Class.forName(mapping.getClassName());
                	Object object = clazz.getConstructor().newInstance();
                	Field[] fields = object.getClass().getDeclaredFields();
                	Enumeration<String> enumeration = request.getParameterNames();
                	for (int i = 0; i < fields.length; i++) {
						if(checkIfExist(enumeration, fields[i])) {
							Object attributObject = request.getParameter(fields[i].getName());
							Object objectCast = castObject(attributObject, fields[i].getType());
							Method method = clazz.getDeclaredMethod("set"+capitalizedName(fields[i].getName()),fields[i].getType());
							method.invoke(object, objectCast);
						}
					}
                	Method method = clazz.getDeclaredMethod(mapping.getMethod());
                	Object returnObject = method.invoke(object,(Object[])null);
                	if(returnObject instanceof ModelView) {
                		ModelView modelView = (ModelView) returnObject;
                		HashMap<String, Object> data = modelView.getData();
                		for (String key : data.keySet()) {
							request.setAttribute(key, data.get(key));
						}
                		RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelView.getUrl());
                		requestDispatcher.forward(request, response);
                	}
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
