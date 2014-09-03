
package com.lapis.jsfexporter.richfaces.extendedDatatable;

import java.io.Serializable;

/**
 * Configuration options for rich:extendedDataTable export source. 
 * @author Leandro de Godoy
 *
 */
public class ExtendedDataTableExportOptions implements Serializable {
	private static final long serialVersionUID = 1L;

	private ExtendedDataTableExportRange range;

	/**
	 * Constructor for default options:
	 * <ul>
	 * <li>Range: ALL</li>
	 * </ul>
	 */
	public ExtendedDataTableExportOptions() {
		this.range = ExtendedDataTableExportRange.ALL;
	}
	
	public ExtendedDataTableExportOptions(ExtendedDataTableExportRange range) {
		this.range = range;
	}

	public ExtendedDataTableExportRange getRange() {
		return range;
	}

	public void setRange(ExtendedDataTableExportRange range) {
		this.range = range;
	}

}
