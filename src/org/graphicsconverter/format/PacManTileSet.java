package org.graphicsconverter.format;

import org.graphicsconverter.Tile;

public class PacManTileSet extends BinaryTileSet
{
	public PacManTileSet(byte[] data, int offset)
	{
		super(2, data, offset);
	}

	@Override
	public Tile readTile(int index)
	{
		Tile t = new Tile();
		int offset = getOffsetForTile(index);
		for (int i=0; i<8; i++) {
			int h,l;
			h = data[offset+7-i+8]&0xff;
			l = data[offset+7-i+0]&0xff;
			for (int j=0; j<4; j++) {
				t.pixels[i][j+0] = (getBit(h,7-j)<<1) | getBit(h,3-j);
				t.pixels[i][j+4] = (getBit(l,7-j)<<1) | getBit(l,3-j);
			}
		}
		return t;
	}
	
	@Override
	public void writeTile(int index, Tile tile)
	{
		int[][] c = tile.pixels;
		int offset = getOffsetForTile(index);
		for (int i=0; i<8; i++) {
			int h,l;
			h = (getBit(c[i][0],1)<<7)
			  | (getBit(c[i][1],1)<<6)
			  | (getBit(c[i][2],1)<<5)
			  | (getBit(c[i][3],1)<<4)
			  | (getBit(c[i][0],0)<<3)
			  | (getBit(c[i][1],0)<<2)
			  | (getBit(c[i][2],0)<<1)
			  | (getBit(c[i][3],0)<<0);
			l = (getBit(c[i][4],1)<<7)
			  | (getBit(c[i][5],1)<<6)
			  | (getBit(c[i][6],1)<<5)
			  | (getBit(c[i][7],1)<<4)
			  | (getBit(c[i][4],0)<<3)
			  | (getBit(c[i][5],0)<<2)
			  | (getBit(c[i][6],0)<<1)
			  | (getBit(c[i][7],0)<<0);
			data[offset+7-i+8] = (byte)h;
			data[offset+7-i+0] = (byte)l;
		}
	}
}
