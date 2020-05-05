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
		boolean testPassed = true;
		int minimumTime = 0;

		if (arbitrationPacket instanceof PursuitArbitrationPacket)
			minimumTime = parameterBO.getIntParam("PURSUIT_MINIMUM_TIME");
		else if (arbitrationPacket instanceof RouteArbitrationPacket)
			minimumTime = parameterBO.getIntParam("ROUTE_MINIMUM_TIME");
		else if (arbitrationPacket instanceof TeamEscapeArbitrationPacket)
			minimumTime = parameterBO.getIntParam("TE_MINIMUM_TIME");
		else if (arbitrationPacket instanceof DragArbitrationPacket)
			minimumTime = parameterBO.getIntParam("DRAG_MINIMUM_TIME");
        
        final long timeDiff = sessionEntity.getEnded() - sessionEntity.getStarted();
        boolean isLegitTime = timeDiff >= minimumTime;

		//Persona
		PersonaEntity personaEntity = personaDAO.findById(activePersonaId);

        if (!isLegitTime) {
            socialBo.sendReport(0L, activePersonaId, 3, String.format("Abnormal event time: %d (below minimum of %d on event %d; session %d)", timeDiff, minimumTime, sessionEntity.getEvent().getId(), sessionEntity.getId()), (int) arbitrationPacket.getCarId(), 0, arbitrationPacket.getHacksDetected());
			testPassed = false;
        }

        if (arbitrationPacket.getHacksDetected() > 0) {
            socialBo.sendReport(0L, activePersonaId, 3, String.format("hacksDetected=%d (event %d; session %d)", arbitrationPacket.getHacksDetected(), sessionEntity.getEvent().getId(), sessionEntity.getId()), (int) arbitrationPacket.getCarId(), 0, arbitrationPacket.getHacksDetected());
			testPassed = false;
		}
		
		if (arbitrationPacket instanceof TeamEscapeArbitrationPacket) {
            TeamEscapeArbitrationPacket teamEscapeArbitrationPacket = (TeamEscapeArbitrationPacket) arbitrationPacket;

            if (teamEscapeArbitrationPacket.getFinishReason() != 8202) {
				if(teamEscapeArbitrationPacket.getCopsDisabled() > teamEscapeArbitrationPacket.getCopsDeployed()) {
					testPassed = false;
					socialBo.sendReport(0L, activePersonaId, 3, "[TE] copsDisabled is higher than copsDeployed!", (int) teamEscapeArbitrationPacket.getCarId(), 0, teamEscapeArbitrationPacket.getHacksDetected());

					if(parameterBO.getBoolParam("AUTOMATIC_BAN")) {
						adminBo.sendChatCommand(activePersonaId, "ban " + personaEntity.getName() + " copsDisabled is higher than copsDeployed!", "AUTOBOT");
					}
				}
            }
		}
		
        if (arbitrationPacket instanceof PursuitArbitrationPacket) {
            PursuitArbitrationPacket pursuitArbitrationPacket = (PursuitArbitrationPacket) arbitrationPacket;

            if (pursuitArbitrationPacket.getFinishReason() != 8202) {
                if (pursuitArbitrationPacket.getCopsDisabled() > pursuitArbitrationPacket.getCopsDeployed()) {
					socialBo.sendReport(0L, activePersonaId, 3, "[SP] copsDisabled is higher than copsDeployed!", (int) pursuitArbitrationPacket.getCarId(), 0, pursuitArbitrationPacket.getHacksDetected());

					if(parameterBO.getBoolParam("AUTOMATIC_BAN")) {
						adminBo.sendChatCommand(activePersonaId, "ban " + personaEntity.getName() + " copsDisabled is higher than copsDeployed!", "AUTOBOT");
					}

                    testPassed = false;
                }

				if(pursuitArbitrationPacket.getTopSpeed() == 0 && pursuitArbitrationPacket.getInfractions() != 0) {
					socialBo.sendReport(0L, activePersonaId, 3, "[SP] Player uses PursuitBot", (int) pursuitArbitrationPacket.getCarId(), 0, pursuitArbitrationPacket.getHacksDetected());	

					if(parameterBO.getBoolParam("AUTOMATIC_BAN")) {
						adminBo.sendChatCommand(activePersonaId, "ban " + personaEntity.getName() + " PursuitBot", "AUTOBOT");
					}

                    testPassed = false;
				}
			}
		}

		return testPassed;
	}
}