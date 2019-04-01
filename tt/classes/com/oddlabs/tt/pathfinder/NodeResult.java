package com.oddlabs.tt.pathfinder;

public final strictfp class NodeResult {
	private final Node result;

	public NodeResult(Node node) {
		this.result = node;
	}

	public Node get() {
		return result;
	}
}
