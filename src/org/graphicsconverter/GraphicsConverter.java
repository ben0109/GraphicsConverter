package org.graphicsconverter;

import java.beans.BeanInfo;
import java.beans.Beans;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class GraphicsConverter
{
	public static void main(String[] args) throws IOException
	{
		Map<String, TileSetFactory> factories = loadFactories();
		
		String srcFile = null;
		String dstFile = null;
		TileSetFactory srcFactory = null;
		TileSetFactory dstFactory = null;
		TileSetFactory current = null;
		
		for (int i=0; i<args.length; i++) {
			if (args[i].equals("-i")) {
				if (i>=args.length-2) {
					usage();
					System.exit(1);
				}
				srcFile = args[++i];
				current = srcFactory = factories.get(args[++i]);
				if (srcFactory==null) {
					System.err.println("unknown format "+args[i]);
					System.exit(1);
				}
				
			} else if (args[i].equals("-o")) {
				if (i>=args.length-2) {
					usage();
					System.exit(1);
				}
				dstFile = args[++i];
				current = dstFactory = factories.get(args[++i]);
				if (dstFactory==null) {
					System.err.println("unknown format "+args[i]);
					System.exit(1);
				}
				
			} else if (args[i].equals("-s")) {
				if (i>=args.length-2) {
					usage();
					System.exit(1);
				}
				String key = args[++i];
				String value = args[++i];
				setFactoryParam(current,key,value);
			}
		}
		
		if (srcFile==null || dstFile==null || srcFactory==null || dstFactory==null) {
			usage();
			System.exit(1);
		}
		
		TileSet src = srcFactory.fromFile(new File(srcFile));
		TileSet dst = dstFactory.create(src.getBPP(), src.getNbTiles());
		for (int i=0; i<src.getNbTiles(); i++) {
			Tile t = src.readTile(i);
			dst.writeTile(i, t);
		}
		
		dst.toFile(new File(dstFile));
	}

	static void usage() {
		System.out.println("Usage: java org.graphicsconverter.GraphicsConverter -i file format -o file format");
	}
	
	private static void setFactoryParam(TileSetFactory current, String key, String value)
	{
		try {
			BeanInfo bi = Introspector.getBeanInfo(current.getClass());
	
			PropertyDescriptor pd = null;
			for (PropertyDescriptor pd2 : bi.getPropertyDescriptors()) {
				if (pd2.getName().equals(key)) {
					pd = pd2;
					break;
				}
			}
			if (pd==null) {
				System.err.println("warning: ignoring unknown property "+key);
				return;
			}
			
			Method setter = pd.getWriteMethod();
			if (setter==null) {
				System.err.println("warning: ignoring unmodifiable property "+key);
				return;
			}
	
			Class<?> type = pd.getPropertyType();
			Object convertedValue;
			if (type.equals(String.class)) {
				convertedValue = value;
			} else if (type.equals(int.class)) {
				convertedValue = Integer.decode(value);
			} else {
				System.err.println("warning: ignoring property with unsupported type "+type.getName());
				return;
			}
	
			setter.invoke(current, convertedValue);
			
		} catch (Exception e) {
			System.err.println("warning: error while setting "+key+", ignored");
		}
	}

	private static Map<String, TileSetFactory> loadFactories() throws IOException
	{
		Map<String,TileSetFactory> factories = new HashMap<String, TileSetFactory>();
		
		// open config file
		InputStream stream = GraphicsConverter.class.getClassLoader().getResourceAsStream("org/graphicsconverter/formats.txt");
		BufferedReader br = new BufferedReader(new InputStreamReader(stream));
		
		// read lines
		while (true) {
			String l = br.readLine();
			// end of file ?
			if (l==null) {
				break;
			}
			// skip empty lines
			if (l.length()==0) {
				continue;
			}
			// expected format is "xxx=xxx"
			String[] tokens = l.split("=");
			if (tokens.length!=2) {
				System.err.println("warning: ignoring config line '"+l+"'");
			} else {
				try {
					Class<?> cls = Class.forName(tokens[1]);
					if (TileSetFactory.class.isAssignableFrom(cls)) {
						factories.put(tokens[0], (TileSetFactory)cls.newInstance());
					} else {
						System.err.println("warning: class '"+tokens[1]+"' is not a TileSetFactory");
					}
				} catch (ClassNotFoundException e) {
					System.err.println("warning: unknown class '"+tokens[1]+"'");
				} catch (Exception e) {
					System.err.println("warning: error while instantiating '"+tokens[1]+"'");
				}
			}
		}
		return factories;
	}
}
