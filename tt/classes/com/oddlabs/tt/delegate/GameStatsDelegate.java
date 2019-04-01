package com.oddlabs.tt.delegate;

import com.oddlabs.tt.animation.TimerAnimation;
import com.oddlabs.tt.animation.Updatable;
import com.oddlabs.tt.camera.Camera;
import com.oddlabs.tt.camera.StaticCamera;
import com.oddlabs.tt.gui.*;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.util.Utils;
import com.oddlabs.tt.viewer.WorldViewer;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import org.lwjgl.input.Keyboard;

public final strictfp class GameStatsDelegate extends CameraDelegate implements Updatable {
	private final static int PLAYER_COLUMN_WIDTH = 100;
	private final static int TEXT_OFFSET = -4;
	public final static ResourceBundle bundle = ResourceBundle.getBundle(GameStatsDelegate.class.getName());

	private final TimerAnimation delay_timer = new TimerAnimation(this, .6f);
	private final Group group_buttons;
	private final WorldViewer viewer;

	public GameStatsDelegate(WorldViewer viewer, Camera old_camera, String label_str) {
		super(viewer.getGUIRoot(), new StaticCamera(old_camera.getState()));
		this.viewer = viewer;
		setDim(LocalInput.getViewWidth(), LocalInput.getViewHeight());
		Label label = new Label(label_str, Skin.getSkin().getHeadlineFont());
		addChild(label);
		label.setPos((getWidth() - label.getWidth())/2, (getHeight() - label.getHeight())*4/5);

		Player[] players = viewer.getWorld().getPlayers();

		ColumnInfo[] score_infos = new ColumnInfo[players.length + 1];
		score_infos[0] = new ColumnInfo(Utils.getBundleString(bundle, "type"), 160);
		for (int i = 0; i < players.length; i++)
			score_infos[i + 1] = new ColumnInfo(players[i].getPlayerInfo().getName(), PLAYER_COLUMN_WIDTH);

		MultiColumnComboBox score_box = new MultiColumnComboBox(viewer.getGUIRoot(), score_infos, 200);
		addChild(score_box);
		score_box.setPos((getWidth() - score_box.getWidth())/2, (getHeight() - score_box.getHeight())/2);

		GUIObject[] units_lost_labels = new GUIObject[players.length + 1];
		units_lost_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "units_lost"), 0, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			units_lost_labels[i + 1] = new IntegerLabel(players[i].getUnitsLost(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(units_lost_labels, null));

		GUIObject[] units_killed_labels = new GUIObject[players.length + 1];
		units_killed_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "units_killed"), 1, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			units_killed_labels[i + 1] = new IntegerLabel(players[i].getUnitsKilled(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(units_killed_labels, null));

		GUIObject[] buildings_lost_labels = new GUIObject[players.length + 1];
		buildings_lost_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "buildings_lost"), 2, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			buildings_lost_labels[i + 1] = new IntegerLabel(players[i].getBuildingsLost(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(buildings_lost_labels, null));

		GUIObject[] buildings_destroyed_labels = new GUIObject[players.length + 1];
		buildings_destroyed_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "buildings_wrecked"), 3, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			buildings_destroyed_labels[i + 1] = new IntegerLabel(players[i].getBuildingsDestroyed(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(buildings_destroyed_labels, null));

		GUIObject[] tree_harvested_labels = new GUIObject[players.length + 1];
		tree_harvested_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "tree_resources"), 3, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			tree_harvested_labels[i + 1] = new IntegerLabel(players[i].getTreeHarvested(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(tree_harvested_labels, null));

		GUIObject[] rock_harvested_labels = new GUIObject[players.length + 1];
		rock_harvested_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "rock_resources"), 4, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			rock_harvested_labels[i + 1] = new IntegerLabel(players[i].getRockHarvested(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(rock_harvested_labels, null));

		GUIObject[] iron_harvested_labels = new GUIObject[players.length + 1];
		iron_harvested_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "iron_resources"), 5, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			iron_harvested_labels[i + 1] = new IntegerLabel(players[i].getIronHarvested(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(iron_harvested_labels, null));

		GUIObject[] rubber_harvested_labels = new GUIObject[players.length + 1];
		rubber_harvested_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "chicken_resources"), 6, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			rubber_harvested_labels[i + 1] = new IntegerLabel(players[i].getRubberHarvested(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(rubber_harvested_labels, null));

		GUIObject[] walked_labels = new GUIObject[players.length + 1];
		walked_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "meters_walked"), 7, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			walked_labels[i + 1] = new IntegerLabel(players[i].getUnitsMoved()*2, Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(walked_labels, null));

		GUIObject[] weapons_labels = new GUIObject[players.length + 1];
		weapons_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "weapons_thrown"), 8, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			weapons_labels[i + 1] = new IntegerLabel(players[i].getWeaponsThrown(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(weapons_labels, null));

		GUIObject[] magics_labels = new GUIObject[players.length + 1];
		magics_labels[0] = new SortedLabel(Utils.getBundleString(bundle, "magics_used"), 9, Skin.getSkin().getMultiColumnComboBoxData().getFont());
		for (int i = 0; i < players.length; i++)
			magics_labels[i + 1] = new IntegerLabel(players[i].getMagics(), Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET);
		score_box.addRow(new Row(magics_labels, null));

		List<GUIObject> total_labels = new ArrayList<>(players.length + 1);
		total_labels.add(new SortedLabel(Utils.getBundleString(bundle, "total"), 10, Skin.getSkin().getMultiColumnComboBoxData().getFont()));
		for (Player player : players) {
			int unit_killed = player.getUnitsKilled();
			int buildings_wrecked = player.getBuildingsDestroyed();
			int tree = player.getTreeHarvested();
			int rock = player.getRockHarvested();
			int iron = player.getIronHarvested();
			int chicken = player.getRubberHarvested();

			int total_score = unit_killed*10 + buildings_wrecked*100 + tree + rock + iron*2 + chicken*4;

			total_labels.add(new IntegerLabel(total_score, Skin.getSkin().getMultiColumnComboBoxData().getFont(), PLAYER_COLUMN_WIDTH + TEXT_OFFSET));
		}
		score_box.addRow(new Row(total_labels.toArray(new GUIObject[total_labels.size()]), null));

		group_buttons = new Group();

		viewer.addGameOverGUI(this, score_box.getY(), group_buttons);
		group_buttons.compileCanvas();
		addChild(group_buttons);
		group_buttons.setPos((getWidth() - group_buttons.getWidth())/2, (getHeight() - group_buttons.getHeight())*1/5);

		setFocusCycle(true);
		delay_timer.start();
	}

        @Override
	public void update(Object anim) {
		addChild(group_buttons);
		delay_timer.stop();
	}

        @Override
	protected void renderGeometry() {
		renderBackgroundAlpha();
	}

        @Override
	protected void keyRepeat(KeyboardEvent event) {
		switch (event.getKeyCode()) {
			case Keyboard.KEY_TAB:
				switchFocus(event.isShiftDown() ? -1 : 1);
				break;
			default:
				super.keyRepeat(event);
				break;
		}
	}

	public void startMenu() {
		viewer.close();
		setDisabled(true);
	}

	public WorldViewer getViewer() {
		return viewer;
	}
}
