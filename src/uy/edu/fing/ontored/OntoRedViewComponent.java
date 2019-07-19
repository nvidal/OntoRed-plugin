package uy.edu.fing.ontored;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;

import org.apache.log4j.Logger;
import org.protege.editor.owl.model.OWLModelManager;
import org.protege.editor.owl.model.event.EventType;
import org.protege.editor.owl.model.event.OWLModelManagerChangeEvent;
import org.protege.editor.owl.model.event.OWLModelManagerListener;
import org.protege.editor.owl.ui.view.AbstractOWLViewComponent;
import org.semanticweb.owlapi.model.OWLClass;
import org.semanticweb.owlapi.model.OWLIndividual;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyStorageException;

import uy.edu.fing.ontored.exceptions.ExtensionException;
import uy.edu.fing.ontored.red.Extension;
import uy.edu.fing.ontored.red.Linking;
import uy.edu.fing.ontored.red.Mapping;
import uy.edu.fing.ontored.red.Metamodeling;
import uy.edu.fing.ontored.red.Red;
import uy.edu.fing.ontored.red.Relacion;
import uy.edu.fing.ontored.utils.FactoryOWLManager;
import uy.edu.fing.ontored.utils.LanguageManager;
import uy.edu.fing.ontored.utils.RelacionTable;
import uy.edu.fing.ontored.utils.WrapperOWLClass;
import uy.edu.fing.ontored.utils.WrapperOWLIndividual;
import uy.edu.fing.ontored.utils.WrapperOWLOntology;

public class OntoRedViewComponent extends AbstractOWLViewComponent{
	private static final long serialVersionUID = -4515710047558710080L;

	private static final Logger log = Logger.getLogger(OntoRedViewComponent.class);

	private Red red;
	private	JTabbedPane tabbedPane;
	private boolean redCreada;; 
	
	// +++ MAPPING +++
	private	JPanel panelMapping;
	private JLabel jlabel_titulo_mapping;
	private JLabel jlabel_origen_mapping;
	private JComboBox<WrapperOWLClass> jcombo_origen_mapping;
	private JLabel jlabel_destino_mapping;
	private JComboBox<WrapperOWLClass> jcombo_destino_mapping;
	private JButton jbutton_crear_mapping;
	private JRadioButton jradiobutton_subclass_mapping;
	private JRadioButton jradiobutton_equivalent_mapping;
	
	// +++ LINKING +++
	private JButton jbutton_crear_linking;
	private JComboBox<WrapperOWLClass> jcombo_destino_linking;
	private JLabel jlabel_destino_linking;
	private JLabel jlabel_origen_linking;
	private JComboBox<WrapperOWLClass> jcombo_origen_linking;
	private JPanel panelLinking;
	private JLabel jlabel_titulo_linking;
	private JLabel jlabel_nombre_linking;
	private JTextField jtext_nombre_linking;
	private JCheckBox jcheck_some_linking;
	private JCheckBox jcheck_all_linking;
	private JTextField jtext_min_linking;
	private JLabel jlabel_min_linking;
	private JCheckBox jcheck_restriction_linking;
	private JTextField jtext_max_linking;
	private JLabel jlabel_max_linking;
	
	// +++ EXTENSION +++
	private JPanel panelExtension;
	private JLabel jlabel_titulo_extension;
	private JComboBox<WrapperOWLOntology> jcombo_origen_extension;
	private JComboBox<WrapperOWLOntology> jcombo_destino_extension;
	
	// +++ METAMODELLING +++
	private JButton jbutton_crear_metamodelling;
	private JPanel panelMetamodelling;
	private JLabel jlabel_titulo_metamodelling;
	private JLabel jlabel_origen_metamodelling;
	private JComboBox<WrapperOWLIndividual> jcombo_origen_metamodelling;
	private JLabel jlabel_destino_metamodelling;
	private JComboBox<WrapperOWLClass> jcombo_destino_metamodelling;

	// ++ Tab Inicial ++//
	private JPanel panelInicial;
	private JLabel jlabel_titulo_inicial;
	private JLabel jlabel_nombre;
	private JTextField jtext_nombre;
	private JLabel jlabel_archivo;
	private JTextField jtext_archivo;
	private JButton jbutton_cargar_red_inicial;
	private JButton jbutton_guardar_red_inicial;
	private JRadioButton jradiobutton_esp;
	private JRadioButton jradiobutton_eng;
	
