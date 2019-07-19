package uy.edu.fing.ontored.utils;

import org.semanticweb.owlapi.model.OWLOntology;

public class WrapperOWLOntology {

	private OWLOntology ontology;
	
	public WrapperOWLOntology(){
		
	}
	public WrapperOWLOntology(OWLOntology o){
		this.ontology = o;
	}
	public String toString(){
		String nombre = ontology.getOntologyID().getOntologyIRI().toString();
		return nombre.substring(nombre.lastIndexOf("/")+1, nombre.length());
	}

	public OWLOntology getOntology() {
		return ontology;
	}

	public void setOntology(OWLOntology ontology) {
		this.ontology = ontology;
	}
	
}
