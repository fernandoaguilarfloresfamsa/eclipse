package com.famsa.aplicacion;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import com.famsa.bean.Configuracion;
import com.famsa.controlador.ProcessFileCtrl;
import com.famsa.enums.BDEnum;
import com.famsa.exceptions.GeneraArchivosExc;
import com.famsa.exceptions.ProcessFileCtrlExc;
import com.famsa.exceptions.SQLServerConnectionExc;
import com.famsa.fabricas.BaseDatosFactory;
import com.famsa.interfaces.IBaseDatosConexion;
import com.github.jaiimageio.plugins.tiff.TIFFImageWriteParam;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.TIFFEncodeParam;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperPrintManager;
import net.sf.jasperreports.engine.JasperReport;

public class GeneraArchivos extends JFrame implements KeyListener, ActionListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2941309105059081624L;
	static final Logger logGeneraArchivos = Logger.getLogger(GeneraArchivos.class.getName());
	static FileHandler fhBuscaArchivos;
	static Configuracion configuracion = null;
	static boolean salida = false;
	static int numArchGen = 0;

    JTextArea displayArea;
    JTextField typingArea;
    static final String NEWLINE = System.getProperty("line.separator");
	
	public static void main(String[] args) throws GeneraArchivosExc, SQLException, SQLServerConnectionExc, 
		JRException, IOException {
		
		try {
			GeneraArchivos.inicio();
		} catch (GeneraArchivosExc e) {
			throw new GeneraArchivosExc(e.toString(), e);
		}
		
        /*
         *	Use an appropriate Look and Feel
         */
        try {
        	/*
        	 *	UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        	 *	UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); 
        	 */
            UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException | ClassNotFoundException ex) {
            throw new GeneraArchivosExc(ex.toString(), ex);
        } 
        /*
         *	Turn off metal's use of bold fonts
         */
        UIManager.put("swing.boldMetal", Boolean.FALSE);
         
        /*
         *	Schedule a job for event dispatch thread:
         *	creating and showing this application's GUI. 
         */
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });		
        
        /*
         * bucle
         */
        while (true) {
            if (salida) {
            	logGeneraArchivos.log(Level.INFO, "FIN DEL PROCESO");
            	break;
            }
            
            try {
				TimeUnit.SECONDS.sleep(1);
				
				String fileName = GeneraArchivos.generaReporteJasper();
				GeneraArchivos.convierteJasperToJPG(fileName);
				GeneraArchivos.convierteMultiTiffToMultiPageTiff(fileName);
				
				numArchGen++;
				DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
				Date date = new Date();
				System.out.println("["+numArchGen+"]-"+dateFormat.format(date));
				
			} catch (InterruptedException e) {
				logGeneraArchivos.log(Level.INFO, e.toString(), e);
				Thread.currentThread().interrupt();
			}
        }              
        
	}

	private static void inicio() throws GeneraArchivosExc {

		try {
			obtenerConfiguracion();
		} catch (GeneraArchivosExc e1) {
			throw new GeneraArchivosExc(e1.toString(), e1);
		}
		
		GeneraArchivos.creaCarpeta(configuracion.getHilo().getPathLogErr());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getEncontrados());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getTemporal());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getJasperReports());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getJasperReportsJPG());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getSourceTIF());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getTemporalTIF());
		GeneraArchivos.creaCarpeta(configuracion.getFolder().getBatch());

		Date date = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS-");
		String nomArc = String.format("%s%s%s.log", 
				configuracion.getHilo().getPathLogErr(), dateFormat.format(date), 
				GeneraArchivos.class.getName());
		try {
			fhBuscaArchivos = new FileHandler(nomArc);
		} catch (SecurityException | IOException e) {
			throw new GeneraArchivosExc(e.toString(), e);
		}
		logGeneraArchivos.addHandler(fhBuscaArchivos);
		SimpleFormatter formatter = new SimpleFormatter();
		fhBuscaArchivos.setFormatter(formatter);
		fhBuscaArchivos.setLevel(Level.ALL);
		logGeneraArchivos.setLevel(Level.ALL);		
	}
	
	private static void obtenerConfiguracion() throws GeneraArchivosExc {
		ProcessFileCtrl cfg = new ProcessFileCtrl();
		try {
			configuracion = cfg.findConfiguration();
		} catch (ProcessFileCtrlExc e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
	}
	
	private static void creaCarpeta(String carpeta) {
    	File directory = new File(carpeta);
		if (! directory.exists()) {
			directory.mkdirs();
		}		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		/*
		 *	Clear the text components.
		 */
        //displayArea.setText("");
        //typingArea.setText("");
         
        /*
         *	Return the focus to the typing area.
         */
        typingArea.requestFocusInWindow();
    }

	@Override
	public void keyPressed(KeyEvent arg0) {
		displayInfo(arg0, "KEY PRESSED: ");
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		displayInfo(arg0, "KEY RELEASED: ");
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		displayInfo(arg0, "KEY TYPED: ");
	}
	
    /**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        /*
         *	Create and set up the window.
         */
    	GeneraArchivos frame = new GeneraArchivos("GeneraArchivos");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
         
        /*
         *	Set up the content pane.
         */
        frame.addComponentsToPane();
         
        /*
         *	Display the window.
         */
        frame.pack();
        frame.setVisible(true);
    }	
    
    private void addComponentsToPane() {
        
        //JButton button = new JButton("Limpiar");
        //button.addActionListener(this);
        JButton btnProceso = new JButton("Proceso");
        btnProceso.addActionListener(this);
         
        typingArea = new JTextField(20);
        typingArea.addKeyListener(this);
         
        /*
         *	Uncomment this if you wish to turn off focus
         *	traversal.  The focus subsystem consumes
         *	focus traversal keys, such as Tab and Shift Tab.
         *	If you uncomment the following line of code, this
         *	disables focus traversal and the Tab events will
         *	become available to the key event listener.
         *	typingArea.setFocusTraversalKeysEnabled(false);
         */
        
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);
        scrollPane.setPreferredSize(new Dimension(375, 125));
         
        getContentPane().add(typingArea, BorderLayout.PAGE_START);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        //getContentPane().add(button, BorderLayout.PAGE_END);
        getContentPane().add(btnProceso, BorderLayout.PAGE_END);
    }    
    
    public GeneraArchivos(String name) {
        super(name);
    }    
    
    /*
     * We have to jump through some hoops to avoid
     * trying to print non-printing characters
     * such as Shift.  (Not only do they not print,
     * but if you put them in a String, the characters
     * afterward won't show up in the text area.)
     */
    private void displayInfo(KeyEvent e, String keyStatus){
         
    	/*
    	 *	You should only rely on the key char if the event
    	 *	is a key typed event.
    	 */
        int id = e.getID();
        String keyString;
        int keyCode = 0;
        
        if (id == KeyEvent.KEY_TYPED) {
            char c = e.getKeyChar();
            keyString = "key character = '" + c + "'";
        } else {
            keyCode = e.getKeyCode();
            keyString = "key code = " + keyCode + " (" + KeyEvent.getKeyText(keyCode) + ")";
        }
         
        int modifiersEx = e.getModifiersEx();
        String modString = "extended modifiers = " + modifiersEx;
        String tmpString = KeyEvent.getModifiersExText(modifiersEx);
        
        if (tmpString.length() > 0) {
            modString += " (" + tmpString + ")";
        } else {
            modString += " (no extended modifiers)";
        }
         
        String actionString = "action key? ";
        
        if (e.isActionKey()) {
            actionString += "YES";
        } else {
            actionString += "NO";
        }
         
        String locationString = "key location: ";
        int location = e.getKeyLocation();
        
        if (location == KeyEvent.KEY_LOCATION_STANDARD) {
            locationString += "standard";
        } else if (location == KeyEvent.KEY_LOCATION_LEFT) {
            locationString += "left";
        } else if (location == KeyEvent.KEY_LOCATION_RIGHT) {
            locationString += "right";
        } else if (location == KeyEvent.KEY_LOCATION_NUMPAD) {
            locationString += "numpad";
        } else {
        	/*
        	 *	(location == KeyEvent.KEY_LOCATION_UNKNOWN)
        	 */
        	
            locationString += "unknown";
        }
         
        displayArea.append(
        		keyStatus + NEWLINE + "    " + 
        		keyString + NEWLINE + "    " + 
        		modString + NEWLINE + "    " + 
        		actionString + NEWLINE + "    " + 
        		locationString + NEWLINE);
        
        displayArea.setCaretPosition(displayArea.getDocument().getLength());
        
        if (keyCode==27) {
        	salida=true;
        }
    }

    private static String generaReporteJasper() throws GeneraArchivosExc {

		JasperPrint jasperPrint;
		try {
			jasperPrint = GeneraArchivos.archivoJasperPrint();
		} catch (GeneraArchivosExc e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
		
		String fileName = generaNombreArchivo();
		
        BufferedImage image;
		try {
			image = (BufferedImage) JasperPrintManager.printPageToImage(jasperPrint, 0, 1f);
		} catch (JRException e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}   
        try {
			ImageIO.write(image, "jpg", archivoDeSalida(configuracion.getFolder().getJasperReportsJPG()+fileName + "." + "jpg"));
		} catch (IOException | GeneraArchivosExc e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
        //JasperViewer.viewReport(jasperPrint, false);		
        
        return fileName;
    }
    
    private static JasperPrint archivoJasperPrint() throws GeneraArchivosExc {
		IBaseDatosConexion baseDatosConexion = BaseDatosFactory.getBaseDatosConexion(BDEnum.BD_SQL_SERVER);
		try (Connection conn = baseDatosConexion.getConnection()) {
			Map<String, Object> parameters = new HashMap<String, Object>();
			parameters.put("", "");
			JasperReport report;
			report = JasperCompileManager.compileReport(configuracion.getFolder().getJasperReports()+"GeneraArchivos.jrxml");
			return JasperFillManager.fillReport(report, parameters, conn);
		}  catch (Exception e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
    }
    
    private static String generaNombreArchivo() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS");
		Date date = new Date();
		return dateFormat.format(date);
    }
    
    private static OutputStream archivoDeSalida(String fileName) throws GeneraArchivosExc {
    	try {
			return new FileOutputStream(fileName);
		} catch (FileNotFoundException e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
    }
    
    private static void convierteJasperToJPG(String fileName) throws GeneraArchivosExc {
		File fileJPG = new File(configuracion.getFolder().getJasperReportsJPG()+fileName + "." + "jpg");
		BufferedImage image;
		try {
			image = ImageIO.read(fileJPG);
		} catch (IOException e1) {
			logGeneraArchivos.log(Level.INFO, e1.toString(), e1);
			throw new GeneraArchivosExc(e1.toString(), e1);
		}
		
		String filenameTif = configuracion.getFolder().getTemporalTIF()+fileName+"."+"tif";
		File tiffFile = new File(filenameTif);
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
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}		
    }
    
    private static void convierteMultiTiffToMultiPageTiff(String fileName) throws GeneraArchivosExc {
    	
    	int numero = (int) (Math.random() * 8) + 1;
    	if(numero==0) {
    		numero=1;
    	}
    	numero++;
    	String[] archivos = new String[numero];
    	archivos[0]=configuracion.getFolder().getTemporalTIF()+fileName+"."+"tif";
    	for(int i=1;i<numero;i++) {
    		int numImg = (int) (Math.random() * 20) + 1;
    		archivos[i]=configuracion.getFolder().getSourceTIF()+numImg+".tif";
    	}
    	
        int numTifs = archivos.length;

        BufferedImage image[] = new BufferedImage[numTifs];
        for (int i = 0; i < numTifs; i++) {
            SeekableStream ss;
			try {
				ss = new FileSeekableStream(archivos[i]);
			} catch (IOException e2) {
				logGeneraArchivos.log(Level.INFO, e2.toString(), e2);
				throw new GeneraArchivosExc(e2.toString(), e2);
			}
            ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
            PlanarImage pi;
			try {
				pi = new NullOpImage(decoder.decodeAsRenderedImage(0),null,null,OpImage.OP_IO_BOUND);
			} catch (IOException e1) {
				logGeneraArchivos.log(Level.INFO, e1.toString(), e1);
				throw new GeneraArchivosExc(e1.toString(), e1);
			}
            image[i] = pi.getAsBufferedImage();
            try {
				ss.close();
			} catch (IOException e) {
				logGeneraArchivos.log(Level.INFO, e.toString(), e);
				throw new GeneraArchivosExc(e.toString(), e);
			}
        }

        TIFFEncodeParam params = new TIFFEncodeParam();
        params.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
        OutputStream out;
		try {
			out = new FileOutputStream(configuracion.getFolder().getEntrada()+fileName+"."+"tif");
		} catch (FileNotFoundException e2) {
			logGeneraArchivos.log(Level.INFO, e2.toString(), e2);
			throw new GeneraArchivosExc(e2.toString(), e2);
		}
        ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, params);
        List <BufferedImage>list = new ArrayList<BufferedImage>(image.length);
        for (int i = 1; i < image.length; i++) {
            list.add(image[i]);
        }
        params.setExtraImages(list.iterator());
        try {
			encoder.encode(image[0]);
		} catch (IOException e1) {
			logGeneraArchivos.log(Level.INFO, e1.toString(), e1);
			throw new GeneraArchivosExc(e1.toString(), e1);
		}
        try {
			out.close();
		} catch (IOException e) {
			logGeneraArchivos.log(Level.INFO, e.toString(), e);
			throw new GeneraArchivosExc(e.toString(), e);
		}
    	
    }
    
}
