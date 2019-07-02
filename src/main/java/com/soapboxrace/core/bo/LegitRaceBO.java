package com.soapboxrace.core.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.jpa.EventSessionEntity;
import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.jaxb.http.ArbitrationPacket;
import com.soapboxrace.jaxb.http.DragArbitrationPacket;
import com.soapboxrace.jaxb.http.PursuitArbitrationPacket;
import com.soapboxrace.jaxb.http.RouteArbitrationPacket;
import com.soapboxrace.jaxb.http.TeamEscapeArbitrationPacket;

import com.soapboxrace.core.dao.PersonaDAO;
import com.soapboxrace.core.bo.AdminBO;

@Stateless
public class LegitRaceBO {

	@EJB
	private ParameterBO parameterBO;

	@EJB
	private SocialBO socialBo;

    @EJB
    private PersonaDAO personaDAO;

    @EJB
    private AdminBO adminBo;

	public boolean isLegit(Long activePersonaId, ArbitrationPacket arbitrationPacket, EventSessionEntity sessionEntity) {
		int minimumTime = 0;
		boolean quitted_event = false;

		if (arbitrationPacket instanceof PursuitArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("PURSUIT_MINIMUM_TIME");
		} else if (arbitrationPacket instanceof RouteArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("ROUTE_MINIMUM_TIME");
		} else if (arbitrationPacket instanceof TeamEscapeArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("TE_MINIMUM_TIME");
			quitted_event = arbitrationPacket.getFinishReason() == 8202;
		} else if (arbitrationPacket instanceof DragArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("DRAG_MINIMUM_TIME");
		}

		final long timeDiff = sessionEntity.getEnded() - sessionEntity.getStarted();
		boolean legit = timeDiff > minimumTime + 1;

		if (!legit && !quitted_event) {
			//SHADOWBAN THAT USER!
			PersonaEntity persona = personaDAO.findById(activePersonaId);

			if(persona.getShadowBanned() == false) {
				persona.setShadowBanned(true);
				personaDAO.update(persona);

			    Date date = new Date(timeDiff);
			    SimpleDateFormat formatter = new SimpleDateFormat("mm:ss.SSS");
			    formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
			    String formatted = formatter.format(date);

				socialBo.sendReport(0L, activePersonaId, 3, String.format("Abnormal event time: %d. User is ShadowBanned", formatted), (int) arbitrationPacket.getCarId(), 0, arbitrationPacket.getHacksDetected());
				adminBo.sendKick(persona.getUser().getId(), activePersonaId);
			}
		}

		if (arbitrationPacket.getHacksDetected() != 0 && arbitrationPacket.getHacksDetected() != 32) {
			socialBo.sendReport(0L, activePersonaId, 3, "hacksDetected = " + arbitrationPacket.getHacksDetected(), (int) arbitrationPacket.getCarId(), 0,
					arbitrationPacket.getHacksDetected());
		}

		return legit;
	}
}