	// TABLE RELACIONES
	private RelacionTable jtable_metamodellings;
	private RelacionTable jtable_mappings;
	private RelacionTable jtable_linkings;
	private RelacionTable jtable_extensions;


	@Override
	protected void initialiseOWLView() throws Exception {
		// ++++++++++ INICIALIZACION ++++++++++//
		// Cargo el OWLModelManager para que se accesible por el resto de las clases.
		new FactoryOWLManager(getOWLModelManager());

		//Agrego el Listener de eventos.
		getOWLModelManager().addListener(owlModelManagerListener);

		setLayout(null);
		{	
			// Create a tabbed pane
			tabbedPane = new JTabbedPane();
			tabbedPane.setVisible(true);
			tabbedPane.setBounds(0, 0, 900, 540);
			add(tabbedPane);

			//Creo las Tab para las relaciones
			createTabInicial();
			createTabMapping();
			createTabLinking();
			createTabExtension();
			createTabMeta();

			// Cargo las listas comboBox
			refreshListClasses();
			refreshListIndividuals();

			tabbedPane.addTab( LanguageManager.getInstance().getText("PanelInicial.nombre"), panelInicial);
			tabbedPane.addTab( LanguageManager.getInstance().getText("PanelMapping.nombre"), panelMapping );
			tabbedPane.addTab( LanguageManager.getInstance().getText("PanelLinking.nombre"), panelLinking );
			tabbedPane.addTab( LanguageManager.getInstance().getText("PanelExtension.nombre"), panelExtension );
			tabbedPane.addTab( LanguageManager.getInstance().getText("PanelMetamodelling.nombre"), panelMetamodelling );
		}
	}

	private OWLModelManagerListener owlModelManagerListener = new OWLModelManagerListener() {
		public void handleChange(OWLModelManagerChangeEvent event) {
			if (redCreada && red!=null){
				if (event.isType(EventType.ACTIVE_ONTOLOGY_CHANGED)) {
					// Crea las Extension que se hayan agregado
					for(OWLOntology o : getOWLModelManager().getActiveOntologies()){
						if(red.getOntologia() != o){
							Extension e = new Extension(red.getOntologia(), o, null);
							if(!red.existeRelacion(e)){
								e.setRed(red);
								red.addRelacion(e);
								jtable_extensions.addData(e);
								break;
							}
						}
					}
					//Elimino las Extension que se hayan quitado
					Set<OWLOntology> list = getOWLModelManager().getActiveOntologies();
					for (Relacion r: red.getRelaciones(Relacion.EXTENSION)){
						if (!list.contains(((Extension)r).getRange())){
							jtable_extensions.removeData(r);
							break;
						}
					}
					// Actualizo las listas
					refreshListClasses();
					refreshListIndividuals();	
				}
				if(event.isType(EventType.ONTOLOGY_LOADED)){
				}
				if(event.isType(EventType.ONTOLOGY_SAVED)){
					// Actualizo las listas
					refreshListClasses();
					refreshListIndividuals();
				}
			}
		}
	};


	/**
	 * Actualiza las listas de Clases de la distintas tabs.
	 */
	public void refreshListClasses(){
		Map<String, WrapperOWLClass> map = new HashMap<String, WrapperOWLClass>();
		for (OWLOntology o : getOWLModelManager().getActiveOntologies()){
			if(o != getOWLModelManager().getActiveOntology()){
				for (OWLClass c : o.getClassesInSignature()){
					if(!map.containsKey(c.toString()))
						map.put(c.toString(), new WrapperOWLClass(c, o));
				}
			}
		}
		// Lists Mapping
		jcombo_origen_mapping.removeAllItems();
		jcombo_destino_mapping.removeAllItems();
		// Lists Linking
		jcombo_origen_linking.removeAllItems();
		jcombo_destino_linking.removeAllItems();
		// List Metamodelling
		jcombo_destino_metamodelling.removeAllItems();
		for (WrapperOWLClass w : map.values()){
			jcombo_origen_mapping.addItem(w);
			jcombo_destino_mapping.addItem(w);
			jcombo_origen_linking.addItem(w);
			jcombo_destino_linking.addItem(w);
			jcombo_destino_metamodelling.addItem(w);
		}
	}

