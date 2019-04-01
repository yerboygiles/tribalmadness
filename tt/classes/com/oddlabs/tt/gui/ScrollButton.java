package com.oddlabs.tt.gui;

import org.lwjgl.input.Keyboard;

public final strictfp class ScrollButton extends GUIObject {
	public ScrollButton() {
		setCanFocus(true);
//		setupPos();
	}

	public void setupPos(ScrollBar owner) {
		setPos(owner.getButtonX(), owner.getButtonY());
		setDim(Skin.getSkin().getScrollBarData().getScrollButton().getWidth(), owner.getButtonHeight());
	}

        @Override
	public void keyPressed(KeyboardEvent event) {
	}

        @Override
	public void keyRepeat(KeyboardEvent event) {
		switch (event.getKeyCode()) {
			case Keyboard.KEY_TAB:
				super.keyRepeat(event);
				break;
		}
	}

        @Override
	public void keyReleased(KeyboardEvent event) {
	}

        @Override
	protected void renderGeometry() {
		Vertical button = Skin.getSkin().getScrollBarData().getScrollButton();
		if (isDisabled()) {
			button.render(0, 0, getHeight(), Skin.DISABLED);
		} else if (isActive()) {
			button.render(0, 0, getHeight(), Skin.ACTIVE);
		} else {
			button.render(0, 0, getHeight(), Skin.NORMAL);
		}
	}

        @Override
	public void mouseClicked(int button, int x, int y, int clicks) {
	}
}
