package com.soapboxrace.core.bo;

import com.soapboxrace.core.api.util.MiscUtils;
import com.soapboxrace.core.dao.BanDAO;
import com.soapboxrace.core.dao.HardwareInfoDAO;
import com.soapboxrace.core.dao.PersonaDAO;
import com.soapboxrace.core.dao.UserDAO;
import com.soapboxrace.core.jpa.BanEntity;
import com.soapboxrace.core.jpa.HardwareInfoEntity;
import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.core.jpa.UserEntity;
import com.soapboxrace.core.xmpp.OpenFireSoapBoxCli;
import com.soapboxrace.core.xmpp.XmppChat;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.time.LocalDateTime;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;

@Stateless
public class AdminBO {
	@EJB
	private TokenSessionBO tokenSessionBo;

	@EJB
	private PersonaDAO personaDao;

	@EJB
	private UserDAO userDao;

	@EJB
	private BanDAO banDAO;

	@EJB
	private HardwareInfoDAO hardwareInfoDAO;

	@EJB
	private OpenFireSoapBoxCli openFireSoapBoxCli;

	@EJB
	private ParameterBO parameterBO;

	public void sendCommand(Long personaId, Long abuserPersonaId, String command)
	{
		CommandInfo commandInfo = CommandInfo.parse(command);
		PersonaEntity personaEntity = personaDao.findById(abuserPersonaId);
		PersonaEntity personaEntity1 = personaDao.findById(personaId);

		if (personaEntity == null)
			return;

		switch (commandInfo.action)
		{
			case BAN:
				if (banDAO.findByUser(personaEntity.getUser()) != null) {
					openFireSoapBoxCli.send(XmppChat.createSystemMessage("User is already banned!"), personaId);
					break;
				}

				sendBan(personaEntity, personaDao.findById(personaId), commandInfo.timeEnd, commandInfo.reason);
				openFireSoapBoxCli.send(XmppChat.createSystemMessage("Banned user!"), personaId);

				if(parameterBO.getStrParam("DISCORD_WEBHOOK_BANREPORT_URL") != null) {
					TemmieWebhook temmie = new TemmieWebhook(parameterBO.getStrParam("DISCORD_WEBHOOK_BANREPORT_URL"));
					DiscordMessage dm = DiscordMessage.builder()
							.username(parameterBO.getStrParam("DISCORD_WEBHOOK_BANREPORT_NAME", "Botte"))
							.content("[ " + personaEntity.getName() + " ] has been banned by [ " + personaEntity1.getName() + "]. ")
							.build();
					temmie.sendMessage(dm);
				}
						
				break;
			case KICK:
				sendKick(personaEntity.getUser().getId(), personaEntity.getPersonaId());
				openFireSoapBoxCli.send(XmppChat.createSystemMessage("Kicked user!"), personaId);
				break;
			case UNBAN:
				BanEntity existingBan;
				if ((existingBan = banDAO.findByUser(personaEntity.getUser())) == null) {
					openFireSoapBoxCli.send(XmppChat.createSystemMessage("User is not banned!"), personaId);
					break;
				}

				banDAO.delete(existingBan);
				openFireSoapBoxCli.send(XmppChat.createSystemMessage("Unbanned user!"), personaId);

				break;
			default:
				break;
		}
	}

	private void sendBan(PersonaEntity personaEntity, PersonaEntity bannedBy, LocalDateTime endsOn, String reason)
	{
		UserEntity userEntity = personaEntity.getUser();
		BanEntity banEntity = new BanEntity();
		banEntity.setUserEntity(userEntity);
		banEntity.setEndsAt(endsOn);
		banEntity.setStarted(LocalDateTime.now());
		banEntity.setReason(reason);
		banEntity.setBannedBy(bannedBy);
		banEntity.setWillEnd(endsOn != null);
		banDAO.insert(banEntity);
		userDao.update(userEntity);
		sendKick(userEntity.getId(), personaEntity.getPersonaId());

		HardwareInfoEntity hardwareInfoEntity = hardwareInfoDAO.findByUserId(userEntity.getId());

		if (hardwareInfoEntity != null) {
			hardwareInfoEntity.setBanned(true);
			hardwareInfoDAO.update(hardwareInfoEntity);
		}
	}

	private void sendKick(Long userId, Long personaId)
	{
		openFireSoapBoxCli.send("<NewsArticleTrans><ExpiryTime><", personaId);
		tokenSessionBo.deleteByUserId(userId);
	}

	private static class CommandInfo
	{
		public CommandInfo.CmdAction action;
		public String reason;
		public LocalDateTime timeEnd;

		public enum CmdAction
		{
			KICK,
			BAN,
			ALERT,
			UNBAN,
			UNKNOWN
		}

		public static CommandInfo parse(String cmd)
		{
			cmd = cmd.replaceFirst("/", "");

			String[] split = cmd.split(" ");
			CommandInfo.CmdAction action;
			CommandInfo info = new CommandInfo();

			switch (split[0].toLowerCase().trim())
			{
				case "ban":
					action = CmdAction.BAN;
					break;
				case "kick":
					action = CmdAction.KICK;
					break;
				case "unban":
					action = CmdAction.UNBAN;
					break;
				default:
					action = CmdAction.UNKNOWN;
					break;
			}

			info.action = action;

			switch (action)
			{
				case BAN:
				{
					LocalDateTime endTime;
					String reason = null;

					if (split.length >= 2)
					{
						long givenTime = MiscUtils.lengthToMiliseconds(split[1]);
						if (givenTime != 0)
						{
							endTime = LocalDateTime.now().plusSeconds(givenTime / 1000);
							info.timeEnd = endTime;

							if (split.length > 2)
							{
								reason = MiscUtils.argsToString(split, 2, split.length);
							}
						} else
						{
							reason = MiscUtils.argsToString(split, 1, split.length);
						}
					}

					info.reason = reason;
					break;
				}
			}

			return info;
		}
	}
}