package uy.edu.fing.ontored.red;



import java.io.Serializable;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uy.edu.fing.ontored.utils.FactoryOWLManager;
import uy.edu.fing.ontored.utils.LanguageManager;



public class Mapping extends Object implements Relacion, Serializable{
	
	private static final long serialVersionUID = 1L;
	//Logger
	private static final Logger log = Logger.getLogger(Mapping.class);
	//Tipos de mapping
	public static final int TIPO_EQUIVALANTE = 0;
	public static final int TIPO_SUB_CLASE = 1;
	
	private OWLClass domain;
	private OWLClass range;
	private int tipo;
	private Red red;
	
	
	public Mapping(OWLClass domain, OWLClass range, int tipo, Red red) {
		super();
		this.domain = domain;
		this.range = range;
		this.tipo = tipo;
		this.red = red;
		//log.info("[Created]:" + this.toString());
	}
	
	@Override
	public void save() {
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();
        //Creao la relacion MAPPING segun el tipo elegido
    	OWLAxiom axiom;
    	if(this.tipo==TIPO_EQUIVALANTE){
        	axiom = factory.getOWLEquivalentClassesAxiom(this.domain, this.range);
    	}
    	else{
    		axiom = factory.getOWLSubClassOfAxiom(this.domain, this.range);
    	}
    	//Agrego el axioma de Mapping a la red y aplico los cambios.
        AddAxiom addAxiom = new AddAxiom(this.red.getOntologia(), axiom);
        manager.applyChange(addAxiom);   
	}
	
	@Override
	public void delete() {
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		// Obtengo la relacion de Mapping segun su tipo
		OWLAxiom axiom;
    	if(this.tipo==TIPO_EQUIVALANTE){
        	axiom = factory.getOWLEquivalentClassesAxiom(this.domain, this.range);
    	}
    	else{
        	axiom = factory.getOWLSubClassOfAxiom(this.domain, this.range);
    	}
    	//Elimino la relacion de la red
    	manager.removeAxiom(this.red.getOntologia(), axiom);
    	this.red.removeRelacion(this);
	}
	
	@Override
	public String toString(){
		if (getTipo()== TIPO_EQUIVALANTE){ 
			if (LanguageManager.getInstance().getIdioma()==LanguageManager.ESP)
				return "Mapping: ["+ this.getNombreDomain() +" es Equivalente a: " + this.getNombreRange()+"]";
			else
				return "Mapping: ["+ this.getNombreDomain() +" Equivalent to: " + this.getNombreRange()+"]";
		}
		if (getTipo()== TIPO_SUB_CLASE) {
			if (LanguageManager.getInstance().getIdioma()==LanguageManager.ESP)
				return "Mapping: ["+ this.getNombreDomain() +" es SubClase de: " + this.getNombreRange()+"]";
			else
				return "Mapping: ["+ this.getNombreDomain() +" SubClass of: " + this.getNombreRange()+"]";
		}
		return "Error: Mapping de tipo no conocido";
	}
	
	public String getNombreDomain(){
		String nombre = this.getDomain().toString();
		int index = nombre.lastIndexOf("/");
		if (index <0)
			index =0;
		String aux = nombre.substring(index+1, nombre.length()-1);
		int i_nom = aux.indexOf(this.red.getNombre());
		if (i_nom > -1){
			aux = aux.substring(i_nom + this.red.getNombre().length(), aux.length());
		}
		return aux;
	}
	public String getNombreRange(){
		String nombre = this.getRange().toString();
		int index = nombre.lastIndexOf("/");
		if (index <0)
			index =0;
		String aux = nombre.substring(index+1, nombre.length()-1);
		int i_nom = aux.indexOf(this.red.getNombre());
		if (i_nom > -1){
			aux = aux.substring(i_nom + this.red.getNombre().length(), aux.length());
		}
		return aux;
	}
	@Override
	public int getTipoRelacion(){
		return Relacion.MAPPING;
	}
	public OWLClass getDomain() {
		return domain;
	}
	public void setDomain(OWLClass domain) {
		this.domain = domain;
	}
	public OWLClass getRange() {
		return range;
	}
	public void setRange(OWLClass range) {
		this.range = range;
	}
	public int getTipo() {
		return tipo;
	}
	public void setTipo(int tipo) {
		this.tipo = tipo;
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
	    if ( !(o instanceof Mapping) ) 
	    	return false;
	    Mapping m = (Mapping)o;
	    
	    return ( this.tipo == m.getTipo() &&
	    		this.domain == m.getDomain() &&
	    		this.range == m.getRange() );
	}

	@Override
	public boolean relacionConOntologia(OWLOntology o) {
		return ( o.getClassesInSignature().contains(this.domain) ||
				o.getClassesInSignature().contains(this.range) );
	}

	@Override
	public String toStringCorto() {
		if (getTipo()== TIPO_EQUIVALANTE) 
			return "Mapping( "+ this.getNombreDomain() +" = " + this.getNombreRange()+" )";
		if (getTipo()== TIPO_SUB_CLASE) 
			return "Mapping( "+ this.getNombreDomain() +" < " + this.getNombreRange()+" )";
		return "Error";
	}
	
	
}
