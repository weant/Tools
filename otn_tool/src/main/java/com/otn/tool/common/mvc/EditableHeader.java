package com.otn.tool.common.mvc;

import com.birosoft.liquid.LiquidTableHeaderUI;

import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.MouseInputListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

/**
 *         Editable header is an important component to a JTable which need to
 *         filter data when user fill the text in the filterFiled. The header
 *         will be a textField and used to receive user's input, so we have no
 *         need to give the column name. you can choose some column to be
 *         editable and others not by passing a int array in
 *         <class>EditableHeader</class> constructor you can determine how to
 *         deal with not editable column, means to display them or not by
 *         passing a <class>Boolean</class>.value there is an example:
 * 
 *         <pre>
 * 
 * Object rows[][] = { { &quot;A&quot;, &quot;About&quot;, 44.36 }, { &quot;B&quot;, &quot;Boy&quot;, 44.84 },
 * 		{ &quot;C&quot;, &quot;Cat&quot;, 463.63 }, { &quot;D&quot;, &quot;Day&quot;, 27.14 }, { &quot;E&quot;, &quot;Eat&quot;, 44.57 },
 * 		{ &quot;F&quot;, &quot;Fail&quot;, 23.15 }, { &quot;G&quot;, &quot;Good&quot;, 4.40 }, { &quot;H&quot;, &quot;Hot&quot;, 24.96 },
 * 		{ &quot;I&quot;, &quot;Ivey&quot;, 5.45 }, { &quot;J&quot;, &quot;Jack&quot;, 49.54 }, { &quot;K&quot;, &quot;Kids&quot;, 280.00 } };
 * 
 * // columns is blank string array
 * String columns[] = { &quot;&quot;, &quot;&quot;, &quot;&quot; };
 * 
 * // table model init
 * TableModel model = new DefaultTableModel(rows, columns) {
 * 	public Class getColumnClass(int column) {
 * 		Class returnValue;
 * 		if ((column &gt;= 0) &amp;&amp; (column &lt; getColumnCount())) {
 * 			returnValue = getValueAt(0, column).getClass();
 * 		} else {
 * 			returnValue = Object.class;
 * 		}
 * 		return returnValue;
 * 	}
 * };
 * // set model
 * final JTable table = new JTable(model);
 * 
 * // choose columns which index = 0 or index = 1 can be editable
 * int[] editableIndices = { 0, 1 };
 * TableColumnModel columnModel = table.getColumnModel();
 * EditableHeader header = new EditableHeader(columnModel, editableIndices, false);
 * // set the table header
 * table.setTableHeader(header);
 * JScrollPane pane = new JScrollPane(table);
 * 
 * </pre>
 * 
 *         Editable header provide a method to notify listeners that text in
 *         header has changed, you should implement
 *         <class>TextChangedListener</class> to handle this situation.
 * 
 *         for example we need to filter data after text changed:
 * 
 *         <pre>
 * final TableRowSorter&lt;TableModel&gt; sorter = new TableRowSorter&lt;TableModel&gt;(model);
 * 
 * // set rowSorter
 * table.setRowSorter(sorter);
 * 
 * // register the TextChangedListener
 * header.addTextValueChangedListener(new TextChangedListener() {
 * 
 * 	private HashMap&lt;TableColumn, RowFilter&lt;TableModel, Integer&gt;&gt; filters = new HashMap&lt;TableColumn, RowFilter&lt;TableModel, Integer&gt;&gt;();
 * 
 * 	&#064;Override
 * 	public void textChanged(TextChangedEvent event) {
 * 		// TODO Auto-generated method stub
 * 		// System.out.println(&quot;changed&quot;);
 * 		String text = event.getNewText();
 * 		TableColumn tc = (TableColumn) event.getSource();
 * 		int index = tc.getModelIndex();
 * 
 * 		if (text != null) {
 * 			filters.put(
 * 					tc,
 * 					new MyFilter&lt;TableModel, Integer&gt;(event.getNewText(), index));
 * 			RowFilter&lt;TableModel, Integer&gt; filter = RowFilter.andFilter(filters
 * 					.values());
 * 			sorter.setRowFilter(filter);
 * 
 * 		}
 * 
 * 	}
 * 
 * });
 * </pre>
 * 
 *         <class>MyFilter</class> must extends RowFilter and is like this:
 * 
 *         <pre>
 * class MyFilter&lt;AbstractTableModel, I&gt; extends RowFilter&lt;AbstractTableModel, I&gt; {
 * 
 * 	// new text user filled
 * 	private String text;
 * 	// the column number need to filter
 * 	private int column;
 * 
 * 	public MyFilter(String text, int column) {
 * 		this.text = text;
 * 		this.column = column;
 * 	}
 * 
 * 	&#064;Override
 * 	public boolean include(Entry entry) {
 * 		// TODO Auto-generated method stub
 * 		String text = entry.getStringValue(this.column);
 * 		if (text.startsWith(this.text))
 * 			return true;
 * 		return false;
 * 	}
 * 
 * }
 * </pre>
 */
