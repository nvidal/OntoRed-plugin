package uy.edu.fing.ontored.utils;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;

public class FactoryOWLManager {
	private static final Logger log = Logger.getLogger(FactoryOWLManager.class);
	private static OWLModelManager manager = null;
	
	public FactoryOWLManager(OWLModelManager man){
		manager = man;
		log.info("OWLManager inicializado");
	}
	
	public static OWLModelManager getOWLManager(){
		if(manager == null){
			log.error("Error al intentar obtener OWLManager");
			return null;
		}
		else
			return manager;
	}


}
