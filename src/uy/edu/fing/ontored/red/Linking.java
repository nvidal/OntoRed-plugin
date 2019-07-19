package uy.edu.fing.ontored.red;



import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLDataFactory;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;

import uy.edu.fing.ontored.utils.FactoryOWLManager;
import uy.edu.fing.ontored.utils.LanguageManager;



public class Linking implements Relacion, Serializable{
	
	private static final long serialVersionUID = 1L;
	//Logger
	private static final Logger log = Logger.getLogger(Linking.class);
	
	private String nombre;
	private OWLClass domain;
	private OWLClass range;
	private Red red;
	//Restricciones
	private int minCard;
	private int maxCard;
	private boolean someValuesFrom;
	private boolean allValuesFrom;
	
	/**
	 * Constructor de la relacion Linking.
	 * Agrega el prefijo "Link:" al nombre de la relacion.
	 */
	public Linking(String nombre, OWLClass domain, OWLClass range, Red red, int min, int max, boolean some, boolean all) {
		super();
		this.nombre = nombre;
		this.domain = domain;
		this.range = range;
		this.red = red;
		this.allValuesFrom = all;
		this.someValuesFrom = some;
		this.minCard = min;
		this.maxCard = max;
		//log.info("[Created]:" + this.toString());
	}
	
	
	@Override
	public void save() {
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
		IRI ontoIRI = this.red.getOntologia().getOntologyID().getOntologyIRI();
    	OWLDataFactory factory = manager.getOWLDataFactory();
    	Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
    	//Creo la nueva propiedad-LINKING/ObjectProperty-
		OWLObjectProperty prop = factory.getOWLObjectProperty(IRI.create(ontoIRI + "#" + this.nombre));
		//Defino el Dominio
		axioms.add(factory.getOWLObjectPropertyDomainAxiom(prop, this.domain));
		
		//++++ RESTRICCIONES ++++
		boolean sinRestricciones = true;
        if(this.someValuesFrom){
        	sinRestricciones = false;
        	OWLClassExpression propSome = factory.getOWLObjectSomeValuesFrom(prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propSome));
        }
        if(this.allValuesFrom){
        	sinRestricciones = false;
        	OWLClassExpression propAll = factory.getOWLObjectAllValuesFrom(prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propAll));
        }
        if(this.minCard>-1){
        	sinRestricciones = false;
        	OWLClassExpression propMinCard = factory.getOWLObjectMinCardinality(minCard, prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propMinCard));
        }
        if(this.maxCard>-1){
        	sinRestricciones = false;
        	OWLClassExpression propMaxCard = factory.getOWLObjectMaxCardinality(maxCard, prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propMaxCard));
        }    
        
        //Defino el Rango
        if(sinRestricciones){
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, this.range));
        }
      	//Agrego el axioma "Linking" a la red.
		manager.addAxioms(this.red.getOntologia(), axioms);
	}
	
	@Override
	public void delete() {
		OWLOntologyManager manager = FactoryOWLManager.getOWLManager().getOWLOntologyManager();
		OWLDataFactory factory = manager.getOWLDataFactory();
		IRI ontoIRI = this.red.getOntologia().getOntologyID().getOntologyIRI();
		Set<OWLAxiom> axioms = new HashSet<OWLAxiom>();
		//Obtengo la propiead -LINKING-
		OWLObjectProperty prop = factory.getOWLObjectProperty(IRI.create(ontoIRI + "#" + this.nombre));
		//Obtengo el Axioma de dominio
		axioms.add(factory.getOWLObjectPropertyDomainAxiom(prop, this.domain));
		//Obtengo los Axiomas de rango
		boolean sinRestricciones = true;
		if(this.someValuesFrom){
        	sinRestricciones = false;
        	OWLClassExpression propSome = factory.getOWLObjectSomeValuesFrom(prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propSome));
        }
        if(this.allValuesFrom){
        	sinRestricciones = false;
        	OWLClassExpression propAll = factory.getOWLObjectAllValuesFrom(prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propAll));
        }
        if(this.minCard>-1){
        	sinRestricciones = false;
        	OWLClassExpression propMinCard = factory.getOWLObjectMinCardinality(minCard, prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propMinCard));
        }
        if(this.maxCard>-1){
        	sinRestricciones = false;
        	OWLClassExpression propMaxCard = factory.getOWLObjectMaxCardinality(maxCard, prop, range);
        	axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, propMaxCard));
        }
        if(sinRestricciones){
    		axioms.add(factory.getOWLObjectPropertyRangeAxiom(prop, this.range));
        }
        // Elimino la relacion Linking de la red
		manager.removeAxioms(this.red.getOntologia(), axioms);
		this.red.removeRelacion(this);
	}
	
	@Override
	public String toString(){
			String res;
			
			if (LanguageManager.getInstance().getIdioma() == LanguageManager.ESP)
				res = "Linking: [" + this.getNombre() +": "+ this.getNombreDomain() +" relacionada con: " + this.getNombreRange();
			else{
				res = "Linking: [" + this.getNombre() +": "+ this.getNombreDomain() +" related to: " + this.getNombreRange();
			}
				
				
			res = res.concat(" - [");
			if(this.minCard>-1)
				res = res.concat(" Min: "+this.minCard);
			if(this.maxCard>-1)
				res = res.concat(" Max: "+this.maxCard);
			if(this.someValuesFrom)
				res = res.concat(" Some ");
			if(this.allValuesFrom)
				res = res.concat(" Only ");
			res = res.concat("] ]");
			return res;
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
		return Relacion.LINKING;
	}
	
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	public Red getRed() {
		return red;
	}
	public void setRed(Red red) {
		this.red = red;
	}
	
	public boolean equals(Object o) {
	    if ( this == o ) 
	    	return true;
	    if ( !(o instanceof Linking) ) 
	    	return false;
	    Linking l = (Linking)o;
	    
	    return ( this.domain == l.getDomain() &&
	    		this.range == l.getRange() &&
	    		this.nombre.equals(l.getNombre()) );
	}


	@Override
	public boolean relacionConOntologia(OWLOntology o) {
		return ( o.getClassesInSignature().contains(this.domain) ||
				o.getClassesInSignature().contains(this.range) );
	}


	@Override
	public String toStringCorto() {
		String res = "Linking (" + this.getNombre() +"( "+ this.getNombreDomain() +", " + this.getNombreRange()+" ))";
		return res;
	}

	
	
}
