package uy.edu.fing.ontored.utils;

import java.util.List;

import javax.swing.table.DefaultTableModel;
import uy.edu.fing.ontored.red.Relacion;

public class RelacionTableModel extends DefaultTableModel{
	
	private static final long serialVersionUID = 1L;

	public void addRow(Relacion r){
		if (r!=null){
			Object [] fila = new Object[1];
			fila[0] = r.toString();
			addRow(fila);
		}
	}
	
	public void addRows(List<Relacion> rels){
		if (rels != null){
			for (Relacion r: rels){
				addRow(r);
			}
		}
	}
	
	public void clear(){
		setRowCount(0);
	}
	

}
