package etu1923.framework.servlet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.text.ParseException;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import com.google.gson.Gson;
import etu1923.framework.Mapping;
import etu1923.framework.ModelView;
import etu1923.framework.annotation.Authentification;
import etu1923.framework.fileUpload.FileUpload;

@MultipartConfig
public class FrontServlet extends HttpServlet{
	HashMap<String, Mapping> mappingUrls;
	HashMap<String, Object> singletons;
	String connectedSession;
	String profileSession;
	
	public HashMap<String, Mapping> getMappingUrls() {
		return mappingUrls;
	}

	public void setMappingUrls(HashMap<String, Mapping> mappingUrls) {
		this.mappingUrls = mappingUrls;
	}
		
	public HashMap<String, Object> getSingletons() {
		return singletons;
	}

	public void setSingletons(HashMap<String, Object> singletons) {
		this.singletons = singletons;
	}
	

	public static ArrayList<Class<?>> checkClasses(File directory, String packageName) throws Exception {
        ArrayList<Class<?>> classes = new ArrayList<>();
        
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

	public void setDefault(Object object)throws Exception{
		Field[] fields = object.getClass().getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Method method = object.getClass().getDeclaredMethod("set"+capitalizedName(fields[i].getName()),fields[i].getType());
			if(fields[i].getType().getName().contains("int") || fields[i].getType().getName().contains("double") || fields[i].getType().getName().contains("float")){
				method.invoke(object, 0);
			}else{
				method.invoke(object, (Object)null);
			}
			
		}
	}
	
	@Override
	public void init() throws ServletException {
		 File f = null;
	        try{
	            f = new File("../webapps/Framework/WEB-INF/classes/etu1923");
	            ArrayList<Class<?>> classes = FrontServlet.checkClasses(f,"etu1923");
	            this.setSingletons(new HashMap<>());
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
	                if(classe.isAnnotationPresent(etu1923.framework.annotation.Singleton.class)) {
						System.out.println("Class singleton: "+classe.getName());
	                	this.getSingletons().put(classe.getName(), null);
	                }
	            }
	            this.connectedSession = getInitParameter("isConnected");
	            this.profileSession = getInitParameter("Profile");
	        }
	        catch(Exception e){
	            e.printStackTrace();
	        }
	}

	protected void processRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {		
        response.setContentType("text/html;charset=UTF-8");
		PrintWriter out = response.getWriter();
        try {     
        	if(request.getContentType() != null) {
        		System.out.println(request.getContentType());
        		ArrayList<FileUpload> allUploads = new ArrayList<FileUpload>();
        		Collection<Part> parts = request.getParts();
				if(parts != null){
					for (Part part : parts) {						
						String fileName = part.getName();
						Part filePart = request.getPart(fileName);
						InputStream in = filePart.getInputStream();
						byte[] fileBytes = in.readAllBytes();
						String directory = "./uploads/";
						File file = new File(directory);
						if(!file.exists()){
							file.mkdirs();
						}
						System.out.println("FileName: "+fileName+" ,Directory: "+directory+" FileBytes: "+fileBytes);
						FileUpload fileUpload = new FileUpload();
						fileUpload.setNom(fileName);
						fileUpload.setSavePath(directory);
						fileUpload.setByte_tab(fileBytes);
						allUploads.add(fileUpload);						
					}
				}
				System.out.println(allUploads);
				request.setAttribute("all_uploads", allUploads);
        	}
        	String url = request.getRequestURL().toString()+"?"+request.getQueryString();
        	out.println("URL: "+url);                
            String urlString = request.getRequestURI().substring(request.getContextPath().length());
            out.println("URLSTRING: "+urlString);
            if(this.getMappingUrls().containsKey(urlString)) {
            	Mapping mapping = this.getMappingUrls().get(urlString);
            	Class clazz = Class.forName(mapping.getClassName());
            	Object object = null;
            	if(this.getSingletons().containsKey(mapping.getClassName())) {
                	if(this.getSingletons().get(mapping.getClassName()) == null) {
						System.out.println("Class insert in singletons: "+mapping.getClassName());
                		this.getSingletons().replace(mapping.getClassName(), null ,clazz.getConstructor().newInstance());
                	}
                	object = this.getSingletons().get(mapping.getClassName());
					setDefault(object);
                }
            	if( object == null ){
					object = clazz.getConstructor().newInstance();
				}
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
				if(equalMethod.isAnnotationPresent(etu1923.framework.annotation.Authentification.class)) {
					if(request.getSession().getAttribute(this.connectedSession)!=null) {
						Authentification authentification = equalMethod.getAnnotation(etu1923.framework.annotation.Authentification.class);
						if(!authentification.user().isEmpty() && !authentification.user().equals(request.getSession().getAttribute(this.profileSession))) {
							throw new Exception("Vous ne pouvez pas accéder à cet URL");
						}
					}
					else {
						throw new Exception("Aucune Session en cours");
					}
				}
				HashMap<String, Object> httpDATA = new HashMap<>();
				if(equalMethod.isAnnotationPresent(etu1923.framework.annotation.Session.class)) {
					Enumeration<String> attributeNames = request.getSession().getAttributeNames();
					while(attributeNames.hasMoreElements()){
						String attribute = attributeNames.nextElement();
						httpDATA.put(attribute, request.getSession().getAttribute(attribute));
					}
					Method method = clazz.getDeclaredMethod("set"+capitalizedName("session"),HashMap.class);
					method.invoke(object,httpDATA);
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
            		if(modelView.isJSON()) {
            			response.setContentType("application/json");
            			out.print(new Gson().toJson(modelView.getData()));
					}
					else if(equalMethod.isAnnotationPresent(etu1923.framework.annotation.APIRest.class)) {
            			response.setContentType("application/json");
            			out.print(new Gson().toJson(modelView));
            		}
            		else {
            			if(modelView.isInvalidateSession()) {
                			request.getSession().invalidate();
                		}
                		if(!modelView.getListSession().isEmpty()) {
                			for (int i = 0; i < modelView.getListSession().size(); i++) {
                				request.getSession().removeAttribute(modelView.getListSession().get(i));
    						}
                		}
                		HashMap<String, Object> data = modelView.getData();
                		HashMap<String, Object> session = modelView.getSession();
                		for (String key : data.keySet()) {
    						request.setAttribute(key, data.get(key));
    					}
                		for (String key : session.keySet()) {
    						request.getSession().setAttribute(key, session.get(key));
    					}
                		RequestDispatcher requestDispatcher = request.getRequestDispatcher(modelView.getUrl());
                		requestDispatcher.forward(request, response);
            		}
            	}
            } 
            else{
            	out.print("Cette url n'existe pas");
            }
        }
        catch (Exception e) {
            e.printStackTrace(out);
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
