package com.otn.tool.common.mvc;

import java.awt.event.MouseEvent;
import java.util.HashMap;

import javax.swing.RowFilter;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import fi.mmm.yhteinen.swing.core.component.table.YColumn;
import fi.mmm.yhteinen.swing.core.component.table.YTableCellRenderer;
import fi.mmm.yhteinen.swing.core.component.table.YTableFormatter;

public class FilterHeaderTable extends MyTable implements TextChangedListener {
	private FilterEnum logic = FilterEnum.AND;
	 
	private HashMap<TableColumn,ModelFilter<TableModel,Object>> filters = new HashMap<TableColumn,ModelFilter<TableModel,Object>>();

	

	public FilterHeaderTable(YColumn[] columns){
		super(columns);
		TableRowSorter<TableModel> sorter = new TableRowSorter<TableModel>(); 
		sorter.setModel(this.getModel());
	     this.setRowSorter(sorter); 
	}
	
	/**
	 * 设置哪些列需要带过滤header
	 * @param colums
	 */
	public void setFilterColumnIndices(int[] colums){
		TableColumnModel columnModel = this.getColumnModel();
		 EditableHeader header = new EditableHeader(columnModel,colums,true);
		 setEditableHeader(header);
	}
	
	public void setEditableHeader(EditableHeader header){
		this.setTableHeader(header);
		header.addTextValueChangedListener(this);
	}

	public void setFiltersLogic(FilterEnum logic){
		this.logic = logic;
	}

	private void addFilter(TableColumn  column,ModelFilter filter){
		filters.put(column, filter);
	}
	
    public String getToolTipText(MouseEvent e) {   
        int row=this.rowAtPoint(e.getPoint());   
        int col=this.columnAtPoint(e.getPoint());   
        String tiptextString=null;   
        if(row>-1 && col>-1){   
            Object value=this.getValueAt(row, col);   
            if(null!=value && !"".equals(value))   
                tiptextString=value.toString();
        }   
        return tiptextString;   
    }   
	
	/**
	 * do filter
	 */
	public void doFilter(){
		RowFilter<TableModel,Object> filter = null;
		switch(logic){
		case AND:filter =RowFilter.andFilter(filters.values());break;
		case OR:filter = RowFilter.orFilter(filters.values());break;
		default: RowFilter.andFilter(filters.values());break;
		}
		
		TableRowSorter trSorter = (TableRowSorter)this.getRowSorter();
		trSorter.setRowFilter(filter);
	}

	@Override
	public void textChanged(TextChangedEvent event) {
		// TODO Auto-generated method stub
//		EditableHeader eh = (EditableHeader)this.getTableHeader();
		TableColumn tc = (TableColumn)event.getSource();
		
		String text = (String)event.getNewText();
		addFilter(tc, new ModelFilter<TableColumn,String>(tc,text));
		doFilter();
		
		
	}
	
	/**
	 * 
	 * the logic of data filter is underlying the tabelModel
	 * 
	 * @author dongsy
	 *
	 * @param <M>
	 * @param <I>
	 */
	private static class ModelFilter<M,I> extends RowFilter<M,I>{

		
		private  TableColumn tcolumn;
		private String text;
		 public ModelFilter(TableColumn column,String text){
			 this.tcolumn = column;
			 this.text = text;
		 }
		@Override
		public boolean include(Entry entry) {
			// TODO Auto-generated method stub
			TableModel model = (TableModel)entry.getModel();
			int column = this.tcolumn.getModelIndex();
			TableCellRenderer cellRenderer = (TableCellRenderer)tcolumn.getCellRenderer();
			String displayValue = entry.getStringValue(column);
				if(cellRenderer!= null && cellRenderer instanceof YTableCellRenderer){
					
					YTableFormatter formatter = ((YTableCellRenderer)cellRenderer).getFormatter();
					int row = (Integer)entry.getIdentifier();
					
					
					
					Object item = model.getValueAt(row, column);
					 displayValue = formatter.format(item, row, column);
					
				}
			
			
			return displayValue.indexOf(text)!=-1;
			
		}
		
}
	

}