	/**
	 * Actualiza las listas de Individuals
	 */
	public void refreshListIndividuals(){
		Map<String, WrapperOWLIndividual> individuals = new HashMap<String, WrapperOWLIndividual>();
		for (OWLOntology o : getOWLModelManager().getActiveOntologies()){
			for (OWLIndividual i : o.getIndividualsInSignature()){
				if(!individuals.containsKey(i.toString()))
					individuals.put(i.toString(), new WrapperOWLIndividual(i, o));
			}
		}
		jcombo_origen_metamodelling.removeAllItems();
		for (WrapperOWLIndividual i: individuals.values()){
			jcombo_origen_metamodelling.addItem(i);
		}
	}


	/**
	 * Actualiza el texto segun el idioma escogido.
	 */
	public void refreshIdioma(){
		//Inicial
		jlabel_titulo_inicial.setText(LanguageManager.getInstance().getText("PanelInicial.titulo"));
		jlabel_nombre.setText(LanguageManager.getInstance().getText("PanelInicial.campoNombre"));
		jlabel_archivo.setText(LanguageManager.getInstance().getText("PanelInicial.campoArchivo"));
		jbutton_cargar_red_inicial.setText(LanguageManager.getInstance().getText("PanelInicial.botonCargar"));
		jbutton_guardar_red_inicial.setText(LanguageManager.getInstance().getText("PanelInicial.botonGuardar"));
		
		//MAPPING
		jlabel_titulo_mapping.setText(LanguageManager.getInstance().getText("PanelMapping.titulo"));
		jlabel_origen_mapping.setText(LanguageManager.getInstance().getText("PanelMapping.origen"));
		jlabel_destino_mapping.setText(LanguageManager.getInstance().getText("PanelMapping.destino"));
		jbutton_crear_mapping.setText(LanguageManager.getInstance().getText("PanelMapping.botonCrear"));
		jtable_mappings.setText(LanguageManager.getInstance().getText("PanelMapping.tituloLista"));
		
		//LINKING
		jlabel_titulo_linking.setText(LanguageManager.getInstance().getText("PanelLinking.titulo"));
		jlabel_nombre_linking.setText(LanguageManager.getInstance().getText("PanelLinking.nomRelacion"));
		jlabel_origen_linking.setText(LanguageManager.getInstance().getText("PanelLinking.origen"));
		jlabel_destino_linking.setText(LanguageManager.getInstance().getText("PanelLinking.destino"));
		jcheck_restriction_linking.setText(LanguageManager.getInstance().getText("PanelLinking.restricciones"));
		jbutton_crear_linking.setText(LanguageManager.getInstance().getText("PanelLinking.botonCrear"));
		jtable_linkings.setText(LanguageManager.getInstance().getText("PanelLinking.tituloLista"));
		
		//EXTENSION
		jlabel_titulo_extension.setText(LanguageManager.getInstance().getText("PanelExtension.titulo"));
		jtable_extensions.setText(LanguageManager.getInstance().getText("PanelExtension.tituloLista"));
		
		//METAMODELLING
		jlabel_titulo_metamodelling.setText(LanguageManager.getInstance().getText("PanelMetamodelling.titulo"));
		jlabel_origen_metamodelling.setText(LanguageManager.getInstance().getText("PanelMetamodelling.origen"));
		jlabel_destino_metamodelling.setText(LanguageManager.getInstance().getText("PanelMetamodelling.destino"));
		jbutton_crear_metamodelling.setText(LanguageManager.getInstance().getText("PanelMetamodelling.botonCrear"));
		jtable_metamodellings.setText(LanguageManager.getInstance().getText("PanelMetamodelling.tituloLista"));
	}
	
