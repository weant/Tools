package com.otn.tool.common.properties;

import java.io.*;
import java.util.*;

/**
 * 读取Properties类型配置文件的抽象类
 *
 */
public abstract class PropertiesConfig {
	
	protected Properties properties = new Properties();
	protected Map<String, List<String>> valueTokey;
	
	public PropertiesConfig() {
		initProperties();
	}
	
	protected void initProperties(){
		try {
			File currentDir = new File(".");
			String confFilePath = currentDir.getAbsolutePath() + getConfFile();
			//properties.load(new FileReader(confFilePath));
			properties.load(new InputStreamReader(new FileInputStream(confFilePath), "UTF-8"));
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public String getProperty(String key){
		if(properties.containsKey(key)){
			return properties.getProperty(key);
		} else {
			return "";
		}
	}
	
	public Properties getProperties(){
		if(properties != null){
			return properties;
		} else {
			return null;
		}
	}
	
	/**
	 * 修改或者增加键值对，key存在则修改，反之则增加
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean setData(String key, String value){
		properties.setProperty(key, value);
		return saveProperties();
	}
	
	public Map<String,String> getPropertiesMap(){
		Map<String, String> map = new HashMap<String, String>();
		if(properties == null || properties.isEmpty()) return map;
		for (Iterator<?> it = properties.keySet().iterator(); it.hasNext();) {
			String key = (String) it.next();
			map.put(key, properties.getProperty(key));
		}
		return map;
	}
	
	/**
	 * 通过值获取该值对应的所有键的集合
	 * @param value
	 * @return
	 */
	public List<String> getKeybyValue(String value){
		if(valueTokey == null) valueTokey = new HashMap<String, List<String>>();
		if(valueTokey.isEmpty()){
			for (Iterator<?> it = properties.keySet().iterator(); it.hasNext();) {
				String key = (String)it.next();
				String val = properties.getProperty(key);
				if(valueTokey.containsKey(val)){
					valueTokey.get(val).add(key);
				} else {
					List<String> keys = new ArrayList<String>();
					keys.add(key);
					valueTokey.put(val, keys);
				}
			}
		}
		if(!valueTokey.containsKey(value)){
			return Collections.emptyList();
		}
		return valueTokey.get(value);
	}

	public boolean saveProperties() {
		try {
			OutputStream fos = new FileOutputStream(new File(".").getAbsolutePath() + getConfFile());
			properties.store(fos, "");
			fos.close();
			if (valueTokey != null) {
				valueTokey.clear();
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;

	}
	/**
	 * 获取具体的配置文件相对路径
	 * @return
	 */
	protected abstract String getConfFile();

}
