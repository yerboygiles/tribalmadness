package com.oddlabs.tt.delegate;

import com.oddlabs.net.NetworkSelector;
import com.oddlabs.tt.camera.Camera;
import com.oddlabs.tt.form.*;
import com.oddlabs.tt.global.Settings;
import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.gui.MenuButton;
import com.oddlabs.tt.guievent.MouseClickListener;
import com.oddlabs.tt.net.Network;
import com.oddlabs.tt.util.Utils;

public final strictfp class MainMenu extends Menu {
	public MainMenu(NetworkSelector network, GUIRoot gui_root, Camera camera) {
		super(network, gui_root, camera);
		reload();
	}

	private void addGameTypeButtons() {
		MenuButton tutorial = new MenuButton(Utils.getBundleString(bundle, "tutorial"), COLOR_NORMAL, COLOR_ACTIVE);
		addChild(tutorial);
		tutorial.addMouseClickListener(new TutorialListener());
		MenuButton campaign_menu = new MenuButton(Utils.getBundleString(bundle, "campaign"), COLOR_NORMAL, COLOR_ACTIVE);
		addChild(campaign_menu);
		campaign_menu.addMouseClickListener(new CampaignListener());
		MenuButton single_player = new MenuButton(Utils.getBundleString(bundle, "skirmish"), COLOR_NORMAL, COLOR_ACTIVE);
		addChild(single_player);
		single_player.addMouseClickListener(new SinglePlayerListener());
		if (!Settings.getSettings().hide_multiplayer) {
			MenuButton multi_player = new MenuButton(Utils.getBundleString(bundle, "multiplayer"), COLOR_NORMAL, COLOR_ACTIVE);
			addChild(multi_player);
			multi_player.addMouseClickListener(new MultiPlayerListener());
		}
	}

        @Override
	protected void addButtons() {
		addGameTypeButtons();

		addDefaultOptionsButton();

		addExitButton();

		if (Network.getMatchmakingClient().isConnected()) {
			new SelectGameMenu(getNetwork(), getGUIRoot(), this);
		}
	}

	private final strictfp class MultiPlayerListener implements MouseClickListener {
                @Override
		public void mouseClicked(int button, int x, int y, int clicks) {
			if (Network.getMatchmakingClient().isConnected()) {
				new SelectGameMenu(getNetwork(), getGUIRoot(), MainMenu.this);
			} else {
				Network.getMatchmakingClient().close();
				new LoginForm(getNetwork(), getGUIRoot(), MainMenu.this);
			}
		}
	}

	private final strictfp class TutorialListener implements MouseClickListener {
                @Override
		public void mouseClicked(int button, int x, int y, int clicks) {
			setMenu(new TutorialForm(getNetwork(), getGUIRoot()));
		}
	}

	private final strictfp class CampaignListener implements MouseClickListener {
                @Override
		public void mouseClicked(int button, int x, int y, int clicks) {
			setMenu(new CampaignForm(getNetwork(), getGUIRoot(), MainMenu.this));
		}
	}

	private final strictfp class SinglePlayerListener implements MouseClickListener {
                @Override
		public void mouseClicked(int button, int x, int y, int clicks) {
			setMenu(new TerrainMenuForm(getNetwork(), getGUIRoot(), MainMenu.this));
		}
	}
}
