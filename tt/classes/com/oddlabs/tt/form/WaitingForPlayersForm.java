package com.oddlabs.tt.form;

import com.oddlabs.tt.gui.CancelListener;
import com.oddlabs.tt.gui.Form;
import com.oddlabs.tt.gui.HorizButton;
import com.oddlabs.tt.gui.KeyboardEvent;
import com.oddlabs.tt.gui.Label;
import com.oddlabs.tt.gui.Skin;
import com.oddlabs.tt.guievent.MouseClickListener;
import com.oddlabs.tt.util.Utils;
import com.oddlabs.tt.viewer.WorldViewer;
import java.util.ResourceBundle;
import org.lwjgl.input.Keyboard;

public final strictfp class WaitingForPlayersForm extends Form {
	private final Label info_label;
	private final ResourceBundle bundle = ResourceBundle.getBundle(WaitingForPlayersForm.class.getName());
	private final WorldViewer viewer;
	
	public WaitingForPlayersForm(WorldViewer viewer) {
		this.viewer = viewer;
		info_label = new Label(Utils.getBundleString(bundle, "waiting"), Skin.getSkin().getHeadlineFont());
		info_label.setDim(280, info_label.getHeight());
		HorizButton abort_button = new HorizButton(Utils.getBundleString(bundle, "abort"), 120);
		abort_button.addMouseClickListener(new AbortListener());
		addChild(info_label);
		addChild(abort_button);
		info_label.place();
		abort_button.place(ORIGIN_BOTTOM_RIGHT);
		compileCanvas();
		centerPos();
	}

        @Override
	protected void keyRepeat(KeyboardEvent event) {
		if (event.getKeyCode() != Keyboard.KEY_ESCAPE) // KEY_ESCAPE should not close this form
			super.keyRepeat(event);
	}

        @Override
	protected void doCancel() {
		viewer.close();
	}

	private final strictfp class AbortListener implements MouseClickListener {
                @Override
		public void mouseClicked(int button, int x, int y, int clicks) {
			viewer.getGUIRoot().addModalForm(new QuestionForm(Utils.getBundleString(bundle, "confirm_abort"), new CancelListener(WaitingForPlayersForm.this)));
		}
	}
}
