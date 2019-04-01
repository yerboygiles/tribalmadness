package com.oddlabs.tt.net;

import com.oddlabs.tt.gui.InfoPrinter;
import com.oddlabs.tt.util.Utils;
import java.util.*;

public final strictfp class ChatCommand {
	private final static Map<String, ChatMethod> commands = new HashMap<>();

	static {
			commands.put("message", ChatCommand::sendMessage);
			commands.put("msg", ChatCommand::sendMessage);
			commands.put("tell", ChatCommand::sendMessage);
			commands.put("whisper", ChatCommand::sendMessage);
			commands.put("info", ChatCommand::getInfo);
			commands.put("finger", ChatCommand::getInfo);
			commands.put("ignore", ChatCommand::ignore);
			commands.put("unignore", ChatCommand::unignore);
			commands.put("ignorelist", ChatCommand::ignoreList);
	}

    private final static Set ignored_nicks = new HashSet();

	private final static ResourceBundle bundle = ResourceBundle.getBundle(ChatCommand.class.getName());

	public static boolean filterCommand(InfoPrinter info_printer, String text) {
		return filterCommand(info_printer, null, text);
	}

	public static boolean filterCommand(InfoPrinter info_printer, Map custom_commands, String text) {
		if (!text.startsWith("/"))
			return false;
		int fist_space = firstSpace(text);
		String cmd = text.substring(1, fist_space);
		String args = text.substring(fist_space, text.length()).trim();
		ChatMethod method = custom_commands != null ? (ChatMethod)custom_commands.get(cmd) : null;
		if (method == null)
			method = (ChatMethod)commands.get(cmd);
		if (method != null) {
			method.execute(info_printer, args);
		} else {
			String unknown_cmd_message = Utils.getBundleString(bundle, "unknown_command", new Object[]{cmd});
			info_printer.print(unknown_cmd_message);
		}
		return true;
	}

	private static int firstSpace(String text) {
		int fist_space = text.indexOf(' ');
		if (fist_space == -1)
			return text.length();
		else
			return fist_space;
	}

	private static void sendMessage(InfoPrinter info_printer, String text) {
		int first_space = firstSpace(text);
		String nick = text.substring(0, first_space);
		String message = text.substring(first_space, text.length()).trim();
		if (!Network.getMatchmakingClient().isConnected())
			info_printer.print(Utils.getBundleString(bundle, "not_connected"));
		else
			Network.getMatchmakingClient().sendPrivateMessage(info_printer.getGUIRoot(), nick, message);
	}

	private static void getInfo(InfoPrinter info_printer, String text) {
		int first_space = firstSpace(text);
		String nick = text.substring(0, first_space);
		if (!Network.getMatchmakingClient().isConnected())
			info_printer.print(Utils.getBundleString(bundle, "not_connected"));
		else
			Network.getMatchmakingClient().requestInfo(info_printer.getGUIRoot(), nick);
	}

	public static void ignore(InfoPrinter info_printer, String text) {
		int first_space = firstSpace(text);
		String nick = text.substring(0, first_space);
		boolean result = ignored_nicks.add(nick.toLowerCase());
		if (result) {
			String msg = Utils.getBundleString(bundle, "ignoring", new Object[]{nick});
			info_printer.print(msg);
		}
	}

	public static void unignore(InfoPrinter info_printer, String text) {
		int first_space = firstSpace(text);
		String nick = text.substring(0, first_space);
		boolean result = ignored_nicks.remove(nick.toLowerCase());
		if (result) {
			String msg = Utils.getBundleString(bundle, "unignoring", new Object[]{nick});
			info_printer.print(msg);
		}
	}

	public static boolean isIgnoring(String nick) {
		return ignored_nicks.contains(nick.toLowerCase());
	}

	private static void ignoreList(InfoPrinter info_printer, String text) {
		String[] nicks = new String[ignored_nicks.size()];
		ignored_nicks.toArray(nicks);
		String result;
		if (nicks.length == 0) {
			result = Utils.getBundleString(bundle, "ignore_list_empty");
		} else {
			result = Utils.getBundleString(bundle, "ignore_list");
                    for (String nick : nicks) {
                        result += " " + nick;
                    }
		}
		info_printer.print(result);
	}

    private ChatCommand() {
    }
}
