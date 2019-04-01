package com.oddlabs.tt.form;

import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.net.PeerHub;
import com.oddlabs.tt.render.Renderer;
import com.oddlabs.tt.util.Utils;
import java.util.ResourceBundle;

public final strictfp class QuitForm extends QuestionForm {
	private static String getI18N(String key) {
		ResourceBundle bundle = ResourceBundle.getBundle(QuitForm.class.getName());
		return Utils.getBundleString(bundle, key);
	}

	public QuitForm(final GUIRoot gui_root) {
		super(!PeerHub.isWaitingForAck() ? getI18N("confirm_quit") : getI18N("confirm_quit_waiting_for_ack"),
                (int button, int x1, int y1, int clicks) -> { Renderer.shutdown(); });
	}
}
