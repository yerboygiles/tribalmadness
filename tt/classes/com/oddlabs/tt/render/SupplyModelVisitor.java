package com.oddlabs.tt.render;

abstract strictfp class SupplyModelVisitor extends WhiteModelVisitor {
        @Override
	public final void markDetailPoint(ElementRenderState render_state) {
		markDetailPolygon(render_state, SpriteRenderer.LOW_POLY);
	}
}
