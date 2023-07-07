package etu1923.framework;

import java.util.HashMap;

public class ModelView {
	String url;
	HashMap<String,Object> data;
	HashMap<String, Object> session;
	boolean isJSON = false;
	
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

	public HashMap<String, Object> getSession() {
		return session;
	}

	public void setSession(HashMap<String, Object> session) {
		this.session = session;
	}

	public boolean isJSON() {
		return isJSON;
	}

	public void setJSON(boolean isJSON) {
		this.isJSON = isJSON;
	}

	public ModelView() {
		this.setData(new HashMap<>());
		this.setSession(new HashMap<>());
	}
	
	public ModelView(String url) {
		this.setUrl(url);
		this.setData(new HashMap<>());
		this.setSession(new HashMap<>());
	}
	
	public ModelView(String url,HashMap<String,Object> data) {
		this.setUrl(url);
		this.setData(new HashMap<>());
		this.setSession(new HashMap<>());
		this.setData(data);
	}
	
	public void addItem(String key,Object value) {
		if(!this.getData().containsKey(key)) {
			this.getData().put(key, value);
		}
	}
	
	public void addSession(String key,Object value) {
		// if(!this.getSession().containsKey(key)) {
			this.getSession().put(key, value);
		// }
	}
}
