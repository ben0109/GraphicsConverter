package org.graphicsconverter.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import org.graphicsconverter.TileSet;
import org.graphicsconverter.TileSetFactory;

public class BinaryTileSetFactory implements TileSetFactory {
	
	protected int bpp,offset;
	protected Class<? extends TileSet> cls;

	public BinaryTileSetFactory(int bpp, Class<? extends TileSet> cls)
	{
		this.bpp = bpp;
		this.cls = cls;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	@Override
	public TileSet create(int wantedBpp, int nbTiles)
	{
		if (bpp<wantedBpp) {
			throw new Error("incompatible formats");
		}
		byte[] data = new byte[offset + (nbTiles*8*bpp)];
		return createTileSet(bpp, data, offset);
	}
	
	@Override
	public TileSet fromFile(File f) throws IOException
	{
		int len = (int)f.length();
		byte[] data = new byte[len];

		FileInputStream fis = new FileInputStream(f);
		int offset = 0;
		while (offset<len) {
			int n = fis.read(data,offset,4096);
			if (n<0) {
				throw new Error("unexpected end of file");
			}
			offset += n;
		}
		fis.close();
		
		return createTileSet(bpp, data, this.offset);
	}

	protected TileSet createTileSet(int bpp, byte[] data, int offset) {
		try {
			return cls.getConstructor(byte[].class,int.class).newInstance(data,offset);
		} catch (Exception e) {
			throw new Error(e);
		}
	}
}
