package uy.edu.fing.ontored.utils;

import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;

public class WrapperOWLIndividual {

	private OWLIndividual individual;
	private OWLOntology ontology;
	
	public WrapperOWLIndividual(OWLIndividual c, OWLOntology o){
		this.individual=c;
		this.ontology=o;
	}
	public String toString(){
		String nombre = individual.toString();
		int index = nombre.lastIndexOf("/");
		if (index <0)
			index =0;
		return nombre.substring(index+1, nombre.length()-1);
	}
	public OWLIndividual getIndividual() {
		return individual;
	}
	public void setIndividual(OWLIndividual individual) {
		this.individual = individual;
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
