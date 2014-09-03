/*
 * #%L
 * Lapis JSF Exporter - PDF export type
 * %%
 * Copyright (C) 2013 Lapis Software Associates
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package com.lapis.jsfexporter.pdf;

import java.io.ByteArrayOutputStream;

import javax.faces.context.ExternalContext;

import com.lapis.jsfexporter.api.IExportCell;
import com.lapis.jsfexporter.api.IExportRow;
import com.lapis.jsfexporter.api.IExportType;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class PDFExportType implements
		IExportType<Document, PdfExportOptions, Integer> {

	private Document document;
	private PdfPTable table;
	private Font font;
	private int rowCount;
	private ByteArrayOutputStream buffer;
	private PdfExportOptions options;
	PdfWriter writer;

	public PDFExportType(PdfExportOptions options) {
		this.options = options;
		document = new Document();
		font = FontFactory.getFont("fonts/DroidSansFallbackFull.ttf",
				BaseFont.IDENTITY_H, true);
		buffer = new ByteArrayOutputStream();
		try {
			writer = PdfWriter.getInstance(document, buffer);
			writer.setBoxSize("art", new Rectangle(25, 25, 559, 788));

		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public Document getContext() {
		return document;
	}

	@Override
	public void beginExport(int columnCount) {
		if (options != null)
			writer.setPageEvent(new PrintPageEvent(options));
		table = new PdfPTable(columnCount);
		if (!document.isOpen()) {
			document.open();
		}

	}

	@Override
	public Integer exportRow(IExportRow row) {
		for (IExportCell cell : row.getCells()) {
			PdfPCell pdfCell = new PdfPCell();
			pdfCell.setColspan(cell.getColumnSpanCount());
			pdfCell.setRowspan(cell.getRowSpanCount());
			pdfCell.setPhrase(new Phrase(cell.getValue(), font));
			table.addCell(pdfCell);
		}

		return rowCount++;
	}

	@Override
	public void endExport() {
		try {
			document.add(table);
		} catch (DocumentException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public void writeExport(ExternalContext externalContext) throws Exception {
		document.close();
		buffer.writeTo(externalContext.getResponseOutputStream());
	}

	@Override
	public String getContentType() {
		return "application/pdf";
	}

	@Override
	public String getFileExtension() {
		return "pdf";
	}

}
