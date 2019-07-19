package uy.edu.fing.ontored.utils;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import uy.edu.fing.ontored.exceptions.EliminarException;
import uy.edu.fing.ontored.red.Relacion;

public class RelacionTable extends JPanel{//JFrame{

	private static final long serialVersionUID = 1L;
	private	JTable		table;
	private	JScrollPane scrollPane;
	private RelacionTableModel modelo;
	private List<Relacion> relacionesEnModelo;

	public void setText(String text){
		//modelo.
	}
	public RelacionTable(String titulo){

		relacionesEnModelo = new ArrayList<Relacion>();
		modelo = new RelacionTableModel();
		modelo.addColumn(titulo);

		setLayout( new BorderLayout() );

		table = new JTable( modelo );
		table.addMouseListener(new MouseAdapter() 
		{
			public void mouseClicked(MouseEvent e) 
			{
				int fila = table.rowAtPoint(e.getPoint());
				int columna = table.columnAtPoint(e.getPoint());
				// No elimino las relaciones tipo EXTENSION
				if ((fila > -1) && (columna > -1) && (relacionesEnModelo.get(fila).getTipoRelacion()!= Relacion.EXTENSION)){
					String mensaje, titulo;
					if ( LanguageManager.getInstance().getIdioma()== LanguageManager.ESP){
						mensaje = "¿Desea eliminar la relacion "+ relacionesEnModelo.get(fila).toStringCorto()+"?";
						titulo = "Eliminar relacion";
					}
					else{
						mensaje = "Do you want to delete relation " + relacionesEnModelo.get(fila).toStringCorto()+"?";
						titulo = "Delete relation";
					}
					int result = JOptionPane.showConfirmDialog(null, mensaje, titulo, JOptionPane.OK_CANCEL_OPTION);
					if(result==0){

						removeData(fila);
					}
				}
			}
		});

		table.setShowHorizontalLines( false );
		table.setRowSelectionAllowed( true );
		table.setColumnSelectionAllowed( true );


		table.setSelectionForeground( Color.white );
		table.setSelectionBackground( Color.gray );

		scrollPane = new JScrollPane(table);
		add( scrollPane, BorderLayout.CENTER );
	}

	/**
	 * Limpia toda la tabla y agrega las nuevas relaciones
	 * @param relaciones
	 */
	public void addData(List<Relacion> relaciones)
	{
		if(relacionesEnModelo==null){
			relacionesEnModelo = new ArrayList<Relacion>();
		}
		relacionesEnModelo.clear();
		relacionesEnModelo= relaciones;

		modelo.clear();
		modelo.addRows(relaciones);
	}
	/**
	 * Agrega la relacion al modelo y a la lista de relaciones.
	 * @param r
	 */
	public void addData(Relacion r){
		relacionesEnModelo.add(r);
		modelo.addRow(r);
	}

	public void removeData(int index){
		Relacion r = relacionesEnModelo.get(index);
		try {
			r.delete();
		} catch (EliminarException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return;
		}
		relacionesEnModelo.remove(index);
		modelo.removeRow(index);
	}


	public void removeData(Relacion r){
		int index = relacionesEnModelo.indexOf(r);
		try {
			r.delete();
		} catch (EliminarException e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
			return;
		}
		relacionesEnModelo.remove(index);
		modelo.removeRow(index);
	}

}