public class EditableHeader extends JTableHeader implements CellEditorListener {
	
	private static final long serialVersionUID = 1L;

	public final int HEADER_ROW = -10;

	transient protected int editingColumn;

	transient protected TableCellEditor cellEditor;

	private static ImageIcon filterIcon = new ImageIcon("./icon/filter.png");
	transient protected Component editorComp;
	private EditableHeaderTableColumn[] newCols;
	protected boolean notEditableHeaderVisible;
	private Map<TableCellEditor,Component> editorCompMap = new HashMap<TableCellEditor, Component>();
	private Map<TableCellEditor,Integer> editorIndexMap = new HashMap<TableCellEditor, Integer>();

	public EditableHeader(TableColumnModel columnModel, int[] editableIndices,
			boolean notEditableHeaderVisible) {
		super(columnModel);
		setReorderingAllowed(false);
		cellEditor = null;

		this.notEditableHeaderVisible = notEditableHeaderVisible;
		recreateTableColumn(columnModel, editableIndices);
	}

	// 注释之后将统�?��用UIManager提供的UI风格
	public void updateUI() {
		setUI(new EditableHeaderUI());
		resizeAndRepaint();
		invalidate();
	}

	private boolean isColumnHeaderEditable(int[] editableIndices,
			int headerIndex) {
		for (int i = 0, length = editableIndices.length; i < length; i++) {
			if (editableIndices[i] == headerIndex) {
				return true;
			}
		}

		return false;
	}

	protected void recreateTableColumn(TableColumnModel columnModel,
			int[] editableIndices) {
		int n = columnModel.getColumnCount();
		newCols = new EditableHeaderTableColumn[n];
		TableColumn[] oldCols = new TableColumn[n];
		for (int i = 0; i < n; i++) {
			oldCols[i] = columnModel.getColumn(i);

			boolean editable = false;
			if (isColumnHeaderEditable(editableIndices, i)) {
				editable = true;
			}
			String headValue = (String) oldCols[i].getHeaderValue();
			newCols[i] = new EditableHeaderTableColumn(editable,
					notEditableHeaderVisible);
			newCols[i].copyValues(oldCols[i]);
			newCols[i].setHeaderRenderer(new FilterFieldTableHeaderRender(
					headValue, editable, notEditableHeaderVisible));
			// oldCols[i].setHeaderValue("");

		}
		for (int i = 0; i < n; i++) {
			columnModel.removeColumn(oldCols[i]);
		}
		for (int i = 0; i < n; i++) {
			columnModel.addColumn(newCols[i]);
		}
	}

	public void addTextValueChangedListener(TextChangedListener listener) {
		// this.listeners.add(listener);
		for (EditableHeaderTableColumn ehtc : newCols) {
			ehtc.addTextValueChangedListener(listener);
		}
	}

	public void removeTextValueChangedListener(TextChangedListener listener) {
		for (EditableHeaderTableColumn ehtc : newCols) {
			ehtc.removeTextValueChangedListener(listener);
		}
	}
	
	public void addKeyGetListener(KeyListener listener){
		for (EditableHeaderTableColumn ehtc : newCols) {
			ehtc.addKeyGetListener(listener);
		}
	}
	
