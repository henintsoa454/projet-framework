package etu1923.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.text.ParseException;
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

	public static Object cast(Object obj, Class<?> clazz) throws ParseException {
		if (obj == null || clazz.isInstance(obj)) {
			return obj;
		}
		if (clazz == int.class || clazz == Integer.class || clazz == Integer.TYPE) {
			return Integer.parseInt(obj.toString());
		} else if (clazz == double.class || clazz == Double.class) {
			return Double.parseDouble(obj.toString());
		} else if (clazz == String.class) {
			return obj.toString();
		} else if (clazz == boolean.class || clazz == Boolean.class) {
			return Boolean.parseBoolean(obj.toString());
		} else if (clazz == java.util.Date.class) {
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
			try {
				return formatter.parse(obj.toString());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else if (clazz == java.sql.Date.class) {
			return java.sql.Date.valueOf(obj.toString());
		}
		return null;
	}

	public static ArrayList<String> enumerationToList(Enumeration<String> enumeration) {
	    ArrayList<String> list = new ArrayList<String>();
	    while (enumeration.hasMoreElements()) {
	        list.add(enumeration.nextElement());
	    }
	    return list;
	}

	public static boolean checkIfExistForField(ArrayList<String> enumerationList, Field field){
		for(int i = 0; i < enumerationList.size(); i++){
			System.out.println("ENUMERATION: "+enumerationList.get(i)+" field: "+field.getName());
			if(field.getName().compareTo(enumerationList.get(i))==0){
				return true;
			}
		}
		return false;
	}
	
	public static boolean checkIfExistForParameter(ArrayList<String> enumerationList, Parameter parameter){
		for(int i = 0; i < enumerationList.size(); i++){
			System.out.println("ENUMERATION: "+enumerationList.get(i).trim()+" parameter: "+parameter.getName().trim());
			if(parameter.getName().trim().compareTo(enumerationList.get(i).trim())==0){
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
                	Method[] allMethods = object.getClass().getDeclaredMethods();
                 	Enumeration<String> enumeration = request.getParameterNames();
					ArrayList<String> enumerationList = new ArrayList<String>();
					enumerationList = enumerationToList(enumeration);
					Method equalMethod = null;
					for (int i = 0; i < allMethods.length; i++) {
						if(allMethods[i].getName().compareTo(mapping.getMethod())==0) {
							equalMethod = allMethods[i];
							break;
						}
					}
					Parameter[] parameters = equalMethod.getParameters();
					Object[] declaredParameter = new Object[parameters.length];
					for (int i = 0; i < parameters.length; i++) {
						if(checkIfExistForParameter(enumerationList, parameters[i])) {
							Object parameterObject = request.getParameter(parameters[i].getName().trim());
							parameterObject = cast(parameterObject, parameters[i].getType());
							declaredParameter[i] = parameterObject;
						}
						else declaredParameter[i] = null;
					}
                	for (int i = 0; i < fields.length; i++) {
						System.out.println("FIELD: "+fields[i].getName());
						if(checkIfExistForField(enumerationList, fields[i])) {
							System.out.println("EXIST FIELD: "+fields[i].getName());
							Object attributObject = request.getParameter(fields[i].getName());
							Object objectCast = cast(attributObject, fields[i].getType());
							Method method = clazz.getDeclaredMethod("set"+capitalizedName(fields[i].getName()),fields[i].getType());
							method.invoke(object, objectCast);
						}
					}
                	Object returnObject = equalMethod.invoke(object,declaredParameter);
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
