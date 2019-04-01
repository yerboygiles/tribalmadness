package com.oddlabs.tt.resource;

import com.oddlabs.util.Utils;
import java.net.*;
import java.util.Objects;

public abstract strictfp class File<R> implements ResourceDescriptor<R> {

    private final URL url;

    protected File(URL url) {
        this.url = Objects.requireNonNull(url, "url");
    }

    protected File(String location) {
        this(Utils.makeURL(location));
    }

    public final URL getURL() {
        return url;
    }

    @Override
    public String toString() {
        return url.toString();
    }

    @Override
    public final int hashCode() {
        return url.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof File))
            return false;
        File other = (File) o;
        return url.equals(other.url);
    }
}
