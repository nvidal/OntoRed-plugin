package uy.edu.fing.ontored.utils;

import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLOntology;

public class WrapperOWLClass {

	private OWLClass clase;
	private OWLOntology ontology;
	
	public WrapperOWLClass(OWLClass c, OWLOntology o){
		this.clase=c;
		this.ontology=o;
	}
	public String toString(){
		String nombre = clase.toString();
		int index = nombre.lastIndexOf("/");
		if (index <0)
			index =0;
		return nombre.substring(index+1, nombre.length()-1);
	}
	public OWLClass getClase() {
		return clase;
	}
	public void setClase(OWLClass clase) {
		this.clase = clase;
	}
	public OWLOntology getOntology() {
		return ontology;
	}
	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	public String getOntologyName(){
		return this.ontology.toString();
	}
	
}
