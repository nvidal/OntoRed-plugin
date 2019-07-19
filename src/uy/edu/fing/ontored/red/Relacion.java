package uy.edu.fing.ontored.red;

import org.semanticweb.owlapi.model.OWLOntology;

import uy.edu.fing.ontored.exceptions.EliminarException;
import uy.edu.fing.ontored.exceptions.ExtensionException;



public interface Relacion{

	public static final int MAPPING = 0;
	public static final int LINKING = 1;
	public static final int EXTENSION = 2;
	public static final int METAMODELING = 3;
	
	/**
	 * @throws ExtensionException 
	 * 
	 * Crea el axioma/relacion y lo graba en la red
	 */
	public void save() throws ExtensionException;
	
	
	/**
	 * @throws OntologyNetworkException
	 * 
	 * Elimina el axioma/relacion de la red 
	 */
	public void delete() throws EliminarException;
	
	public String toString();
	
	public int getTipoRelacion();
	
	public void setRed(Red red);
	
	public boolean equals(Object o);

	public boolean relacionConOntologia(OWLOntology o);

	public String toStringCorto();
}
