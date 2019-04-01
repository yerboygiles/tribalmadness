package com.oddlabs.util;

import java.io.*;
import java.net.*;

public final strictfp class FontInfo implements Serializable {
	private final static long serialVersionUID = 1;

	private final String texture_name;
	private final Quad[] key_map;
	private final int x_border;
	private final int y_border;
	private final int font_height;

	public FontInfo(String texture_name, Quad[] key_map, int x_border, int y_border, int font_height) {
		this.texture_name = texture_name;
		this.key_map = key_map;
		this.x_border = x_border;
		this.y_border = y_border;
		this.font_height = font_height;
	}

	public String getTextureName() {
		return texture_name;
	}

	public Quad[] getKeyMap() {
		return key_map;
	}

	public int getBorderX() {
		return x_border;
	}

	public int getBorderY() {
		return y_border;
	}

	public int getHeight() {
		return font_height;
	}

	public void saveToFile(String file_name) {
		try (ObjectOutputStream os = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file_name)))) {
			os.writeObject(this);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static FontInfo loadFromFile(URL url) {
		return (FontInfo)Utils.loadObject(url);
	}
}
