package com.famsa.controlador;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import com.famsa.aplicacion.XCapture;
import com.famsa.exceptions.XCaptureExcep;
import com.famsa.fabricas.ConfigFactory;
import com.famsa.fabricas.EliminaFactory;
import com.famsa.fabricas.SeparaFactory;
import com.famsa.interfaces.IConfig;
import com.famsa.interfaces.IElimina;
import com.famsa.interfaces.IProcesa;
import com.famsa.interfaces.ISepara;
import com.famsa.xmlmarshall.Archivo;
import com.famsa.xmlmarshall.Config;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.FormatException;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Reader;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

public class Procesa implements IProcesa {

	private static final Logger logger = Logger.getLogger(XCapture.class.getName());
	String codeBar = null;
	String msg = null;
	Config config = null;
	Archivo archivo = null;
	
	@Override
	public Archivo buildI(Archivo arch) throws XCaptureExcep {

		archivo=arch;
		codeBar=null;
		
		obtenerConfiguracion();
		
		separaImagen();

		boolean encontre = false;
		for (int i=0;i<archivo.getNumeroPaginas();i++) {
			
			String[] tokens = archivo.getNomArch().split("\\.(?=[^\\.]+$)");
			String archivoAnt = archivo.getPathCopy()+tokens[0]+"-"+(i+1)+".ant";
			String archivoTif = archivo.getPathCopy()+tokens[0]+"-"+(i+1)+".tif";

			if (!encontre) {
				
				String resultado = buscaCodeBar(archivoAnt);
				if (resultado!=null) {
					encontre=true;
					codeBar = resultado;
				
					msg = String.format("%s   Codigo: %s", archivo.getThreadName(),codeBar);
					logger.info(msg);
				}
			}
			
			cambiaTamanoImagen(archivoAnt, archivoTif);
			
			eliminaImagenAnt(archivoAnt);
		}
		return archivo;
	}

	private void separaImagen() throws XCaptureExcep {
		ISepara iSeparaImagen = SeparaFactory.buildInstance();
		Archivo sepArch = iSeparaImagen.buildI(archivo);
		archivo.setNumeroPaginas(sepArch.getNumeroPaginas());
	}
	
	private String buscaCodeBar(String archivo) throws XCaptureExcep {
		String resultado = null;
		InputStream barCodeInputStream;
		try {
			barCodeInputStream = new FileInputStream(archivo);
		} catch (FileNotFoundException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		BufferedImage barCodeBufferedImage;
		BufferedImage cropedImage;
		try {
			barCodeBufferedImage = ImageIO.read(barCodeInputStream);
			cropedImage = barCodeBufferedImage.getSubimage(
					0, 0, barCodeBufferedImage.getWidth(), barCodeBufferedImage.getHeight()/2);
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		LuminanceSource source = new BufferedImageLuminanceSource(cropedImage);
		BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
		Reader reader = new MultiFormatReader();
		try {
			resultado = reader.decode(bitmap).getText();
		} catch (NotFoundException | ChecksumException | FormatException e) {
			resultado = null;
		}
		try {
			barCodeInputStream.close();
		} catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
		return resultado;
	}

    private void cambiaTamanoImagen(String pathFileName, String copyPathFileName) throws XCaptureExcep {
    	
    	int maxWidth = 0;
    	int maxHeight = 0;
    	
    	BufferedImage bimage = cargaImagen(pathFileName);
    	
    	maxWidth = (int) (bimage.getWidth() - (bimage.getWidth() * config.getHilo().getPorcentaje()));
    	maxHeight = (int) (bimage.getHeight() - (bimage.getHeight() * config.getHilo().getPorcentaje()));
    	
        if(bimage.getHeight()>bimage.getWidth()){
            int heigt = (bimage.getHeight() * maxWidth) / bimage.getWidth();
            bimage = reDimensiona(bimage, maxWidth, heigt);
            int width = (bimage.getWidth() * maxHeight) / bimage.getHeight();
            bimage = reDimensiona(bimage, width, maxHeight);
        }else{
            int width = (bimage.getWidth() * maxHeight) / bimage.getHeight();
            bimage = reDimensiona(bimage, width, maxHeight);
            int heigt = (bimage.getHeight() * maxWidth) / bimage.getWidth();
            bimage = reDimensiona(bimage, maxWidth, heigt);
        }
        guardaImagen(bimage, copyPathFileName);
    }

    private void eliminaImagenAnt(String nombreFile) throws XCaptureExcep {
    	IElimina iEliminaArchivo = EliminaFactory.delFileAntInstance();
    	try {
			iEliminaArchivo.delFileAnt(nombreFile);
		} catch (XCaptureExcep e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
		}
    }
    
    private BufferedImage cargaImagen(String pathFileName) throws XCaptureExcep {
        BufferedImage bimage = null;
        try {
            bimage = ImageIO.read(new File(pathFileName));
        } catch (Exception e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
        }
        return bimage;
    }

    private void guardaImagen(BufferedImage bufferedImage, String pathFileName) throws XCaptureExcep {
        try {
            File file =new File(pathFileName);
            file.getParentFile().mkdirs();
            ImageIO.write(bufferedImage, "tiff", file);
        } catch (IOException e) {
			logger.log(Level.SEVERE, e.toString(), e);
			throw new XCaptureExcep(e.toString(), e);
        }
    }    

    private BufferedImage reDimensiona(BufferedImage bufferedImage, int newW, int newH) {
        int w = bufferedImage.getWidth();
        int h = bufferedImage.getHeight();
        BufferedImage bufim = new BufferedImage(newW, newH, bufferedImage.getType());
        Graphics2D g = bufim.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(bufferedImage, 0, 0, newW, newH, 0, 0, w, h, null);
        g.dispose();
        return bufim;
    }    

	private void obtenerConfiguracion() {
		IConfig iConfig = ConfigFactory.getConfig();
		config = iConfig.getIConfig();
	}

}
