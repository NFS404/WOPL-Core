package com.soapboxrace.core.bo.util;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.bo.ParameterBO;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;

@Stateless
public class DiscordWebhook {
	@EJB
	private ParameterBO parameterBO;

	public void sendMessage(String message, String webHookUrl, String botName) {
		TemmieWebhook temmie = new TemmieWebhook(webHookUrl);
		DiscordMessage dm = DiscordMessage.builder().username(botName).content(message).build();
		temmie.sendMessage(dm);
	}

	public void sendMessage(String message, String webHookUrl) {
		sendMessage(message, webHookUrl, parameterBO.getStrParam("DISCORD_WEBHOOK_DEFAULTNAME"));
	}

	public void sendMessage(String message) {
		sendMessage(message, parameterBO.getStrParam("DISCORD_WEBHOOK_DEFAULTURL"), parameterBO.getStrParam("DISCORD_WEBHOOK_DEFAULTNAME"));
	}
}