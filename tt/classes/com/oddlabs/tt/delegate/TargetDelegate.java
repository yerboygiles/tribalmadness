package com.oddlabs.tt.delegate;

import com.oddlabs.tt.camera.GameCamera;
import com.oddlabs.tt.gui.*;
import com.oddlabs.tt.viewer.WorldViewer;
import org.lwjgl.input.Keyboard;

public strictfp class TargetDelegate extends ControllableCameraDelegate {
	private final int action;

	public TargetDelegate(WorldViewer viewer, GameCamera camera, int action) {
		super(viewer, camera);
		this.action = action;
	}

        @Override
	public boolean canHoverBehind() {
		return true;
	}

        @Override
	protected final int getCursorIndex() {
		return GUIRoot.CURSOR_TARGET;
	}

        @Override
	public final void keyPressed(KeyboardEvent event) {
		getCamera().keyPressed(event);
		switch (event.getKeyCode()) {
			case Keyboard.KEY_ESCAPE:
				pop();
				break;
			case Keyboard.KEY_SPACE:
			case Keyboard.KEY_RETURN:
				break;
			default:
				super.keyPressed(event);
				break;
		}
	}

        @Override
	public void keyReleased(KeyboardEvent event) {
		if (event.getKeyCode() != Keyboard.KEY_SPACE || event.getKeyCode() != Keyboard.KEY_RETURN)
			getCamera().keyReleased(event);
	}

        @Override
	public void mousePressed(int button, int x, int y) {
		if (button == LocalInput.LEFT_BUTTON) {
			getViewer().getPicker().pickTarget(getViewer().getSelection().getCurrentSelection(), getViewer().getGUIRoot().getDelegate().getCamera().getState(), getViewer().getPeerHub().getPlayerInterface(), x, y, action);
			pop();
		} else {
			super.mousePressed(button, x, y);
		}
	}
}
