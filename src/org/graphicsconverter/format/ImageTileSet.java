package org.graphicsconverter.format;

import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.graphicsconverter.Tile;
import org.graphicsconverter.TileSet;

public class ImageTileSet implements TileSet {
	
	protected String imageFormat;

	protected BufferedImage data;
	
	protected int nbTiles, bpp;
	
	public ImageTileSet(String imageFormat, BufferedImage data)
	{
		this.imageFormat = imageFormat;
		this.data = data;
		
		int w = data.getWidth()/8;
		int h = data.getHeight()/8;
		nbTiles = w*h;
		
		int nColors = ((IndexColorModel)data.getColorModel()).getMapSize()-1;
		bpp = 0;
		while (nColors>0) {
			nColors >>= 1;
			bpp++;
		}
	}
	
	@Override
	public int getNbTiles()
	{
		return nbTiles;
	}
	
	@Override
	public int getBPP()
	{
		return bpp;
	}
	
	@Override
	public void toFile(File f) throws IOException
	{
		ImageIO.write(data, imageFormat, f);
	}
	


	protected int[] getPalette()
	{
		if (!(data.getColorModel() instanceof IndexColorModel)) {
			throw new Error("cannot decode non indexed images");
		}
		IndexColorModel m = (IndexColorModel)data.getColorModel();
		int[] palette = new int[m.getMapSize()];
		m.getRGBs(palette);
		return palette;
	}

	protected int getPaletteIndex(int[] palette, int rgb)
	{
		for (int i=0; i<palette.length; i++) {
			if (palette[i]==rgb) {
				return i;
			}
		}
		throw new Error("color not found");
	}
	
	
	@Override
	public Tile readTile(int index)
	{
		Tile t = new Tile();

		int x = (index%(data.getWidth()/8))*8;
		int y = (index/(data.getWidth()/8))*8;
		int[] palette = getPalette();
		
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				t.pixels[i][j] = getPaletteIndex(palette, data.getRGB(x+i, y+j));
			}
			
		}
		return t;
	}
	
	@Override
	public void writeTile(int index, Tile tile)
	{
		int x = (index%(data.getWidth()/8))*8;
		int y = (index/(data.getWidth()/8))*8;
		int[] palette = getPalette();
		
		for (int i=0; i<8; i++) {
			for (int j=0; j<8; j++) {
				data.setRGB(x+i, y+j, palette[tile.pixels[i][j]]);
			}
		}
	}
}
