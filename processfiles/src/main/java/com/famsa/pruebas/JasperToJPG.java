package com.famsa.pruebas;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import com.famsa.enums.BDEnum;
import com.famsa.exceptions.SQLServerConnectionExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.view.JasperViewer;

public class JasperToJPG {

	public static void main(String[] args) throws SQLException, SQLServerConnectionExc, JRException, IOException {
		
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {

			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("", "");	//en este caso no lleva parametros
			JasperReport report = JasperCompileManager.compileReport("C:\\Users\\RDEFAGUILA\\git\\eclipse\\processfiles\\resources\\GeneraArchivos.jrxml");

			JasperPrint jasperPrint = JasperFillManager.fillReport(report, parameters, conn);
			
			final String extension = "jpg";
			final float zoom = 1f;
			String fileName = "report";

			int pages = jasperPrint.getPages().size();
			for (int i = 0; i < pages; i++) {
			    try(OutputStream out = new FileOutputStream(fileName + "_p" + (i+1) +  "." + extension)){
			        BufferedImage image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, i,zoom);   
			        ImageIO.write(image, extension, out);	//guarda la imagen
			        
			        JasperViewer.viewReport(jasperPrint, false);			        
			    } catch (IOException e) {
			        e.printStackTrace();
			    }
			}		
		}
		
		
		
		File fileJPG = new File("C:\\Users\\RDEFAGUILA\\git\\eclipse\\processfiles\\report_p1.jpg");
		BufferedImage image = ImageIO.read(fileJPG);
		
		String filename = "C:\\Users\\RDEFAGUILA\\git\\eclipse\\processfiles\\report_p1.tif";
		File tiffFile = new File(filename);
		ImageOutputStream ios = null;
		ImageWriter writer = null;

		try {

			// find an appropriate writer
			Iterator<?> it = ImageIO.getImageWritersByFormatName("TIF");
			if (it.hasNext()) {
				writer = (ImageWriter)it.next();
			} 
	
			// setup writer
			ios = ImageIO.createImageOutputStream(tiffFile);
			writer.setOutput(ios);
			TIFFImageWriteParam writeParam = new TIFFImageWriteParam(Locale.ENGLISH);
			writeParam.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
			// see writeParam.getCompressionTypes() for available compression type strings
			writeParam.setCompressionType("PackBits");
	
			// convert to an IIOImage
			IIOImage iioImage = new IIOImage(image, null, null);
	
			// write it!
			writer.write(null, iioImage, writeParam);

		} catch (IOException e) {
			e.printStackTrace();
		}		
			
			
			
			
			
			
	}

}
