package uy.edu.fing.ontored.red;



import java.io.Serializable;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.OWLOntology;

import uy.edu.fing.ontored.exceptions.EliminarException;
import uy.edu.fing.ontored.exceptions.ExtensionException;
import uy.edu.fing.ontored.utils.LanguageManager;



public class Extension extends Object implements Relacion, Serializable{
	
	private static final long serialVersionUID = 1L;
	//Logger
	private static final Logger log = Logger.getLogger(Extension.class);
	
	private OWLOntology domain;
	private OWLOntology range;
	private Red red;
	
	
	public Extension(OWLOntology domain, OWLOntology range, Red red) {
		super();
		this.domain = domain;
		this.range = range;
		this.red = red;
		log.info("[Created]:" + this.toString());
	}
	
	@Override
	public void save() throws ExtensionException {
		
	}
							
	@Override
	public void delete() throws EliminarException {

	}
	
	@Override
	public String toString(){
		if (LanguageManager.getInstance().getIdioma()==LanguageManager.ESP)
			return "Extension: ["+ this.getNombreDomain() +" incluye a: " + this.getNombreRange()+"]";
		else//if (FactoryOWLManager.getIdioma()==FactoryOWLManager.ENG)
			return "Extension: ["+ this.getNombreDomain() +" includes: " + this.getNombreRange()+"]";
	}
	
	public String getNombreDomain(){
		String nombre = this.domain.getOntologyID().getOntologyIRI().toString();
		return nombre.substring(nombre.lastIndexOf("/")+1, nombre.length());
	}
	public String getNombreRange(){
		String nombre = this.range.getOntologyID().getOntologyIRI().toString();
		return nombre.substring(nombre.lastIndexOf("/")+1, nombre.length());
	}
	@Override
	public int getTipoRelacion(){
		return Relacion.EXTENSION;
	}
	public OWLOntology getDomain() {
		return domain;
	}
	public void setDomain(OWLOntology domain) {
		this.domain = domain;
	}
	public OWLOntology getRange() {
		return range;
	}
	public void setRange(OWLOntology range) {
		this.range = range;
	}
	public Red getRed() {
		return red;
	}
	public void setRed(Red red) {
		this.red = red;
	}
	
	public boolean equals(Object o) {
	    if ( this == o ) 
	    	return true;
	    if ( !(o instanceof Extension) ) 
	    	return false;
	    
	    Extension e = (Extension)o;
	    return ( this.domain == e.getDomain() &&
	    		this.range == e.getRange() );
	}

	@Override
	/**
	 * Retorna true, si la ontologia importada(range) es igual a O
	 */
	public boolean relacionConOntologia(OWLOntology o) {
		return this.range == o;
	}

	@Override
	public String toStringCorto() {
		return "Extension( "+ this.getNombreDomain() +" > " + this.getNombreRange()+" )";
	}
	
	
}
