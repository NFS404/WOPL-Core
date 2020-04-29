package com.soapboxrace.core.bo;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.jpa.EventEntity;
import com.soapboxrace.core.jpa.EventSessionEntity;
import com.soapboxrace.core.jpa.PersonaEntity;
import com.soapboxrace.jaxb.http.ArbitrationPacket;
import com.soapboxrace.jaxb.http.DragArbitrationPacket;
import com.soapboxrace.jaxb.http.PursuitArbitrationPacket;
import com.soapboxrace.jaxb.http.RouteArbitrationPacket;
import com.soapboxrace.jaxb.http.TeamEscapeArbitrationPacket;

import com.soapboxrace.core.dao.PersonaDAO;
import com.soapboxrace.core.bo.AdminBO;

import com.soapboxrace.core.api.util.EventFinishReason;

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
		boolean report = true;

		if (arbitrationPacket instanceof PursuitArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("PURSUIT_MINIMUM_TIME");
		} else if (arbitrationPacket instanceof RouteArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("ROUTE_MINIMUM_TIME");
		} else if (arbitrationPacket instanceof TeamEscapeArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("TE_MINIMUM_TIME");
			report = false;
		} else if (arbitrationPacket instanceof DragArbitrationPacket) {
			minimumTime = parameterBO.getIntParam("DRAG_MINIMUM_TIME");
		}

		final long timeDiff = sessionEntity.getEnded() - sessionEntity.getStarted();
		boolean legit = timeDiff > minimumTime + 1;

		if (!report) legit = true;

		//eventname
		EventEntity eventInformation = sessionEntity.getEvent();
		String eventNameFull = eventInformation.getName();
		String eventName = eventNameFull.split("\\(")[0];

		if (!legit) {
            socialBo.sendReport(0L, activePersonaId, 3, String.format("Abnormal event time: %d (below minimum of %d on %w)", timeDiff, minimumTime, eventName), (int)arbitrationPacket.getCarId(), 0, arbitrationPacket.getHacksDetected());
		}

		if (arbitrationPacket.getHacksDetected() != 0 && 
			arbitrationPacket.getHacksDetected() != 8 && 
			arbitrationPacket.getHacksDetected() != 32 && 
			arbitrationPacket.getHacksDetected() != 40) {
			socialBo.sendReport(0L, activePersonaId, 3, "hacksDetected = " + arbitrationPacket.getHacksDetected(), (int) arbitrationPacket.getCarId(), 0,
					arbitrationPacket.getHacksDetected());
		}

		if (arbitrationPacket instanceof TeamEscapeArbitrationPacket) {
			TeamEscapeArbitrationPacket teamEscapeArbitrationPacket = (TeamEscapeArbitrationPacket)arbitrationPacket;

			if(teamEscapeArbitrationPacket.getFinishReason() != 8202) {
				if(teamEscapeArbitrationPacket.getCopsDisabled() > teamEscapeArbitrationPacket.getCopsDeployed()) {
					legit = false;
					socialBo.sendReport(0L, activePersonaId, 3, "[TE] copsDisabled is higher than copsDeployed!", (int) teamEscapeArbitrationPacket.getCarId(), 0,
						teamEscapeArbitrationPacket.getHacksDetected());
				}
			}
		}

		if (arbitrationPacket instanceof PursuitArbitrationPacket) {
			PursuitArbitrationPacket pursuitArbitrationPacket  = (PursuitArbitrationPacket)arbitrationPacket;

			if(pursuitArbitrationPacket.getFinishReason() != 8202) {
				if(pursuitArbitrationPacket.getCopsDisabled() > pursuitArbitrationPacket.getCopsDeployed()) {
					legit = false;
					socialBo.sendReport(0L, activePersonaId, 3, "[SP] copsDisabled is higher than copsDeployed!", (int) pursuitArbitrationPacket.getCarId(), 0,
						pursuitArbitrationPacket.getHacksDetected());
				}

				if(pursuitArbitrationPacket.getTopSpeed() == 0 && pursuitArbitrationPacket.getInfractions() != 0) {
					legit = false;
					socialBo.sendReport(0L, activePersonaId, 3, String.format("[SP] Player uses PursuitBot on %s", eventName), (int) pursuitArbitrationPacket.getCarId(), 0,
						pursuitArbitrationPacket.getHacksDetected());	
				}
			}
		}

		return legit;
	}
}