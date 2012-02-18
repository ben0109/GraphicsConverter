package org.graphicsconverter.format;

import org.graphicsconverter.Tile;

public class Planar1BPPTileSet extends BinaryTileSet
{
	public Planar1BPPTileSet(byte[] data, int offset) {
		super(1, data, offset);
	}

	@Override
	public Tile readTile(int index)
	{
		Tile r = new Tile();
		int offset = getOffsetForTile(index);
		for (int j=0; j<8; j++) {
			int b = data[offset+j];
			for (int i=0; i<8; i++) {
				r.pixels[i][j] = getBit(b,7-i);
			}
		}
		return r;
	}

	@Override
	public void writeTile(int index, Tile tile)
	{
		int offset = getOffsetForTile(index);
		for (int j=0; j<8; j++) {
			int b = 0;
			for (int i=0; i<8; i++) {
				b |= tile.pixels[i][j]<<(7-i);
			}
			data[offset+j] = (byte)b;
		}
	}

}
