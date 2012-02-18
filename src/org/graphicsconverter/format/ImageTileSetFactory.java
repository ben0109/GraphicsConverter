package org.graphicsconverter.format;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.graphicsconverter.TileSet;
import org.graphicsconverter.TileSetFactory;

public class ImageTileSetFactory implements TileSetFactory {

	private static final IndexColorModel MODEL_1BPP = buildModel(1, 0xff);
	private static final IndexColorModel MODEL_2BPP = buildModel(2, 0x55);
	private static final IndexColorModel MODEL_4BPP = buildModel(4, 0x11);
	private static final IndexColorModel MODEL_8BPP = buildModel(4, 0x01);
	
	private static IndexColorModel buildModel(int n, int t)
	{
		byte[] a = new byte[1<<n];
		for (int i=0; i<1<<n; i++) {
			a[i] = (byte)(i*t);
		}
		return new IndexColorModel(n,1<<n,a,a,a);
	}
	
	private String imageFormat;
	
	public ImageTileSetFactory(String imageFormat)
	{
		this.imageFormat = imageFormat;
	}
	
	@Override
	public TileSet create(int bpp, int nbTiles)
	{		
		int width,height;
		if (nbTiles<16) {
			width = nbTiles*8;
			height = 8;
		} else {
			width = 16*8;
			height = ((nbTiles+15)/16)*8;
		}
		
		IndexColorModel cm;
		switch (bpp) {
		case 1: cm = MODEL_1BPP; break;
		case 2: cm = MODEL_2BPP; break;
		case 4: cm = MODEL_4BPP; break;
		case 8: cm = MODEL_8BPP; break;
		default: throw new Error("unsupported bit depth: "+bpp);
		}
		
		return new ImageTileSet(imageFormat, new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY, cm));
	}
	
	@Override
	public TileSet fromFile(File f) throws IOException
	{
		BufferedImage data = ImageIO.read(f);
		return new ImageTileSet(imageFormat, data);
	}
}
