package etu1923.framework;

public class Mapping {
	String className;
	String method;
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	
	public Mapping() {
		
	}
	
	public Mapping(String className,String Method) {
		this.setClassName(className);
		this.setMethod(Method);
	}
}
