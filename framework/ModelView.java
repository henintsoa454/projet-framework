package etu1923.framework;

public class ModelView {
	String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	
	public ModelView() {
		
	}
	
	public ModelView(String url) {
		this.setUrl(url);
	}
}
