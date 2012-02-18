package org.graphicsconverter;

import java.io.File;
import java.io.IOException;


public interface TileSet
{
	int getBPP();

	int getNbTiles();
	
	Tile readTile(int index);
	
	void writeTile(int index, Tile tile);

	void toFile(File f) throws IOException;
}
