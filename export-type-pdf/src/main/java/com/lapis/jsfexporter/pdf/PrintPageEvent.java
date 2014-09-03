package com.lapis.jsfexporter.pdf;



import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.omnifaces.util.Faces;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.Image;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Phrase;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPageEventHelper;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

/**
 * @author Leandro de Godoy
 * Add  page number, logotype,
 * title and subtitle for PDF
 */
public class PrintPageEvent extends PdfPageEventHelper {

	int pagenumber;

	public PdfPTable table;

	public PdfTemplate tpl;

	private PdfGState gstate;

	public BaseFont arial12;

	private PdfExportOptions options;

	public PrintPageEvent() {

	}

	public PrintPageEvent(PdfExportOptions options) {
		this.options = options;
	}

	public void onOpenDocument(PdfWriter writer, Document document) {
		try {

			gstate = new PdfGState();
			gstate.setFillOpacity(0.3f);
			gstate.setStrokeOpacity(0.2f);
			tpl = writer.getDirectContent().createTemplate(100, 100);
			tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
			arial12 = BaseFont.createFont("Helvetica", BaseFont.WINANSI, false);
			tpl = writer.getDirectContent().createTemplate(100, 100);
			tpl.setBoundingBox(new Rectangle(-20, -20, 100, 100));
		} catch (Exception e) {
			throw new ExceptionConverter(e);
		}
	}

	public void onCloseDocument(PdfWriter writer, Document document) {
		tpl.beginText();
		tpl.setFontAndSize(arial12, 12);
		tpl.setTextMatrix(0, 0);
		tpl.showText(Integer.toString(writer.getPageNumber() - 1));
		tpl.endText();
		tpl.sanityCheck();
	}

	public void onEndPage(PdfWriter writer, Document document) {

		Rectangle rect = writer.getBoxSize("art");
		ColumnText.showTextAligned(writer.getDirectContent(),
				Element.ALIGN_CENTER,
				new Phrase(String.format("%d", pagenumber)),
				(rect.getLeft() + rect.getRight()) / 2, rect.getBottom() - 18,
				0);

	}

	@Override
	public void onStartPage(PdfWriter writer, Document document) {
		pagenumber++;
		if (options == null || options.getPdfTitle() == null
				|| options.getPdfSubTitle() == null
				|| options.getPdfImageLogo() == null) {
			return;
		}
		try {

			Font font = FontFactory.getFont("fonts/DroidSansFallbackFull.ttf",
					BaseFont.IDENTITY_H, true);

			PdfPTable table = new PdfPTable(2);
			int[] widths = { 300, 50 };
			table.setWidths(widths);
			table.setWidthPercentage(100);
			PdfPCell cell1 = new PdfPCell(new Paragraph(new Chunk(" ", font)));
			cell1.setBorder(0);
			cell1.setBorderWidthTop(1);
			PdfPCell cell2 = new PdfPCell();
			cell2.setBorder(0);

			StringBuilder stringTitulo = new StringBuilder();
			if (options != null && options.getPdfTitle() != null) {
				stringTitulo.append(options.getPdfTitle());
			}

			Paragraph titulo = new Paragraph(stringTitulo.toString());
			titulo.setAlignment(Element.ALIGN_CENTER);
			PdfPCell cell3 = new PdfPCell(titulo);
			cell3.setBorder(0);

			PdfPCell cell4;
			if (options != null && options.getPdfImageLogo() != null) {
				URL urlImage = Faces.getResource("/resources/"
						+ options.getPdfImageLogo());
				Image logo = Image.getInstance(urlImage);
				logo.setAlignment(Image.ALIGN_CENTER);
				logo.scaleToFit(50.0f, 50.00f);
				cell4 = new PdfPCell(logo, true);
				cell4.setBorder(0);
			} else {
				cell4 = new PdfPCell(new Paragraph("    "));
			}

			PdfPCell space1 = new PdfPCell(new Paragraph("    "));
			space1.setBorder(0);
			PdfPCell space2 = new PdfPCell(new Paragraph("    "));
			space2.setBorder(0);
			Paragraph codigoVersaoSecao;
			if (options != null && options.getPdfSubTitle() != null) {
				codigoVersaoSecao = new Paragraph(options.getPdfSubTitle()
						+ "\n\n", font);
			} else {
				codigoVersaoSecao = new Paragraph("");
			}
			PdfPCell cell5 = new PdfPCell(codigoVersaoSecao);
			cell5.setBorder(0);
			cell5.setBorderWidthTop(1);
			cell5.setBorderWidthBottom(1);

			PdfPCell cell6 = new PdfPCell();
			cell6.setBorder(0);

			PdfPCell cell7 = new PdfPCell();
			cell7.setBorder(0);
			cell7.setRowspan(2);
			PdfPCell cell8 = new PdfPCell();
			cell8.setBorder(0);
			cell8.setRowspan(2);

			table.addCell(cell1);
			table.addCell(cell2);
			table.addCell(cell3);
			table.addCell(cell4);
			table.addCell(space1);
			table.addCell(space2);
			table.addCell(cell5);
			table.addCell(cell6);
			table.addCell(cell7);
			table.addCell(cell8);

			document.add(table);
			document.add(new Paragraph("   "));
		} catch (DocumentException e2) {
			e2.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

}
