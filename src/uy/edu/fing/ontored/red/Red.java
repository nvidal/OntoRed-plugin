package uy.edu.fing.ontored.red;



import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.semanticweb.owlapi.model.AxiomType;
import org.semanticweb.owlapi.model.ClassExpressionType;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLClassExpression;
import org.semanticweb.owlapi.model.OWLEquivalentClassesAxiom;
import org.semanticweb.owlapi.model.OWLMetamodellingAxiom;
import org.semanticweb.owlapi.model.OWLObjectMaxCardinality;
import org.semanticweb.owlapi.model.OWLObjectMinCardinality;
import org.semanticweb.owlapi.model.OWLObjectProperty;
import org.semanticweb.owlapi.model.OWLObjectPropertyDomainAxiom;
import org.semanticweb.owlapi.model.OWLObjectPropertyRangeAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLSubClassOfAxiom;


public class Red implements Serializable{
	
	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(Red.class);
	
	private OWLOntology ontologia;
	private List<Relacion> relaciones;
	private String nombre;
	
	/**
	 * Constructor de la Red apartir de la ontologia
	 * Crea y carga las relaciones de la red a partir de la ontologia.
	 * @param ontologia
	 * @param archivo
	 */
	public Red(OWLOntology ontologia) {
		super();

		this.ontologia = ontologia;
		this.nombre = getNombreOntology();
		//this.setSaved(true);
		log.info("INIT OntoRed-Network: ["+nombre+"] ["+ontologia+"] ");
		
		// ------------------------------ //
		// CARGO LAS RELACIONES DE LA RED //
		// ------------------------------ //
		this.relaciones = new ArrayList<Relacion>();
		
		// ----------- //
		// EXTENSIONS //
		for (OWLOntology i : this.ontologia.getImports()){
			Extension e = new Extension(this.ontologia, i, this);
			this.relaciones.add(e);
			log.info("[Add]:"+e.toString());
		}
		
		// --------- //
		// LINKINGS //
		for (OWLObjectProperty o : this.ontologia.getObjectPropertiesInSignature()){
			String nomLink = o.getIRI().toString();
			nomLink = nomLink.substring(nomLink.lastIndexOf("#")+1, nomLink.length());
			if (nomLink.startsWith("Link:")){
				int min=-1, max = -1;
				boolean some = false, all = false;
				OWLClass range =null, domain = null;
				for(OWLObjectPropertyDomainAxiom d : this.ontologia.getObjectPropertyDomainAxioms(o)){
					domain = d.getDomain().asOWLClass();
				}
				for(OWLObjectPropertyRangeAxiom r : this.ontologia.getObjectPropertyRangeAxioms(o)){
					OWLClassExpression c = r.getRange();
					if(!c.isAnonymous())
						range = c.asOWLClass();
					else{
						if (c.getClassesInSignature().size()>0){
							range = (OWLClass) c.getClassesInSignature().toArray()[0];
						}
					}
					if(c.getClassExpressionType() == ClassExpressionType.OBJECT_ALL_VALUES_FROM){
						all = true;
					}
					else if(c.getClassExpressionType() == ClassExpressionType.OBJECT_SOME_VALUES_FROM){
						some = true;
					}
					else if(c.getClassExpressionType() == ClassExpressionType.OBJECT_MAX_CARDINALITY){
						max =( (OWLObjectMaxCardinality) c).getCardinality();
					}
					else if(c.getClassExpressionType() == ClassExpressionType.OBJECT_MIN_CARDINALITY){
						min =( (OWLObjectMinCardinality) c).getCardinality();
					}
					 
				}
				if(domain != null && range !=null){
					Linking l = new Linking(nomLink, domain, range, this, min, max, some, all);
					this.relaciones.add(l);
					log.info("[Add]:"+l.toString());
				}
			}
		}
		
		// -------- //
		// MAPPINGS //
		for (OWLSubClassOfAxiom s: this.ontologia.getAxioms(AxiomType.SUBCLASS_OF)){
			OWLClassExpression sub = s.getSubClass();
			OWLClassExpression supr = s.getSuperClass();
			if(!getNombreOntologia(sub.toString()).equals(getNombreOntologia(supr.toString()))){ // si son clases de distintas ontologias => MAP subclass
				Mapping m = new Mapping(sub.asOWLClass(), supr.asOWLClass(), Mapping.TIPO_SUB_CLASE, this);
				this.relaciones.add(m);
				log.info("[Add]:"+m.toString());
			}
		}
		for (OWLEquivalentClassesAxiom e: this.ontologia.getAxioms(AxiomType.EQUIVALENT_CLASSES)){
			Set<OWLClassExpression> setClases  = e.getClassExpressions();
			if(setClases.size() == 2){
				OWLClassExpression c1 = (OWLClassExpression)setClases.toArray()[0];
				OWLClassExpression c2 = (OWLClassExpression)setClases.toArray()[1];
				if(!getNombreOntologia(c1.toString()).equals(getNombreOntologia(c2.toString()))){
					Mapping m = new Mapping(c1.asOWLClass(), c2.asOWLClass(), Mapping.TIPO_EQUIVALANTE, this);
					this.relaciones.add(m);
					log.info("[Add]:"+m.toString());
				}
			}
		}
		
		// --------------- //
		// METAMODELINGS //
		for (OWLMetamodellingAxiom ma: this.ontologia.getAxioms(AxiomType.METAMODELLING)){
			Metamodeling m = new Metamodeling(ma.getMetamodelIndividual(), ma.getModelClass().asOWLClass(), this);
			this.relaciones.add(m);
			log.info("[Add]:"+m.toString());
		}
	}
	
	protected String getNombreOntologia(String iri){
		return iri.substring(0,iri.lastIndexOf("#")+1);
	}
	
	public void save(){
		
	}
	
	/**
	 *  se asume precargada la ubicacion del archivo y ontologia.
	 */
	public void load(){

	}
	
	public String getNombre() {
		return nombre;
	}
	
	public void setNombre(String nombre) {
		//this.setSaved(false);
		this.nombre = nombre;
	}
	
	public OWLOntology getOntologia() {
		return ontologia;
	}
	
	public void setOntologia(OWLOntology ontologia) {
		this.ontologia = ontologia;
	}
	
	public List<Relacion> getRelaciones() {
		return relaciones;
	}
	
	/**
	 * Devuelve las relaciones del tipo "tipo".
	 * @param metamodelling
	 * @return
	 */
	public List<Relacion> getRelaciones(int tipo) {
		List<Relacion> res = new ArrayList<Relacion>();
		for(Relacion r: relaciones){
			if (r.getTipoRelacion() == tipo)
				res.add(r);
		}
		return res;
	}
	
	public void setRelaciones(List<Relacion> relaciones) {
		this.relaciones = relaciones;
	}

	public void addRelacion(Relacion r) {
		this.relaciones.add(r);
	}
	
	public void removeRelacion(Relacion r) {
		this.relaciones.remove(r);
	}

	public String getNombreOntology(){
		return this.ontologia.getOntologyID().getOntologyIRI().toString();
	}
	
	public boolean existeRelacion(Relacion r){
		boolean existe = false;
		for (Relacion rel: this.relaciones){
			if (rel.equals(r)){
				existe = true;
				break;
			}
		}
		return existe;
	}
	
	public boolean ontologiaRelacionada(OWLOntology o){
		if(this.ontologia == o)
			return true;
		boolean relacion = false;
		for(Relacion r : this.relaciones){
			if(r.getTipoRelacion()!= Relacion.EXTENSION && r.relacionConOntologia(o)){
				relacion = true;
				break;
			}
		}
		return relacion;
	}
}
