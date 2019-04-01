package com.oddlabs.net;

import java.io.*;
import java.net.*;

public final strictfp class DNSTask implements Callable<InetSocketAddress> {
	private final String dns_name;
	private final int port;
	private final Connection connection;

	public DNSTask(String dns_name, int port, Connection conn) {
		this.dns_name = dns_name;
		this.port = port;
		this.connection = conn;
	}

        @Override
	public void taskCompleted(Object result) {
		connection.connect((SocketAddress)result);
	}

        @Override
	public void taskFailed(Exception e) {
		connection.dnsError((IOException)e);
	}

	/* WARNING: Potentially threaded and not deterministic. See Callable.java for details */
        @Override
	public InetSocketAddress call() throws Exception {
		InetAddress inet_address = InetAddress.getByName(dns_name);
		InetSocketAddress address = new InetSocketAddress(inet_address, port);
		return address;
	}
}

