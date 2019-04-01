package com.oddlabs.tt.gui;

import com.oddlabs.tt.guievent.CheckBoxListener;
import com.oddlabs.tt.util.ToolTip;

public final strictfp class CheckBox extends GUIObject implements ToolTip {
	private final static int CHECK_BOX_LISTENER = 0;
	private final java.util.List[] event_listeners = new java.util.ArrayList[1];

	private final String tool_tip;
	
	private boolean marked;
	private boolean pressed = false;

	public CheckBox(boolean marked, String text) {
		this(marked, text, "");
	}
	
	public CheckBox(boolean marked, String text, String tool_tip) {
		this.marked = marked;
		this.tool_tip = tool_tip;
		Label label = new Label(text, Skin.getSkin().getEditFont());
		addChild(label);
		label.setPos(Skin.getSkin().getCheckBoxMarked()[Skin.NORMAL].getWidth(), (Skin.getSkin().getCheckBoxMarked()[Skin.NORMAL].getHeight() - label.getHeight())/2);
		event_listeners[CHECK_BOX_LISTENER] = new java.util.ArrayList();
		setDim(Skin.getSkin().getCheckBoxMarked()[Skin.NORMAL].getWidth() + label.getWidth(), Skin.getSkin().getCheckBoxMarked()[Skin.NORMAL].getHeight());
		setCanFocus(true);
	}

	public boolean isMarked() {
		return marked;
	}

	public void setMarked(boolean marked) {
		if (marked != this.marked) {
			this.marked = marked;
			checkedAll(marked);
		}
	}
	
        @Override
	public void appendToolTip(ToolTipBox tool_tip_box) {
		tool_tip_box.append(tool_tip);
	}

	private void toggleMarked() {
		marked = !marked;
		checkedAll(marked);
	}

        @Override
	protected void mouseClicked(int button, int x, int y, int clicks) {
		toggleMarked();
	}

        @Override
	protected void mouseReleased(int button, int x, int y) {
		pressed = false;
	}

        @Override
	protected void mousePressed(int button, int x, int y) {
		pressed = true;
	}

        @Override
	protected void renderGeometry() {
		if (isDisabled()) {
			if (marked)
				Skin.getSkin().getCheckBoxMarked()[Skin.DISABLED].render(0, 0);
			else
				Skin.getSkin().getCheckBoxUnmarked()[Skin.DISABLED].render(0, 0);
		} else if (isActive()) {
			if (marked) {
				if (pressed && isHovered())
					Skin.getSkin().getCheckBoxUnmarked()[Skin.ACTIVE].render(0, 0);
				else
					Skin.getSkin().getCheckBoxMarked()[Skin.ACTIVE].render(0, 0);
			} else {
				if (pressed && isHovered())
					Skin.getSkin().getCheckBoxMarked()[Skin.ACTIVE].render(0, 0);
				else
					Skin.getSkin().getCheckBoxUnmarked()[Skin.ACTIVE].render(0, 0);
			}
		} else {
			if (marked)
				Skin.getSkin().getCheckBoxMarked()[Skin.NORMAL].render(0, 0);
			else
				Skin.getSkin().getCheckBoxUnmarked()[Skin.NORMAL].render(0, 0);
		}
	}

	public void checkedAll(boolean marked) {
		checked(marked);
		java.util.List list = getCheckBoxListeners();
		for (int i = 0; i < list.size(); i++) {
			CheckBoxListener listener = (CheckBoxListener)list.get(i);
			if (listener != null)
				listener.checked(marked);
		}
	}

	void checked(boolean marked) {
/*
		GUIObject parent = (GUIObject)getParent();
		if (parent != null)
			parent.checkedAll(marked);
*/
	}

	public void addCheckBoxListener(CheckBoxListener listener) {
		event_listeners[CHECK_BOX_LISTENER].add(listener);
	}

	private java.util.List getCheckBoxListeners() {
		return event_listeners[CHECK_BOX_LISTENER];
	}

	public void removeCheckBoxListener(CheckBoxListener listener) {
		event_listeners[CHECK_BOX_LISTENER].remove(listener);
	}
}
