package com.oddlabs.http;

import java.io.IOException;

public abstract strictfp class DefaultHttpCallback implements HttpCallback {
        @Override
	public final void error(int error_code, String error_message) {
		error(new IOException("HTTP error code: " + error_code + " " + error_message));
	}
}
