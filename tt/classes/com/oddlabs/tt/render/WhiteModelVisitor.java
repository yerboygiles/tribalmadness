package com.oddlabs.tt.render;

abstract strictfp class WhiteModelVisitor extends ModelVisitor {
	private final static float[] COLOR_TEAM = {1f, 1f, 1f};

        @Override
	public final float[] getSelectionColor(ElementRenderState render_state) {
		return COLOR_TEAM;
	}

        @Override
	public final float[] getTeamColor(ElementRenderState render_state) {
		return COLOR_TEAM;
	}
}
