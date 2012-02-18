package org.graphicsconverter.format;

import org.graphicsconverter.Tile;

public class Planar2BPPTileSet extends BinaryTileSet
{
	public Planar2BPPTileSet(byte[] data, int offset) {
		super(2, data, offset);
	}

	@Override
	public Tile readTile(int index)
	{
		Tile r = new Tile();
		int offset = getOffsetForTile(index);
		for (int j=0; j<8; j++) {
			for (int p=0; p<2; p++) {
				int b = data[offset+2*j+p];
				for (int i=0; i<8; i++) {
					r.pixels[i][j] |= getBit(b,7-i)<<p;
				}
			}
		}
		return r;
	}

	@Override
	public void writeTile(int index, Tile tile)
	{
		int offset = getOffsetForTile(index);
		for (int j=0; j<8; j++) {
			for (int p=0; p<2; p++) {
				int b = 0;
				for (int i=0; i<8; i++) {
					b |= getBit(tile.pixels[i][j],p)<<(7-i);
				}
				data[offset+2*j+p] = (byte)b;
			}
		}
	}

}
