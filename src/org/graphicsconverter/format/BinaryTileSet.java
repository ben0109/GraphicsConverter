package org.graphicsconverter.format;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.graphicsconverter.TileSet;

public abstract class BinaryTileSet implements TileSet {
	
	protected int bpp,offset;

	protected byte[] data;
	
	public BinaryTileSet(int bpp, byte[] data, int offset)
	{
		this.bpp = bpp;
		this.data = data;
		this.offset = offset;
	}
	
	@Override
	public int getBPP()
	{
		return bpp;
	}
	
	@Override
	public int getNbTiles()
	{
		return (data.length-offset)/getTileSize();
	}

	protected int getTileSize() {
		return 8*bpp;
	}

	protected int getOffsetForTile(int index) {
		return offset+index*getTileSize();
	}
	
	@Override
	public void toFile(File f) throws IOException
	{
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(data);
		fos.close();
	}

	protected static int getBit(int i, int n)
	{
		return (i>>n)&1;
	}
}
