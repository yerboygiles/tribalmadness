package com.oddlabs.tt.form;

import com.oddlabs.net.NetworkSelector;
import com.oddlabs.tt.audio.AudioManager;
import com.oddlabs.tt.delegate.CameraDelegate;
import com.oddlabs.tt.delegate.NullDelegate;
import com.oddlabs.tt.event.LocalEventQueue;
import com.oddlabs.tt.gui.Fadable;
import com.oddlabs.tt.gui.GUI;
import com.oddlabs.tt.gui.GUIImage;
import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.gui.LabelBox;
import com.oddlabs.tt.gui.LocalInput;
import com.oddlabs.tt.gui.ProgressBar;
import com.oddlabs.tt.gui.ProgressBarInfo;
import com.oddlabs.tt.gui.Skin;
import com.oddlabs.tt.render.UIRenderer;
import com.oddlabs.tt.util.Utils;
import java.util.Random;
import java.util.ResourceBundle;
import org.lwjgl.openal.AL;

public final strictfp class ProgressForm {
	private final static int PROGRESSBAR_LOADINGTIP_SPACING = 45;
	private final static int NUM_TIPS = 39;
	private final static String TIP_PREFIX = "tip";
	private final static String[] LOADING_TIPS;

	private static ProgressForm current_progress = null;

	private final ProgressBar progress_bar;
	private final GUIImage image;

	static {
		LOADING_TIPS = new String[NUM_TIPS];
		ResourceBundle bundle = ResourceBundle.getBundle(ProgressForm.class.getName());
		for (int i = 0; i < LOADING_TIPS.length; i++)
			LOADING_TIPS[i] = Utils.getBundleString(bundle, TIP_PREFIX + i);
	}

	public static void setProgressForm(NetworkSelector network, GUI gui, LoadCallback callback) {
		setProgressForm(network, gui, callback, false);
	}

	public static void setProgressForm(NetworkSelector network, final GUI gui, final LoadCallback callback, final boolean first_progress) {
		String texture;
		int texture_width;
		int texture_height;
		int image_width;
		int image_height;
		int progress_x;
		int progress_y;
		int progress_width;
		boolean show_tip;

		if (first_progress) {
			texture = "/textures/gui/oddlabs";
			texture_width = 1024;
			texture_height = 1024;
			image_width = 800;
			image_height = 600;
			progress_x = 320;
			progress_y = 145;
			progress_width = 200;
			show_tip = false;
		} else {
			texture = "/textures/gui/startup";
			texture_width = 1024;
			texture_height = 1024;
			image_width = 800;
			image_height = 600;
			progress_x = 250;
			progress_y = 145;
			progress_width = 300;
			show_tip = true;
		}

		Fadable load_fadable = () -> {
                    callback(gui, callback, first_progress);
                };
		current_progress = new ProgressForm(network, gui, load_fadable, first_progress, new ProgressBarInfo[]{new ProgressBarInfo(""/*"Loading landscape resources"*/, 10),
			new ProgressBarInfo(""/*"Loading races resources"*/, 30),
			new ProgressBarInfo(""/*"Generating textures"*/, 5),
			new ProgressBarInfo(""/*"Generating terrain"*/, 5),
			new ProgressBarInfo(""/*"Generating alpha maps"*/, 5),
			new ProgressBarInfo(""/*"Blending textures"*/, 2f),
			new ProgressBarInfo(""/*"Generating pathfinding grids"*/, 5),
			new ProgressBarInfo(""/*"Generating quadtrees"*/, 6)},
			texture, texture_width, texture_height, image_width, image_height, progress_x, progress_y, progress_width, show_tip);
		if (first_progress)
			load_fadable.fadingDone();
	}

	private ProgressForm(NetworkSelector network, final GUI gui, final Fadable load_fadable, boolean first_progress, ProgressBarInfo[] info, String texture_name, int texture_width, int texture_height, int image_width, int image_height, int progress_x, int progress_y, int progress_width, boolean show_tip) {
		if (AL.isCreated())
			AudioManager.getManager().stopSources();
		final GUIRoot gui_root;
		if (!first_progress) {
			gui_root = gui.newFade(load_fadable, null);
		} else {
			gui_root = gui.getGUIRoot();
		}
		CameraDelegate delegate = new NullDelegate(gui_root, false);
		gui_root.pushDelegate(delegate);
		int screen_width = LocalInput.getViewWidth();
		int screen_height = LocalInput.getViewHeight();
		progress_width = (int)(progress_width*(float)screen_width/image_width);
		progress_x = (int)(progress_x*(float)screen_width/image_width);
		progress_y = (int)(progress_y*(float)screen_height/image_height);

		image = new GUIImage(screen_width, screen_height, 0f, 0f, (float)image_width/texture_width, (float)image_height/texture_height, texture_name);
		image.setPos(0, 0);

		progress_bar = new ProgressBar(network, gui, progress_width, info, false);
		progress_y -= progress_bar.getHeight();
		progress_bar.setPos(progress_x, progress_y);
		delegate.addChild(image);
		delegate.addChild(progress_bar);
		if (show_tip) {
			Random random = new Random(LocalEventQueue.getQueue().getHighPrecisionManager().getTick());
//			CharSequence tip_string = LOADING_TIPS[7];
			CharSequence tip_string = LOADING_TIPS[random.nextInt(LOADING_TIPS.length)];
			int tip_width = Skin.getSkin().getEditFont().getWidth(tip_string);
			tip_width = StrictMath.min(LocalInput.getViewWidth() - 10, tip_width);
			LabelBox tip = new LabelBox(tip_string, Skin.getSkin().getEditFont(), tip_width);
//			Label tip = new Label(LOADING_TIPS[random.nextInt(LOADING_TIPS.length)], Skin.getSkin().getEditFont());
			tip.setPos(progress_bar.getX() + progress_bar.getWidth()/2 - tip.getWidth()/2, progress_bar.getY() - tip.getHeight() - PROGRESSBAR_LOADINGTIP_SPACING);
			delegate.addChild(tip);
		}
	}

	private static void callback(GUI gui, LoadCallback callback, boolean first_progress) {
		Fadable start_sources_fadable = () -> {
                    if (AL.isCreated())
                        AudioManager.getManager().startSources();
                };

		GUIRoot client_root = gui.createRoot();
		UIRenderer renderer = callback.load(client_root);
		gui.newFade(start_sources_fadable, client_root, renderer);
	}

	public static void progress() {
		current_progress.progress_bar.progress();
	}

	public static void progress(float step) {
		current_progress.progress_bar.progress(step);
	}
}
