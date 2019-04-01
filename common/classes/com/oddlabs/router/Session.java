package com.oddlabs.router;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

final strictfp class Session {
	public final SessionInfo info;
	public final SessionID session_id;
	private final Logger logger;
	private final Set players = new HashSet();
	private final SessionManager manager;
	private long initial_time;

	private boolean started;

	Session(Logger logger, SessionID session_id, SessionInfo info, SessionManager manager) {
		this.logger = logger;
		this.manager = manager;
		this.session_id = session_id;
		this.info = info;
	}

	boolean hasClient(final int client_id) {
		final boolean[] result = new boolean[1];
		visit((RouterClient client) -> {
                    if (client.getClientID() == client_id)
                        result[0] = true;
                });
		return result[0];
	}

	void removePlayer(RouterClient client) {
		players.remove(client);
		if (getNumPlayers() == 0)
			manager.remove(this);
		checksum();
	}

	void checksum() {
		final Map checksum_to_count = new HashMap();
		final int[] best_checksum = new int[1];
		final boolean[] missing_checksum = new boolean[1];
		visit(new SessionVisitor() {
			private int best_checksum_count = 0;

                        @Override
			public final void visit(RouterClient client) {
				if (client.getChecksums().isEmpty()) {
					missing_checksum[0] = true;
					return;
				}
				Integer client_checksum = (Integer)client.getChecksums().get(0);
				Integer count = (Integer)checksum_to_count.get(client_checksum);
				if (count == null) {
					count = 1;
				} else {
					count = count + 1;
				}
				checksum_to_count.put(client_checksum, count);
				if (best_checksum_count < count) {
					best_checksum[0] = client_checksum;
					best_checksum_count = count;
				}
			}
		});
		if (checksum_to_count.size() == 1)
			return;
		final List clients_to_be_kicked = new ArrayList();
		visit((RouterClient client) -> {
                    Integer client_checksum;
                    if (missing_checksum[0]) {
                        if (client.getChecksums().isEmpty())
                            return;
                        client_checksum = (Integer)client.getChecksums().get(0);
                    } else
                        client_checksum = (Integer)client.getChecksums().remove(0);
                    if (client_checksum != best_checksum[0]) {
                        logger.log(Level.WARNING, "Kicking client because of checksum error: {0} != {1}", new Object[]{client_checksum, best_checksum[0]});
                        clients_to_be_kicked.add(client);
                    }
                });
		for (int i = 0; i < clients_to_be_kicked.size(); i++) {
			RouterClient client = (RouterClient)clients_to_be_kicked.get(i);
			client.doError(true, new IOException("Checksum mismatch"));
		}
	}

	void addPlayer(RouterClient client) {
		players.add(client);
		if (info.num_participants == players.size())
			start();
	}

	int getNextTick() {
		return manager.getNextTick(this);
	}

	void startTimeout(RouterClient client) {
		manager.startTimeout(client);
	}

	private void start() {
		this.started = true;
		visit((RouterClient client) -> {
                    client.getInterface().start();
                });
		this.initial_time = manager.start(this);
	}

	long getInitialTime() {
		return initial_time;
	}

	boolean isComplete() {
		return started;
	}

	void visit(SessionVisitor visitor) {
		Iterator it = players.iterator();
		while (it.hasNext()) {
			RouterClient client = (RouterClient)it.next();
			visitor.visit(client);
		}
	}

	int getNumPlayers() {
		return players.size();
	}

        @Override
	public String toString() {
		String result = "(Session: info = " + info + " players : (";
		Iterator it = players.iterator();
		while (it.hasNext()) {
			result += it.next().toString() + " ";
		}
		return result + "))";
	}
}
