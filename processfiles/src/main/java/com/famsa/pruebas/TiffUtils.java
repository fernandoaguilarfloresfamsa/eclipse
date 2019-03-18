package com.famsa.pruebas;

import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.awt.image.BufferedImage;

import javax.media.jai.NullOpImage;
import javax.media.jai.OpImage;
import javax.media.jai.PlanarImage;

import com.sun.media.jai.codec.ImageEncoder;
import com.sun.media.jai.codec.SeekableStream;
import com.sun.media.jai.codec.FileSeekableStream;
import com.sun.media.jai.codec.ImageDecoder;
import com.sun.media.jai.codec.ImageCodec;
import com.sun.media.jai.codec.TIFFEncodeParam;

public class TiffUtils {

  public static void main(String[] args) throws Exception {

    String [] tifs = {
        "C:\\Users\\RDEFAGUILA\\Pictures\\OracleMagazine\\0001.tif",
        "C:\\Users\\RDEFAGUILA\\Pictures\\OracleMagazine\\0002.tif",
        "C:\\Users\\RDEFAGUILA\\Pictures\\OracleMagazine\\0003.tif",
        "C:\\Users\\RDEFAGUILA\\Pictures\\OracleMagazine\\0004.tif",
    };
    int numTifs = tifs.length;  // 2 pages

    BufferedImage image[] = new BufferedImage[numTifs];
    for (int i = 0; i < numTifs; i++) {
        SeekableStream ss = new FileSeekableStream(tifs[i]);
        ImageDecoder decoder = ImageCodec.createImageDecoder("tiff", ss, null);
        PlanarImage pi = new NullOpImage
            (decoder.decodeAsRenderedImage(0),null,null,OpImage.OP_IO_BOUND);
        image[i] = pi.getAsBufferedImage();
        ss.close();
    }

    TIFFEncodeParam params = new TIFFEncodeParam();
    params.setCompression(TIFFEncodeParam.COMPRESSION_DEFLATE);
    OutputStream out = new FileOutputStream("C:\\Users\\RDEFAGUILA\\Pictures\\OracleMagazine\\multipage.tif");
    ImageEncoder encoder = ImageCodec.createImageEncoder("tiff", out, params);
    List <BufferedImage>list = new ArrayList<BufferedImage>(image.length);
    for (int i = 1; i < image.length; i++) {
        list.add(image[i]);
    }
    params.setExtraImages(list.iterator());
    encoder.encode(image[0]);
    out.close();

    System.out.println("Done.");
  }
}