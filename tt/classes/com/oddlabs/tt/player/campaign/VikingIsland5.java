package com.oddlabs.tt.player.campaign;

import com.oddlabs.net.NetworkSelector;
import com.oddlabs.tt.form.CampaignDialogForm;
import com.oddlabs.tt.form.InGameCampaignDialogForm;
import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.model.RacesResources;
import com.oddlabs.tt.net.GameNetwork;
import com.oddlabs.tt.net.PlayerSlot;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.player.UnitInfo;
import com.oddlabs.tt.procedural.Landscape;
import com.oddlabs.tt.trigger.campaign.GameStartedTrigger;
import com.oddlabs.tt.trigger.campaign.PlayerEleminatedTrigger;
import com.oddlabs.tt.trigger.campaign.VictoryTrigger;
import com.oddlabs.tt.util.Utils;
import java.util.ResourceBundle;

public final strictfp class VikingIsland5 extends Island {
	private final ResourceBundle bundle = ResourceBundle.getBundle(VikingIsland5.class.getName());

	public VikingIsland5(Campaign campaign) {
		super(campaign);
	}

        @Override
	public void init(NetworkSelector network, GUIRoot gui_root) {
		String[] ai_names = new String[]{Utils.getBundleString(bundle, "name0"),
			Utils.getBundleString(bundle, "name1"),
			Utils.getBundleString(bundle, "name2"),
			Utils.getBundleString(bundle, "name3"),
			Utils.getBundleString(bundle, "name4"),
			Utils.getBundleString(bundle, "name5")};
		// gametype, owner, game, meters_per_world, hills, vegetation_amount, supplies_amount, seed, speed, map_code
		GameNetwork game_network = startNewGame(network, gui_root, 512, Landscape.NATIVE, .85f, 1f, .9f, 89864, 5, VikingCampaign.MAX_UNITS, ai_names);
		game_network.getClient().getServerInterface().setPlayerSlot(0,
				PlayerSlot.HUMAN,
				RacesResources.RACE_VIKINGS,
				0,
				true,
				PlayerSlot.AI_NONE);
		game_network.getClient().setUnitInfo(0,
				new UnitInfo(false, false, 0, true,
					getCampaign().getState().getNumPeons(),
					getCampaign().getState().getNumRockWarriors(),
					getCampaign().getState().getNumIronWarriors(),
					getCampaign().getState().getNumRubberWarriors()));
		game_network.getClient().getServerInterface().setPlayerSlot(1,
				PlayerSlot.AI,
				RacesResources.RACE_VIKINGS,
				0,
				true,
				PlayerSlot.AI_HARD);
		game_network.getClient().setUnitInfo(1, new UnitInfo(false, false, 0, false, 25, 5, 0, 0));

		int ai_peons;
		switch (getCampaign().getState().getDifficulty()) {
			case CampaignState.DIFFICULTY_EASY:
				ai_peons = 5;
				break;
			case CampaignState.DIFFICULTY_NORMAL:
				ai_peons = 10;
				break;
			case CampaignState.DIFFICULTY_HARD:
				ai_peons = 25;
				break;
			default:
				throw new RuntimeException();
		}
		game_network.getClient().getServerInterface().setPlayerSlot(2,
				PlayerSlot.AI,
				RacesResources.RACE_NATIVES,
				1,
				true,
				PlayerSlot.AI_HARD);
		game_network.getClient().setUnitInfo(2, new UnitInfo(true, false, 1, false, ai_peons, 0, 0, 1));
		game_network.getClient().getServerInterface().setPlayerSlot(3,
				PlayerSlot.AI,
				RacesResources.RACE_NATIVES,
				1,
				true,
				PlayerSlot.AI_HARD);
		game_network.getClient().setUnitInfo(3, new UnitInfo(true, false, 1, false, ai_peons, 0, 0, 1));
		game_network.getClient().getServerInterface().startServer();
	}

        @Override
	protected void start() {
		Runnable runnable;
		final Player enemy0 = getViewer().getWorld().getPlayers()[2];
		final Player enemy1 = getViewer().getWorld().getPlayers()[3];

		// Introduction
		final Runnable answer = () -> {
                    CampaignDialogForm dialog = new InGameCampaignDialogForm(getViewer(), Utils.getBundleString(bundle, "header0"),
                            Utils.getBundleString(bundle, "dialog0"),
                            getCampaign().getIcons().getFaces()[0],
                            CampaignDialogForm.ALIGN_IMAGE_LEFT);
                    addModalForm(dialog);
                };
		runnable = () -> {
                    CampaignDialogForm dialog = new InGameCampaignDialogForm(getViewer(), Utils.getBundleString(bundle, "header1"),
                            Utils.getBundleString(bundle, "dialog1"),
                            getCampaign().getIcons().getFaces()[5],
                            CampaignDialogForm.ALIGN_IMAGE_RIGHT,
                            answer);
                    addModalForm(dialog);
                };
		new GameStartedTrigger(getViewer().getWorld(), runnable);

		// Winner prize
		final Runnable prize = () -> {
                    getCampaign().getState().setIslandState(5, CampaignState.ISLAND_COMPLETED);
                    getCampaign().getState().setIslandState(4, CampaignState.ISLAND_AVAILABLE);
                    getCampaign().getState().setIslandState(6, CampaignState.ISLAND_AVAILABLE);
                    getCampaign().getState().setNumRockWarriors(getCampaign().getState().getNumRockWarriors() + 5);
                    getCampaign().victory(getViewer());
                };

		// Winning condition
		runnable = () -> {
                    CampaignDialogForm dialog = new InGameCampaignDialogForm(getViewer(), Utils.getBundleString(bundle, "header2"),
                            Utils.getBundleString(bundle, "dialog2"),
                            getCampaign().getIcons().getFaces()[5],
                            CampaignDialogForm.ALIGN_IMAGE_RIGHT,
                            prize);
                    addModalForm(dialog);
                };
		new VictoryTrigger(getViewer(), runnable);

		// Put warrior in tower
		enemy0.getAI().manTowers(1); // TODO: replace with insertGuardTower()
		enemy1.getAI().manTowers(1); // TODO: replace with insertGuardTower()

		// Defeat if friends eleminated
		runnable = () -> {
                    getCampaign().defeated(getViewer(), Utils.getBundleString(bundle, "game_over"));
                };
		new PlayerEleminatedTrigger(runnable, getViewer().getWorld().getPlayers()[1]);
	}

        @Override
	public CharSequence getHeader() {
		return Utils.getBundleString(bundle, "header");
	}

        @Override
	public CharSequence getDescription() {
		return Utils.getBundleString(bundle, "description");
	}

        @Override
	public CharSequence getCurrentObjective() {
		return Utils.getBundleString(bundle, "objective");
	}
}
