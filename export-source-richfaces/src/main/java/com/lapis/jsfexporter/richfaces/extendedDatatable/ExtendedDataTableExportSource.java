
package com.lapis.jsfexporter.richfaces.extendedDatatable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;

import org.richfaces.component.UIColumn;
import org.richfaces.component.UIExtendedDataTable;

import com.lapis.jsfexporter.api.FacetType;
import com.lapis.jsfexporter.api.IExportCell;
import com.lapis.jsfexporter.api.IExportType;
import com.lapis.jsfexporter.impl.ExportCellImpl;
import com.lapis.jsfexporter.impl.ExportRowImpl;
import com.lapis.jsfexporter.spi.IExportSource;
import com.lapis.jsfexporter.util.ExportUtil;

/**
 * @author Leandro de Godoy
 *
 */
public class ExtendedDataTableExportSource implements IExportSource<UIExtendedDataTable, ExtendedDataTableExportOptions> {

	@Override
	public Class<UIExtendedDataTable> getSourceType() {
		return UIExtendedDataTable.class;
	}

	@Override
	public ExtendedDataTableExportOptions getDefaultConfigOptions() {
		return new ExtendedDataTableExportOptions();
	}
	
	@Override
	public int getColumnCount(UIExtendedDataTable source, ExtendedDataTableExportOptions configOptions) {
		int columnCount = 0;
		for (UIComponent kid : source.getChildren()) {
			if (kid instanceof UIColumn && kid.isRendered()&& !kid.getId().startsWith("action")) {
				columnCount++;
			}
		}
		return columnCount;
	}

	@Override
	public void exportData(UIExtendedDataTable source,ExtendedDataTableExportOptions configOptions, IExportType<?, ?, ?> exporter, FacesContext context) throws Exception {
		List<UIColumn> columns = new ArrayList<UIColumn>();
		for (UIComponent kid : source.getChildren()) {
			if (kid instanceof UIColumn && kid.isRendered()&& !kid.getId().startsWith("action")) {
				columns.add((UIColumn) kid);
			}
		}
		
		List<List<String>> columnNames = exportFacet(FacetType.HEADER, source, columns, exporter, context);
		
		if (configOptions.getRange() == ExtendedDataTableExportRange.ALL) {
			exportRowCells(source, columns, columnNames, 0, source.getRowCount(), exporter, context);
		} else { // PAGE_ONLY
			exportRowCells(source, columns, columnNames, source.getFirst(), source.getFirst() + source.getRows(), exporter, context);
		}
		
		exportFacet(FacetType.FOOTER, source, columns, exporter, context);
	}
	
	private List<List<String>> exportFacet(FacetType facetType, UIExtendedDataTable source, List<UIColumn> columns, IExportType<?, ?, ?> exporter, FacesContext context) {
		List<List<String>> columnNames = new ArrayList<List<String>>();
		List<IExportCell> facetCells = new ArrayList<IExportCell>();
		
		boolean hasFacet = false;
		for (UIColumn column : columns) {
			UIComponent facetComponent = column.getFacet(facetType.getFacetName());
			if (facetComponent != null) {
				hasFacet = true;
				columnNames.add(Arrays.asList(ExportUtil.transformComponentsToString(context, facetComponent)));
			}
		}
		if (hasFacet) {
			for (List<String> columnName : columnNames) {
				facetCells.add(new ExportCellImpl(Arrays.asList(facetType.getFacetName()), columnName.get(0), 1, 1));
			}
			exporter.exportRow(new ExportRowImpl(Arrays.asList(facetType.getFacetName()), null, facetType, facetCells));
		}
		
		for (int i = 0; i < columnNames.size(); i++) {
			columnNames.set(i, new ArrayList<String>(new LinkedHashSet<String>(columnNames.get(i))));
		}
		
		return columnNames;
	}
	
	private void exportRowCells(UIExtendedDataTable source, List<UIColumn> columns, List<List<String>> columnNames, int startingRow, int endingRow, IExportType<?, ?, ?> exporter, FacesContext context) {
		List<String> rowName = Arrays.asList(source.getVar());
		List<IExportCell> cells = new ArrayList<IExportCell>();
		int columnCount = columns.size();
		
		for (int i = startingRow; i < endingRow; i++) {
			source.setRowKey(i);
			
			for (int j = 0; j < columnCount; j++) {
				UIColumn column = columns.get(j);
				
				cells.add(new ExportCellImpl(columnNames.get(j), ExportUtil.transformComponentsToString(context, column.getChildren()), 1, 1));
			}
			
			exporter.exportRow(new ExportRowImpl(rowName, null, null, cells));
			cells.clear();
		}
	}
	
}