	public void removeKeyGetListener(KeyListener listener){
		for (EditableHeaderTableColumn ehtc : newCols) {
			ehtc.removeKeyGetListener(listener);
		}
	}

	public void resizeCellAt(TableColumn resizeColumn, Point p) {	
		for(TableCellEditor editor:editorCompMap.keySet()) {
			Integer modelIndex = editorIndexMap.get(editor);
			if(modelIndex == null) continue;
			Rectangle rect = getHeaderRect(modelIndex);
			Component component = editorCompMap.get(editor);
			if (component != null) {
				component.setBounds(rect);
				component.validate();
				component.repaint();
			}
		}
		
	}

	public boolean editCellAt(int index, EventObject e) {
		if (cellEditor != null && !cellEditor.stopCellEditing()) {
			return false;
		}
		if (!isCellEditable(index)) {
			return false;
		}
		final TableCellEditor editor = getCellEditor(index);
		
		if (editor != null && editor.isCellEditable(e)) {
			editorComp = prepareEditor(editor, index);
			editorComp.setBounds(getHeaderRect(index));
			add(editorComp);
			editorIndexMap.put(editor, columnModel.getColumn(index).getModelIndex());
			editorCompMap.put(editor, editorComp);
			editorComp.addFocusListener(new FocusAdapter() {				
				public void focusLost(FocusEvent e) {
					removeComponent(editor);		
				}		
			});
			editorComp.addKeyListener(new KeyAdapter() {					
				public  void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_ENTER) {
						removeComponent(editor);
					}			
				}
			});
			editorComp.validate();
			editorComp.repaint();
			setCellEditor(editor);
			setEditingColumn(index);
			editor.addCellEditorListener(this);

