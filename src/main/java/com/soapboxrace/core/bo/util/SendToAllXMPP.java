package com.soapboxrace.core.bo.util;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.xmpp.OpenFireRestApiCli;
import com.soapboxrace.core.xmpp.OpenFireSoapBoxCli;
import com.soapboxrace.core.xmpp.RoomEntity;
import com.soapboxrace.core.xmpp.XmppChat;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SendToAllXMPP {
	@EJB
    private OpenFireRestApiCli restApiCli;

    @EJB
    private OpenFireSoapBoxCli openFireSoapBoxCli;

	public void sendMessageToChannel(String message, String channelname) {
		List<RoomEntity> channels = restApiCli.getAllRooms()
			.stream()
			.collect(Collectors.toList());

        String msg = XmppChat.createSystemMessage(message);

        for (RoomEntity channel : channels) {
        	System.out.println(channel.getName());
        	if(channel.getName().equals(channelname)) {
	            List<Long> members = restApiCli.getOnlinePersonas();
	                
	            for (Long member : members) {
	                openFireSoapBoxCli.send(msg, member);
	            }
	        }
        }
	}

	public void sendMessage(String message) {
		List<RoomEntity> channels = restApiCli.getAllRooms();

        String msg = XmppChat.createSystemMessage(message);

        List<Long> members = restApiCli.getOnlinePersonas();
                
        for (Long member : members) {
            openFireSoapBoxCli.send(msg, member);
        }
   	}
}