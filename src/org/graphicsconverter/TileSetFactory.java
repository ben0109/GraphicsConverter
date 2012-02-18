package org.graphicsconverter;

import java.io.File;
import java.io.IOException;


public interface TileSetFactory
{	
	TileSet create(int bpp, int nbTiles);

	TileSet fromFile(File f) throws IOException;
}