			return true;
		}
		return false;
	}
	private void removeComponent(TableCellEditor editor) {
		if(editor.getCellEditorValue().equals("")) {
			Component component = editorCompMap.get(editor);
			Integer modelIndex = editorIndexMap.get(editor);
			if(modelIndex != null)
				editorIndexMap.remove(editor);
			if(component == null) return;
			remove(component);					
			editorCompMap.remove(editor);
			repaint();
		}
	}
	public boolean isCellEditable(int index) {
		if (getReorderingAllowed()) {
			return false;
		}
		int columnIndex = columnModel.getColumn(index).getModelIndex();
		EditableHeaderTableColumn col = (EditableHeaderTableColumn) columnModel
				.getColumn(columnIndex);
		return col.isHeaderEditable();
	}

	public TableCellEditor getCellEditor(int index) {
		int columnIndex = columnModel.getColumn(index).getModelIndex();
		EditableHeaderTableColumn col = (EditableHeaderTableColumn) columnModel
				.getColumn(columnIndex);
		return col.getHeaderEditor();
	}

	public void setCellEditor(TableCellEditor newEditor) {
		TableCellEditor oldEditor = cellEditor;
		cellEditor = newEditor;

		// firePropertyChange

		if (oldEditor != null && oldEditor instanceof TableCellEditor) {
			((TableCellEditor) oldEditor)
					.removeCellEditorListener((CellEditorListener) this);
		}
		if (newEditor != null && newEditor instanceof TableCellEditor) {
			((TableCellEditor) newEditor)
					.addCellEditorListener((CellEditorListener) this);
		}
	}

	@SuppressWarnings("deprecation")
	public Component prepareEditor(TableCellEditor editor, int index) {
		Object value = columnModel.getColumn(index).getHeaderValue();
		boolean isSelected = true;
		int row = HEADER_ROW;
		JTable table = getTable();
		Component comp = editor.getTableCellEditorComponent(table, value,
				isSelected, row, index);
		if (comp instanceof JComponent) {
			((JComponent) comp).setNextFocusableComponent(this);
		}
		return comp;
	}

	public TableCellEditor getCellEditor() {
		return cellEditor;
	}

	public Component getEditorComponent() {
		return editorComp;
	}

	public void setEditingColumn(int aColumn) {
		editingColumn = aColumn;
	}

	public int getEditingColumn() {
		return editingColumn;
	}
	
	public void removeEditor() {		
/*		TableCellEditor editor = getCellEditor();
		if (editor != null) {
			editor.removeCellEditorListener(this);
			
			requestFocus();
			remove(editorComp);
//			int index = getEditingColumn();
//			Rectangle cellRect = getHeaderRect(index);

			setCellEditor(null);
			setEditingColumn(-1);
			editorComp = null;

			// repaint(cellRect);
		}*/
	}

	public boolean isEditing() {
		return (cellEditor == null) ? false : true;
	}

	//
	// CellEditorListener
	//
	public void editingStopped(ChangeEvent e) {	
		removeEditor();
		/*TableCellEditor editor = getCellEditor();
		if (editor != null) {
			Object value = editor.getCellEditorValue();
			int index = getEditingColumn();
			columnModel.getColumn(index).setHeaderValue(value);
			removeEditor();
		}*/
	}

	public void editingCanceled(ChangeEvent e) {		
		removeEditor();
	}

	static class FilterFieldTableHeaderRender implements TableCellRenderer {

		@SuppressWarnings("unused")
		private boolean isHeaderEditable;
		@SuppressWarnings("unused")
		private boolean notEditableHeaderVisible;
		private IconTextField filterField = new IconTextField(20);
		private JLabel label;

		public FilterFieldTableHeaderRender(String headValue,
				boolean isHeaderEditable, boolean notEditableHeaderVisible) {
			this.isHeaderEditable = isHeaderEditable;
			this.notEditableHeaderVisible = notEditableHeaderVisible;
			filterField.setIcon(filterIcon);
			label = new JLabel();
			if (isHeaderEditable) {
				label.setIcon(filterIcon);
			}
			label.setHorizontalAlignment(JLabel.CENTER);
			label.setBorder(BorderFactory.createEtchedBorder());
			label.setText(headValue);

		}

		@Override
		public Component getTableCellRendererComponent(JTable table,
				Object value, boolean isSelected, boolean hasFocus, int row,
				int column) {
			// TODO Auto-generated method stub

			return label;
		}

	}

	static class EditableHeaderTableColumn extends TableColumn implements
			DocumentListener, KeyListener {

		/**
			 * 
			 */
		private static final long serialVersionUID = 1L;
		protected TableCellEditor headerEditor;
		protected IconTextField jt;
		protected boolean isHeaderEditable;
		protected boolean notEditableHeaderVisible;
		private LinkedList<TextChangedListener> textListeners = new LinkedList<TextChangedListener>();
		private LinkedList<KeyListener> keyListeners = new LinkedList<KeyListener>();

		public EditableHeaderTableColumn(boolean isHeaderEditable,
				boolean notEditableHeaderVisible) {

			this.isHeaderEditable = isHeaderEditable;
			this.notEditableHeaderVisible = notEditableHeaderVisible;
			setHeaderEditor(createDefaultHeaderEditor());

		}

		public synchronized void addTextValueChangedListener(
				TextChangedListener listener) {
			this.textListeners.add(listener);
		}

		public synchronized void removeTextValueChangedListener(
				TextChangedListener listener) {
			this.textListeners.remove(listener);
		}
		
		public synchronized void addKeyGetListener(KeyListener listener){
			this.keyListeners.add(listener);
		}
		
		public synchronized void removeKeyGetListener(KeyListener listener){
			this.keyListeners.remove(listener);
		}

		public void fireTextValueChanged(Object eventSource, String text) {
			TextChangedEvent event = new TextChangedEvent(eventSource, text);
			for (TextChangedListener listener : this.textListeners) {
				listener.textChanged(event);
			}
		}

		public void setHeaderEditor(TableCellEditor headerEditor) {
			this.headerEditor = headerEditor;
		}

		public TableCellEditor getHeaderEditor() {
			return headerEditor;
		}

		public void setHeaderEditable(boolean isEditable) {
			isHeaderEditable = isEditable;
		}

		public boolean isHeaderEditable() {
			return isHeaderEditable;
		}

		public void copyValues(TableColumn base) {
			modelIndex = base.getModelIndex();
			identifier = base.getIdentifier();
			width = base.getWidth();
			minWidth = base.getMinWidth();
			setPreferredWidth(base.getPreferredWidth());
			maxWidth = base.getMaxWidth();
			headerRenderer = base.getHeaderRenderer();
			headerValue = "";
			cellRenderer = base.getCellRenderer();
			cellEditor = base.getCellEditor();
			isResizable = base.getResizable();
		}

		protected TableCellEditor createDefaultHeaderEditor() {
			jt = new IconTextField();
			jt.setIcon(filterIcon);
			jt.getDocument().addDocumentListener(this);
			jt.addKeyListener(this);
			if ((!isHeaderEditable) && (!notEditableHeaderVisible)) {
				jt.setVisible(false);
			}

			if ((!isHeaderEditable) && (notEditableHeaderVisible)) {
				jt.setEditable(false);
				jt.setBackground(Color.gray);
			}

			DefaultCellEditor editor = new DefaultCellEditor(jt);
			editor.setClickCountToStart(1);
			return editor;
		}

		@Override
		public void changedUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			
			this.fireTextValueChanged(this, jt.getText());
		}

		@Override
		public void insertUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			this.fireTextValueChanged(this, jt.getText());
		}

		@Override
		public void removeUpdate(DocumentEvent e) {
			// TODO Auto-generated method stub
			this.fireTextValueChanged(this, jt.getText());
		}

		@Override
		public void keyTyped(KeyEvent e) {
			for (KeyListener listener : this.keyListeners) {
				listener.keyTyped(e);
			}
		}

		@Override
		public void keyPressed(KeyEvent e) {
			for (KeyListener listener : this.keyListeners) {
				listener.keyPressed(e);
			}			
		}

		@Override
		public void keyReleased(KeyEvent e) {
			for (KeyListener listener : this.keyListeners) {
				listener.keyReleased(e);
			}			
		}

	}

}

