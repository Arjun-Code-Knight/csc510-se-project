package com.simplshot.ocr;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.simplshot.server.AppStart;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

public class OcrUtility {
	
	private static OcrUtility ocrUtil = new OcrUtility();
	private static final Logger LOGGER = Logger.getLogger(OcrUtility.class.getName());
	
	public static OcrUtility getInstance()
	{
		return ocrUtil;
	}
	
	public synchronized String processImage(String image)
	{
		ITesseract instance = new Tesseract();
		File testFile = new File(image);
		instance.setDatapath(AppStart.TESSDATA);
		try {
			String result = instance.doOCR(testFile);
			LOGGER.info("File "+image +"\nExtracted Text "+result);
			return result;
		} catch (TesseractException ex) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "Error processing Image on the server", ex);
			ex.printStackTrace();
		}
		return null;
	}
}
