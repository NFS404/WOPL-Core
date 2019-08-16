package com.soapboxrace.core.bo.util;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import com.soapboxrace.core.xmpp.OpenFireRestApiCli;
import com.soapboxrace.core.xmpp.OpenFireSoapBoxCli;
import com.soapboxrace.core.xmpp.XmppChat;
import org.igniterealtime.restclient.entity.MUCRoomEntity;
import java.util.List;
import java.util.stream.Collectors;

@Stateless
public class SendToAllXMPP {
	@EJB
    private OpenFireRestApiCli restApiCli;

    @EJB
    private OpenFireSoapBoxCli openFireSoapBoxCli;

	public void sendMessage(String message, String channelname) {
		List<MUCRoomEntity> channels = restApiCli.getAllRooms()
			.stream()
			.filter(r -> r.getRoomName().startsWith(channelname))
			.collect(Collectors.toList());

        String msg = XmppChat.createSystemMessage(message);

        for (MUCRoomEntity channel : channels) {
            List<Long> members = restApiCli.getAllOccupantsInRoom(channel.getRoomName());
                
            for (Long member : members) {
                openFireSoapBoxCli.send(msg, member);
            }
        }
	}

	public void sendMessage(String message) {
		sendMessage(message, "channel");
	}
}