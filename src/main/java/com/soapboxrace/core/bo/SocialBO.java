package com.soapboxrace.core.bo;


import javax.ejb.EJB;
import javax.ejb.Stateless;
import com.soapboxrace.core.bo.ParameterBO;
import com.soapboxrace.core.dao.PersonaDAO;
import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.core.dao.ReportDAO;
import com.soapboxrace.core.jpa.ReportEntity;

import com.mrpowergamerbr.temmiewebhook.DiscordEmbed;
import com.mrpowergamerbr.temmiewebhook.DiscordMessage;
import com.mrpowergamerbr.temmiewebhook.TemmieWebhook;
import com.mrpowergamerbr.temmiewebhook.embed.ThumbnailEmbed;

@Stateless
public class SocialBO {

	@EJB
	private ReportDAO reportDao;

	@EJB
	private ParameterBO parameterBO;

	@EJB
	private PersonaDAO personaDao;
	
	public void sendReport(Long personaId, Long abuserPersonaId, Integer petitionType, String description, Integer customCarID, Integer chatMinutes, Long hacksDetected) {
		ReportEntity reportEntity = new ReportEntity();
		reportEntity.setAbuserPersonaId(abuserPersonaId);
		reportEntity.setChatMinutes(chatMinutes);
		reportEntity.setCustomCarID(customCarID);
		reportEntity.setDescription(description);
		reportEntity.setPersonaId(personaId);
		reportEntity.setPetitionType(petitionType);
		reportEntity.setHacksDetected(hacksDetected);
		reportDao.insert(reportEntity);

		if(parameterBO.getStrParam("DISCORD_WEBHOOK_REPORT_URL") != null) {
			PersonaEntity personaEntity = personaDao.findById(abuserPersonaId);
			PersonaEntity personaEntity1 = personaDao.findById(personaId);

			TemmieWebhook temmie = new TemmieWebhook(parameterBO.getStrParam("DISCORD_WEBHOOK_REPORT_URL"));
			DiscordMessage dm = DiscordMessage.builder()
					.username(parameterBO.getStrParam("DISCORD_WEBHOOK_REPORT_NAME", "Botte"))
					.content("[ " + personaEntity.getName() + " ] has been reported by [ " + personaEntity1.getName() + "]. Reason: " + description)
					.build();
			temmie.sendMessage(dm);
		}
	}

}
