package etu1923.framework;

import java.util.HashMap;

public class ModelView {
	String url;
	HashMap<String,Object> data;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
	
	public HashMap<String, Object> getData() {
		return data;
	}

	public void setData(HashMap<String, Object> data) {
		this.data = data;
	}

	public ModelView() {
		this.setData(new HashMap<>());
	}
	
	public ModelView(String url) {
		this.setUrl(url);
		this.setData(new HashMap<>());
	}
	
	public ModelView(String url,HashMap<String,Object> data) {
		this.setUrl(url);
		this.setData(new HashMap<>());
		this.setData(data);
	}
	
	public void addItem(String key,Object value) {
		if(!this.getData().containsKey(key)) {
			this.getData().put(key, value);
		}
	}
}
