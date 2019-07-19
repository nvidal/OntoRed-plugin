package uy.edu.fing.ontored.utils;

import java.io.InputStream;
import java.util.Properties;

public class LanguageManager {
	
	//Manejo de idioma
	public static final int ESP = 0;
	public static final int ENG = 1;
	
	static private LanguageManager manager = null;
	static private Properties props =null;
	static private int lang = ENG;
	
	protected LanguageManager(){
		 try{
	        InputStream input = null;
	        if(lang == ENG)
	        	input = LanguageManager.class.getClassLoader().getResourceAsStream("lang/ENG.properties");//"src/uy/edu/fing/ontored/utils/ENG.properties");//"lang/ENG.properties");
	        else
	        	input = LanguageManager.class.getClassLoader().getResourceAsStream("lang/ESP.properties");//"src/uy/edu/fing/ontored/utils/ESP.properties");//"lang/ESP.properties");//
	        props = new Properties();
	        props.load(input);
	       } 
	    catch(Exception e){
	        System.out.println("error" + e);
	    }
	}
	
	public static LanguageManager getInstance(){
		if(manager == null){
			manager = new LanguageManager();
		}
		return manager;
	}
	
	public String getText(String key){
		String res = props.getProperty(key);
		if (res !=null)
			return res;
		else
			return "Not Found";
	}

	public static boolean cambiarIdioma(int idioma){
		if(idioma != lang){
			lang = idioma;
			manager = new LanguageManager();
			return true;
		}
		return false;
	}
	
	public static void setIdioma(int idioma){
		lang = idioma;
	}
	
	public int getIdioma(){
		return lang;
	}
	
}
