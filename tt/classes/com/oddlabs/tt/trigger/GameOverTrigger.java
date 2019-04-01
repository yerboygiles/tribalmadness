package com.oddlabs.tt.trigger;

import com.oddlabs.matchmaking.MatchmakingServerInterface;
import com.oddlabs.tt.animation.Animated;
import com.oddlabs.tt.gui.GUIRoot;
import com.oddlabs.tt.net.PeerHub;
import com.oddlabs.tt.player.Player;
import com.oddlabs.tt.player.PlayerInfo;
import com.oddlabs.tt.util.StateChecksum;
import com.oddlabs.tt.util.Utils;
import com.oddlabs.tt.viewer.WorldViewer;
import java.util.Arrays;
import java.util.ResourceBundle;

public final strictfp class GameOverTrigger implements Animated {

    private final int[] teams;
    private final boolean[] dead_tribes;
    private final ResourceBundle bundle = ResourceBundle.getBundle(GameOverTrigger.class.getName());
    private final WorldViewer viewer;

    public GameOverTrigger(WorldViewer viewer) {
        this.viewer = viewer;
        viewer.getWorld().getAnimationManagerRealTime().registerAnimation(this);
        teams = new int[MatchmakingServerInterface.MAX_PLAYERS];
        dead_tribes = new boolean[viewer.getWorld().getPlayers().length];
        Arrays.fill(dead_tribes, false);
    }

    @Override
    public void animate(float t) {
        Player[] players = viewer.getWorld().getPlayers();
        Player local_player = viewer.getLocalPlayer();
        boolean enemy_alive = false;

        for (int i = 0; i < players.length; i++) {
            Player current = players[i];
            if (!dead_tribes[i]) {
                if (!viewer.getPeerHub().isAlive(current)) {
                    if (current == local_player) {
                        doGameOver(countTeams(players));
                        return;
                    } else {
                        dead_tribes[i] = true;
                        String defeat_message = Utils.getBundleString(bundle, "defeat_message", new Object[]{current.getPlayerInfo().getName()});
                        viewer.getPeerHub().receiveChat(PeerHub.SYSTEM_NAME, defeat_message, false);
                    }
                } else
                    if (local_player.isEnemy(current)) {
                        enemy_alive = true;
                    }
            }
        }
        if (!enemy_alive) {
            doGameWon();
            return;
        }
        if (countTeams(players) < 2) {
            stop();
        }
    }

    private int countTeams(Player[] players) {
        for (int i = 0; i < players.length; i++) {
            teams[i] = 0;
        }

        for (Player current : players) {
            if (viewer.getPeerHub().isAlive(current) && current.getPlayerInfo().getTeam() != PlayerInfo.TEAM_NEUTRAL)
                teams[current.getPlayerInfo().getTeam()]++;
        }

        int team_count = 0;
        for (int i = 0; i < teams.length; i++) {
            if (teams[i] > 0)
                team_count++;
        }
        return team_count;
    }

    @Override
    public void updateChecksum(StateChecksum checksum) {
    }

    public void disable() {
        viewer.getWorld().getAnimationManagerRealTime().removeAnimation(this);
    }

    private void createDelayTrigger(String text) {
        GUIRoot gui_root = viewer.getGUIRoot();
        new GameOverDelayTrigger(viewer, gui_root.getDelegate().getCamera(), text);
    }

    private void doGameOver(int team_count) {
        viewer.getPeerHub().leaveGame();
        if (team_count < 2) {
            createDelayTrigger(Utils.getBundleString(bundle, "you_defeated_game_over"));
        } else {
            createDelayTrigger(Utils.getBundleString(bundle, "you_defeated"));
        }
        disable();
    }

    private void doGameWon() {
        viewer.getPeerHub().gameWon();
        createDelayTrigger(Utils.getBundleString(bundle, "you_victorious"));
        disable();
    }

    private void stop() {
        createDelayTrigger(Utils.getBundleString(bundle, "game_over"));
        disable();
    }
}
