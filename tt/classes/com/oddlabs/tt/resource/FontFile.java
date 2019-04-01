package com.oddlabs.tt.resource;

import com.oddlabs.tt.font.Font;
import com.oddlabs.util.FontInfo;

public final strictfp class FontFile extends File<Font> {

    public FontFile(String file_name) {
        super(file_name);
    }

    @Override
    public Font newInstance() {
        FontInfo font_info = FontInfo.loadFromFile(getURL());
        return new Font(font_info);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof FontFile) && super.equals(o);
    }
}
