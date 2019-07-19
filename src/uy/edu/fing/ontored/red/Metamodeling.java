package uy.edu.fing.ontored.red;




import java.io.Serializable;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AddAxiom;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uy.edu.fing.ontored.utils.FactoryOWLManager;
import uy.edu.fing.ontored.utils.LanguageManager;



public class Metamodeling extends Object implements Relacion, Serializable{
	
	private static final long serialVersionUID = 1L;
	//Logger
	private static final Logger log = Logger.getLogger(Metamodeling.class);
	
	private OWLIndividual domain;
	private OWLClass range;
	private Red red;
	
	
	public Metamodeling(OWLIndividual domain, OWLClass range, Red red) {
		super();
		this.domain = domain;
		this.range = range;
		this.red = red;
		//log.info("[Created]:" + this.toString());
	}
	
	@Override
	public void save() {
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	//Creo la relacion de Meta-modeling
    	OWLAxiom axiom = factory.getOWLMetamodellingAxiom(range, domain);
    	//Agrego la relacion a la red y aplico los cambios
        AddAxiom addAxiom = new AddAxiom(this.red.getOntologia(), axiom);
        manager.applyChange(addAxiom);
	}
	
	@Override
	public void delete() {	
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		//Obtengo la relacion
    	OWLAxiom axiom = factory.getOWLMetamodellingAxiom(range, domain);
    	//Elimino el metamodeling de la red
    	manager.removeAxiom(this.red.getOntologia(), axiom);
    	this.red.removeRelacion(this);
	}
	
	@Override
	public String toString(){
		if (LanguageManager.getInstance().getIdioma() == LanguageManager.ESP)
			return "Metamodeling: ["+ this.getNombreDomain() +" es metamodelo de: " + this.getNombreRange()+"]";
		else
			return "Metamodeling: ["+ this.getNombreDomain() +" is metamodel of: " + this.getNombreRange()+"]";
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
		return Relacion.METAMODELING;
	}
	public OWLIndividual getDomain() {
		return domain;
	}
	public void setDomain(OWLIndividual domain) {
		this.domain = domain;
	}
	public OWLClass getRange() {
		return range;
	}
	public void setRange(OWLClass range) {
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
	    if ( !(o instanceof Metamodeling) ) 
	    	return false;
	    
	    Metamodeling m = (Metamodeling)o;
	    return ( this.domain == m.getDomain() &&
	    		this.range == m.getRange() );
	}

	@Override
	public boolean relacionConOntologia(OWLOntology o) {
		return ( o.getClassesInSignature().contains(this.range) ||
		o.getIndividualsInSignature().contains(this.domain) );
	}

	@Override
	public String toStringCorto() {
		return "Metamodeling( "+ this.getNombreDomain() +" m= " + this.getNombreRange()+" )";
	}
	
	
}
