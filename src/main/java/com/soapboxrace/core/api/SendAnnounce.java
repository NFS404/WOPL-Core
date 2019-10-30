package com.soapboxrace.core.api;

import javax.ejb.EJB;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.soapboxrace.core.bo.ParameterBO;

import com.soapboxrace.core.bo.util.SendToAllXMPP;

@Path("/api")
public class SendAnnounce {

	@EJB
	private SendToAllXMPP internalXmpp;

	@EJB
	private ParameterBO parameterBO;

	@GET
	@Path("/SendAnnouncement")
	@Produces(MediaType.TEXT_PLAIN)
	public String SendAnnouncement(@QueryParam("message") String message, @QueryParam("key") String key) {
		if(!key.equals(parameterBO.getStrParam("ANNOUNCE_KEY"))) return "NO KEY!";

		if(message != null) {
			internalXmpp.sendMessage(message);

			return "sent";
		} else {
			return "EMPTY message GET";
		}
	}
}