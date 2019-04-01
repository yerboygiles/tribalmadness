package com.oddlabs.tt.net;

public final strictfp class InGameChatHistory extends ChatHistory {
        @Override
	public void chat(ChatMessage message) {
		if (message.type == ChatMessage.CHAT_PRIVATE || message.type == ChatMessage.CHAT_NORMAL ||  message.type == ChatMessage.CHAT_TEAM) {
			addMessage(message.formatLong());
		}
	}
}
