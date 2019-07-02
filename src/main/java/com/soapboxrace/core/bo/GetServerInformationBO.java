package com.soapboxrace.core.bo;

import com.soapboxrace.core.dao.ServerInfoDAO;
import com.soapboxrace.core.jpa.ServerInfoEntity;

import javax.ejb.EJB;
import javax.ejb.Stateless;

@Stateless
public class GetServerInformationBO {

	@EJB
	private ServerInfoDAO serverInfoDAO;

	@EJB
	private OnlineUsersBO onlineUsersBO;

	@EJB
	private ParameterBO parameterBO;

	public ServerInfoEntity getServerInformation() {
		ServerInfoEntity serverInfoEntity = serverInfoDAO.findInfo();
		serverInfoEntity.setOnlineNumber(onlineUsersBO.getNumberOfUsersOnlineNow());
		String ticketToken = parameterBO.getStrParam("TICKET_TOKEN");
		if (ticketToken != null && !ticketToken.equals("null")) {
			serverInfoEntity.setRequireTicket(true);
		}
		serverInfoEntity.setServerVersion("0.1.0");
		serverInfoEntity.setGameShutdownTimeInSeconds(parameterBO.getIntParam("MTNTR_GAMESHUTDOWNTIME", 3*60*60));
		return serverInfoEntity;
	}

}