class EditableHeaderUI extends LiquidTableHeaderUI {

	protected MouseInputListener createMouseInputListener() {
		return new MouseInputHandler((EditableHeader) header);
	}

	public class MouseInputHandler extends
			LiquidTableHeaderUI.MouseInputHandler {
		private Component dispatchComponent;

		protected EditableHeader header;

		public MouseInputHandler(EditableHeader header) {
			this.header = header;
		}

		private void setDispatchComponent(MouseEvent e) {
			Component editorComponent = header.getEditorComponent();
			Point p = e.getPoint();
			Point p2 = SwingUtilities.convertPoint(header, p, editorComponent);
			dispatchComponent = SwingUtilities.getDeepestComponentAt(
					editorComponent, p2.x, p2.y);
		}

		private boolean repostEvent(MouseEvent e) {
			if (dispatchComponent == null) {
				return false;
			}
			MouseEvent e2 = SwingUtilities.convertMouseEvent(header, e,
					dispatchComponent);
			dispatchComponent.dispatchEvent(e2);
			return true;
		}

		public void mousePressed(MouseEvent e) {
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			super.mousePressed(e);

			if (header.getResizingColumn() == null) {
				Point p = e.getPoint();
				TableColumnModel columnModel = header.getColumnModel();
				int index = columnModel.getColumnIndexAtX(p.x);
				if (index != -1) {
					if (header.editCellAt(index, e)) {
						setDispatchComponent(e);
						repostEvent(e);
					}
				}
			}
		}

		public void mouseDragged(MouseEvent e) {
			super.mouseDragged(e);
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			TableColumn resizeColumn = header.getResizingColumn();
			if (resizeColumn != null) {
				header.resizeCellAt(resizeColumn, e.getPoint());
			}
		}

		public void mouseReleased(MouseEvent e) {
			super.mouseReleased(e);
			if (!SwingUtilities.isLeftMouseButton(e)) {
				return;
			}
			TableColumn resizeColumn = header.getResizingColumn();
			if (resizeColumn != null) {
				header.resizeCellAt(resizeColumn, e.getPoint());
			}
			repostEvent(e);
			dispatchComponent = null;
			
		}

	}

}