	/**
	 * Crea el Tab Inicial del plugin.
	 */
	public void createTabInicial(){
		
		panelInicial = new JPanel();
		panelInicial.setLayout( null );
		{
			jlabel_titulo_inicial = new JLabel(LanguageManager.getInstance().getText("PanelInicial.titulo"));
			jlabel_titulo_inicial.setBounds(330,5,200,20);
			jlabel_titulo_inicial.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			panelInicial.add(jlabel_titulo_inicial);

			jlabel_nombre = new JLabel(LanguageManager.getInstance().getText("PanelInicial.campoNombre"));
			jlabel_nombre.setBounds(130, 80, 120, 20);
			panelInicial.add(jlabel_nombre);

			jtext_nombre = new JTextField();
			jtext_nombre.setBounds( 250, 80, 500, 20 );
			panelInicial.add( jtext_nombre );
			
			jlabel_archivo = new JLabel(LanguageManager.getInstance().getText("PanelInicial.campoArchivo"));
			jlabel_archivo.setBounds(130, 120, 120, 20);
			panelInicial.add(jlabel_archivo);

			jtext_archivo = new JTextField();
			jtext_archivo.setBounds( 250, 120, 500, 20 );
			panelInicial.add( jtext_archivo );
			

			//IDIOMA
			jradiobutton_esp = new JRadioButton("Español",false);
			jradiobutton_esp.setBounds(300, 250, 100, 20);
			jradiobutton_esp.addActionListener(new ActionListener(){
			    public void actionPerformed(ActionEvent e) {
			        LanguageManager.getInstance();
					if(LanguageManager.cambiarIdioma(LanguageManager.ESP)){
			        	refreshIdioma();
			        }
			      }
			  });
			panelInicial.add(jradiobutton_esp);
			jradiobutton_eng = new JRadioButton("English",true);
			jradiobutton_eng.setBounds(450, 250, 100, 20);
			jradiobutton_eng.addActionListener(new ActionListener(){
			    public void actionPerformed(ActionEvent e) {
			        LanguageManager.getInstance();
					if(LanguageManager.cambiarIdioma(LanguageManager.ENG)){
			        	refreshIdioma();
			        }
			      }
			  });
			panelInicial.add(jradiobutton_eng);

			ButtonGroup bg1 = new ButtonGroup( );
			bg1.add(jradiobutton_esp);
			bg1.add(jradiobutton_eng);
			//++++++

			jbutton_cargar_red_inicial = new JButton(LanguageManager.getInstance().getText("PanelInicial.botonCargar"));
			jbutton_cargar_red_inicial.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					cargarRedInicial();
				}
			});
			jbutton_cargar_red_inicial.setBounds(150, 400, 100, 40);
			panelInicial.add(jbutton_cargar_red_inicial);
			
			jbutton_guardar_red_inicial = new JButton(LanguageManager.getInstance().getText("PanelInicial.botonGuardar"));
			jbutton_guardar_red_inicial.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					guardarRed();
				}
			});
			jbutton_guardar_red_inicial.setBounds(550, 400, 100, 40);
			panelInicial.add(jbutton_guardar_red_inicial);
		}
	}

	public void refreshTabInicial(String nombre, String archivo){
		jtext_archivo.setText(archivo);
		jtext_archivo.setEditable(false);
		jtext_nombre.setText(nombre);
		jtext_nombre.setEditable(false);
		
		jbutton_cargar_red_inicial.setEnabled(false);
		
	}
	
	
	public boolean cargarRedInicial(){
		
		OWLOntology onto = getOWLModelManager().getActiveOntology();
		if (onto==null){
			JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noHayOnto"));
			return false;
		}
		String archivo = getOWLModelManager().getOntologyPhysicalURI(onto).getPath();
		if (archivo == null || archivo.isEmpty()){
			JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.cargarOnto"));
			return false;
		}
		
		this.red = new Red(onto);
		this.redCreada = true;
		refreshRelaciones();
		
        refreshTabInicial(this.red.getNombre(), archivo);
		
        return true;
	}
	
	public boolean guardarRed(){
		if (!this.redCreada){
			JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noRed"));
			return false;
		}

        OWLModelManager manager = this.getOWLModelManager();
        if (manager.isDirty()){
	        try {
				manager.save(this.red.getOntologia());
			} catch (OWLOntologyStorageException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.errorGuardarRed"));
				return false;
			}
	        String archivo = manager.getOntologyPhysicalURI(this.red.getOntologia()).getPath();
	        if(!jtext_archivo.equals(archivo)){
	        	jtext_archivo.setText(archivo);
	        }
        }
        if (!manager.isDirty()){
        	JOptionPane.showMessageDialog(null,LanguageManager.getInstance().getText("Mensajes.exitoGuardarRed"));
        }
		return true;
	}
		
	public void refreshRelaciones(){
		if(this.red!=null){
			jtable_extensions.addData(this.red.getRelaciones(Relacion.EXTENSION));
			jtable_mappings.addData(this.red.getRelaciones(Relacion.MAPPING));
			jtable_metamodellings.addData(this.red.getRelaciones(Relacion.METAMODELING));
			jtable_linkings.addData(this.red.getRelaciones(Relacion.LINKING));
		}
	}


	//++ TAB de MAPPING ++
	public void createTabMapping()
	{
		panelMapping = new JPanel();
		panelMapping.setLayout( null );
		{
			jlabel_titulo_mapping = new JLabel(LanguageManager.getInstance().getText("PanelMapping.titulo"));
			jlabel_titulo_mapping.setBounds(375, 5, 150, 20);
			jlabel_titulo_mapping.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			panelMapping.add(jlabel_titulo_mapping);

			jlabel_origen_mapping = new JLabel(LanguageManager.getInstance().getText("PanelMapping.origen"));
			jlabel_origen_mapping.setBounds(95, 50, 80, 20);
			panelMapping.add(jlabel_origen_mapping);

			jcombo_origen_mapping = new JComboBox<WrapperOWLClass>();
			jcombo_origen_mapping.setBounds(175, 50, 180, 20);
			panelMapping.add(jcombo_origen_mapping);

			jlabel_destino_mapping = new JLabel(LanguageManager.getInstance().getText("PanelMapping.destino"));
			jlabel_destino_mapping.setBounds(545, 50, 80, 20);
			panelMapping.add(jlabel_destino_mapping);

			jcombo_destino_mapping = new JComboBox<WrapperOWLClass>();
			jcombo_destino_mapping.setBounds(625, 50,180,20);
			panelMapping.add(jcombo_destino_mapping);

			jradiobutton_subclass_mapping = new JRadioButton("SubClass",true);
			jradiobutton_subclass_mapping.setBounds(410, 80, 80, 20);
			panelMapping.add(jradiobutton_subclass_mapping);
			jradiobutton_equivalent_mapping = new JRadioButton("Equivalent",false);
			jradiobutton_equivalent_mapping.setBounds(410, 100, 80, 20);
			panelMapping.add(jradiobutton_equivalent_mapping);

			ButtonGroup bg1 = new ButtonGroup( );
			bg1.add(jradiobutton_subclass_mapping);
			bg1.add(jradiobutton_equivalent_mapping);

			
			
			jbutton_crear_mapping = new JButton(LanguageManager.getInstance().getText("PanelMapping.botonCrear"));
			jbutton_crear_mapping.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if(!redCreada){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noRed"));//"No existe una red creada");
						return;
					}
					if(addMapping()){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.relacionCreada"));//"Relacion creada con exito");
						jcombo_origen_mapping.setSelectedIndex(0);
						jcombo_destino_mapping.setSelectedIndex(0);
					}
				}
			});
			jbutton_crear_mapping.setBounds(375, 150, 150, 40);
			panelMapping.add(jbutton_crear_mapping);
			
			
			// LISTA DE MAPPINGS
			jtable_mappings = new RelacionTable(LanguageManager.getInstance().getText("PanelMapping.tituloLista"));
			jtable_mappings.setBounds(150, 300, 600, 150);
			panelMapping.add(jtable_mappings);
			//++++++++++++++++
		}	
	}

	public boolean addMapping(){
		try{
			if(jcombo_origen_mapping.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noClaseDomino"));
				return false;
			}
			if(jcombo_destino_mapping.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noClaseRango"));
				return false;
			}

			// Obtengo las clases
			WrapperOWLClass domain = (WrapperOWLClass) jcombo_origen_mapping.getSelectedItem();
			WrapperOWLClass range = (WrapperOWLClass) jcombo_destino_mapping.getSelectedItem();
			// Compruebo que sean de diferentes ontologias
			if(domain.getOntology() == range.getOntology()){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.clasesMismaOnto"));
				return false;
			}

			Relacion m = null;
			if(jradiobutton_subclass_mapping.isSelected()){
				//Creo la relacion Mapping SUBCLASS
				m = new Mapping(domain.getClase(), range.getClase(), Mapping.TIPO_SUB_CLASE, null);
			}
			else{
				//Creo la relacion Mapping EQUIVALENT
				m = new Mapping(domain.getClase(), range.getClase(), Mapping.TIPO_EQUIVALANTE, null);
			}
			if (this.red.existeRelacion(m)){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.existeRelacion"));
				return false;
			}
			m.setRed(this.red);
			m.save();
			this.red.addRelacion(m);
			//Agrego la relacion a la lista
			jtable_mappings.addData(m);
			return true;
		}
		catch(Exception ex){
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Error: "+ ex.toString());
			return false;
		}

	}
	//++++++++++++++++++++


	//++ TAB de LINKING ++
	public void createTabLinking()
	{
		panelLinking = new JPanel();
		panelLinking.setLayout( null );
		{
			jlabel_titulo_linking = new JLabel(LanguageManager.getInstance().getText("PanelLinking.titulo"));
			jlabel_titulo_linking.setBounds(375, 5, 150, 20);
			jlabel_titulo_linking.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			panelLinking.add(jlabel_titulo_linking);

			jlabel_nombre_linking = new JLabel(LanguageManager.getInstance().getText("PanelLinking.nomRelacion"));
			jlabel_nombre_linking.setBounds(305, 50, 120, 20);
			panelLinking.add(jlabel_nombre_linking);

			jtext_nombre_linking = new JTextField();
			jtext_nombre_linking.setBounds( 445, 50, 150, 20 );
			panelLinking.add( jtext_nombre_linking);

			jlabel_origen_linking = new JLabel(LanguageManager.getInstance().getText("PanelLinking.origen"));
			jlabel_origen_linking.setBounds(95, 80, 80, 20);
			panelLinking.add(jlabel_origen_linking);

			jcombo_origen_linking = new JComboBox<WrapperOWLClass>();
			jcombo_origen_linking.setBounds(175, 80, 180, 20);
			panelLinking.add(jcombo_origen_linking);

			jlabel_destino_linking = new JLabel(LanguageManager.getInstance().getText("PanelLinking.destino"));
			jlabel_destino_linking.setBounds(545, 80, 80, 20);
			panelLinking.add(jlabel_destino_linking);

			jcombo_destino_linking = new JComboBox<WrapperOWLClass>();
			jcombo_destino_linking.setBounds(625,80,180,20);
			panelLinking.add(jcombo_destino_linking);


			//++ Restrictions ++//
			jcheck_some_linking = new JCheckBox("SomeValues");
			jcheck_some_linking.setSelected(false);
			jcheck_some_linking.setBounds(360, 200, 100, 20);
			panelLinking.add(jcheck_some_linking);

			jcheck_all_linking = new JCheckBox("AllValues");
			jcheck_all_linking.setSelected(false);
			jcheck_all_linking.setBounds(480, 200, 100, 20);
			panelLinking.add(jcheck_all_linking);

			jtext_min_linking = new JTextField();
			jtext_min_linking.setBounds(400, 160, 30, 20);
			jtext_min_linking.setEnabled(false);
			panelLinking.add(jtext_min_linking);

			jlabel_min_linking = new JLabel("Min");
			jlabel_min_linking.setBounds(370, 160, 30, 20);
			panelLinking.add(jlabel_min_linking);

			jtext_max_linking = new JTextField();
			jtext_max_linking.setBounds(500, 160, 30, 20);
			jtext_max_linking.setEnabled(false);
			panelLinking.add(jtext_max_linking);

			jlabel_max_linking = new JLabel("Max");
			jlabel_max_linking.setBounds(470, 160, 30, 20);
			panelLinking.add(jlabel_max_linking);

			jcheck_restriction_linking = new JCheckBox(LanguageManager.getInstance().getText("PanelLinking.restricciones"));
			jcheck_restriction_linking.setSelected(false);
			jcheck_restriction_linking.setBounds(410, 130, 140, 20);
			jcheck_restriction_linking.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent arg0) {
					if (jcheck_restriction_linking.isSelected()) {
						jtext_max_linking.setEnabled(true);
						jtext_min_linking.setEnabled(true);
					}
					else{
						jtext_max_linking.setEnabled(false);
						jtext_min_linking.setEnabled(false);
					}
				}
			});
			panelLinking.add(jcheck_restriction_linking);
			//++++++++
			
			jbutton_crear_linking = new JButton(LanguageManager.getInstance().getText("PanelLinking.botonCrear"));
			jbutton_crear_linking.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					if(!redCreada){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noRed"));
						return;
					}
					if(addLinking()){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.relacionCreada"));
						jtext_nombre_linking.setText("");
						jcombo_origen_linking.setSelectedIndex(0);
						jcombo_destino_linking.setSelectedIndex(0);
						//refresh restrictions
						jcheck_restriction_linking.setSelected(false);
						jcheck_some_linking.setSelected(false);
						jcheck_all_linking.setSelected(false);
						jtext_max_linking.setText("");
						jtext_max_linking.setEnabled(false);
						jtext_min_linking.setText("");
						jtext_min_linking.setEnabled(false);
					}
				}
			});
			jbutton_crear_linking.setBounds(375, 250, 150, 40);
			panelLinking.add(jbutton_crear_linking);
			
			
			// LISTA DE LINKINGS
			jtable_linkings = new RelacionTable(LanguageManager.getInstance().getText("PanelLinking.tituloLista"));
			jtable_linkings.setBounds(150, 340, 600, 150);
			panelLinking.add(jtable_linkings);
			//++++++++++++++++
		}
	}

	public boolean addLinking(){
		try{
			if(jtext_nombre_linking.getText() == null || jtext_nombre_linking.getText().isEmpty()){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("PanelLinking.noNombreRel"));
				return false;
			}
			if(jcombo_origen_linking.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noClaseDomino"));
				return false;
			}
			if(jcombo_destino_linking.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noClaseRango"));
				return false;
			}
			int min =-1;
			int max =-1;
			if(jcheck_restriction_linking.isSelected()){
				if (jtext_max_linking.getText().trim() =="" && jtext_min_linking.getText().trim()==""){
					JOptionPane.showMessageDialog(null,LanguageManager.getInstance().getText("PanelLinking.noRestricciones"));
					return false;
				}
				else{
					try {min = Integer.parseInt(jtext_min_linking.getText().trim()); 
					}catch (NumberFormatException nfe){min=-1;
					}
					try {max = Integer.parseInt(jtext_max_linking.getText().trim());
					} catch (NumberFormatException nfe){max=-1;
					} 
				}
			}

			String nombre = jtext_nombre_linking.getText();

			// Obtengo las clases
			WrapperOWLClass domain = (WrapperOWLClass) jcombo_origen_linking.getSelectedItem();
			WrapperOWLClass range = (WrapperOWLClass) jcombo_destino_linking.getSelectedItem();
			// Compruebo que sean de diferentes ontologias

			if(domain.getOntology() == range.getOntology()){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.clasesMismaOnto"));
				return false;
			}

			boolean all = this.jcheck_all_linking.isSelected();
			boolean some = this.jcheck_some_linking.isSelected();

			Relacion l = null;
			l = new Linking("Link:"+nombre, domain.getClase(),range.getClase(), null, min, max, some, all);
			if (this.red.existeRelacion(l)){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.existeRelacion"));
				return false;
			}
			l.setRed(this.red);
			l.save();
			this.red.addRelacion(l);
			//Agrego la relacion a la lista
			jtable_linkings.addData(l);
			
			return true;
		}
		catch(Exception ex){
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Error: "+ ex.toString());
			return false;
		}

	}
	//++++++++++++++++++++

	//++ TAB de extension ++
	public void createTabExtension()
	{
		panelExtension = new JPanel();
		panelExtension.setLayout( null );
		{
			jlabel_titulo_extension = new JLabel(LanguageManager.getInstance().getText("PanelExtension.titulo"));
			jlabel_titulo_extension.setBounds(375, 5, 150, 20);
			jlabel_titulo_extension.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			panelExtension.add(jlabel_titulo_extension);
		
			//LISTA DE EXTENSIONS
			jtable_extensions = new RelacionTable(LanguageManager.getInstance().getText("PanelExtension.tituloLista"));
			jtable_extensions.setBounds(150, 50/*300*/, 600, 150);
			panelExtension.add(jtable_extensions);
			//++++++++++++++++
		}
	}

	/**
	 * No se utiliza.
	 */
	public boolean addExtension(){
		try{
			if(jcombo_origen_extension.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noOntoDomino"));
				return false;
			}
			if(jcombo_destino_linking.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noOntoRango"));
				return false;
			}

			// Obtengo las ontologias
			WrapperOWLOntology domain = (WrapperOWLOntology) jcombo_origen_extension.getSelectedItem();
			WrapperOWLOntology range = (WrapperOWLOntology) jcombo_destino_extension.getSelectedItem();
			// Compruebo que sean diferentes ontologias
			if(domain.getOntology() == range.getOntology()){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noOntoDiferentes"));
				return false;
			}

			Relacion e = null;
			e = new Extension(domain.getOntology(), range.getOntology(), null);
			if (this.red.existeRelacion(e)){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.existeRelacion"));
				return false;
			}
			e.setRed(this.red);
			e.save();
			this.red.addRelacion(e);
			//Agrego la relacion a la lista
			jtable_extensions.addData(e);
			
			return true;

		}catch(ExtensionException ex){
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(null, ex.getMessage());
			return false;
		}catch(Exception ex){
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Error: "+ ex.toString());
			return false;
		}
	}
	//++++++++++++++++++++

	// +++ TAB de MetaModelling +++
	public void createTabMeta()
	{
		panelMetamodelling = new JPanel();
		panelMetamodelling.setLayout( null );
		{
			jlabel_titulo_metamodelling = new JLabel(LanguageManager.getInstance().getText("PanelMetamodelling.titulo"));
			jlabel_titulo_metamodelling.setBounds(350, 5, 200, 20);
			jlabel_titulo_metamodelling.setFont(new Font("Lucida Grande", Font.BOLD, 16));
			panelMetamodelling.add(jlabel_titulo_metamodelling);

			jlabel_origen_metamodelling = new JLabel(LanguageManager.getInstance().getText("PanelMetamodelling.origen"));
			jlabel_origen_metamodelling.setBounds(80, 50, 100, 20);
			panelMetamodelling.add(jlabel_origen_metamodelling);

			jcombo_origen_metamodelling = new JComboBox<WrapperOWLIndividual>();
			jcombo_origen_metamodelling.setBounds(180, 50, 200, 20);
			panelMetamodelling.add(jcombo_origen_metamodelling);

			jlabel_destino_metamodelling = new JLabel(LanguageManager.getInstance().getText("PanelMetamodelling.destino"));
			jlabel_destino_metamodelling.setBounds(420, 50, 100, 20);
			panelMetamodelling.add(jlabel_destino_metamodelling);

			jcombo_destino_metamodelling = new JComboBox<WrapperOWLClass>();
			jcombo_destino_metamodelling.setBounds(520, 50,200,20);
			panelMetamodelling.add(jcombo_destino_metamodelling);

			jbutton_crear_metamodelling = new JButton(LanguageManager.getInstance().getText("PanelMetamodelling.botonCrear"));
			jbutton_crear_metamodelling.addActionListener(new ActionListener() {

				public void actionPerformed(ActionEvent arg0) {
					if(!redCreada){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noRed"));
						return;
					}
					if(addMetamodelling()){
						JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.relacionCreada"));
						jcombo_origen_metamodelling.setSelectedIndex(0);
						jcombo_destino_metamodelling.setSelectedIndex(0);
					}
				}
			});
			jbutton_crear_metamodelling.setBounds(375, 150, 150, 40);
			panelMetamodelling.add(jbutton_crear_metamodelling);
			
			
			
			//LISTA DE METAMODELLINGS
			jtable_metamodellings = new RelacionTable(LanguageManager.getInstance().getText("PanelMetamodelling.tituloLista"));
			jtable_metamodellings.setBounds(150, 300, 600, 150);
			panelMetamodelling.add(jtable_metamodellings);
			//++++++++++++++++

		}
	}

	public boolean addMetamodelling(){
		try{
			if(jcombo_origen_metamodelling.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noInstanciaDominio"));
				return false;
			}
			if(jcombo_destino_metamodelling.getSelectedIndex()==-1){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.noClaseRango"));
				return false;
			}

			WrapperOWLIndividual domain = (WrapperOWLIndividual) jcombo_origen_metamodelling.getSelectedItem();
			WrapperOWLClass range = (WrapperOWLClass) jcombo_destino_metamodelling.getSelectedItem();
//			// Compruebo que sean diferentes ontologias
//			if(domain.getOntology() == range.getOntology()){
//				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.mismaOnto"));
//				return false;
//			}

			Relacion mm = null;
			mm = new Metamodeling(domain.getIndividual(), range.getClase(), null);
			if (this.red.existeRelacion(mm)){
				JOptionPane.showMessageDialog(null, LanguageManager.getInstance().getText("Mensajes.existeRelacion"));
				return false;
			}
			mm.setRed(this.red);
			mm.save();
			this.red.addRelacion(mm);
			//Agrego la relacion a la lista
			jtable_metamodellings.addData(mm);
			return true;

		}catch(Exception ex){
			log.error(ex.getMessage());
			JOptionPane.showMessageDialog(null, "Error: "+ ex.toString());
			return false;
		}
	}

	//++++++++++++++++++++

	@Override
	protected void disposeOWLView() {
		getOWLModelManager().removeListener(owlModelManagerListener);
		log.info("[Disposed Example View]");
	}

	



}