package notify;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collection;

import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

public interface RosterInfo {

	//public void work() throws AuthenticationException,
		//	MalformedURLException, IOException, ServiceException, DataSourceException;

	public void work() throws
		MalformedURLException, IOException, DataSourceException, AuthenticationException, ServiceException;

	public String getSubject();

	public String getRecipientTitle();

	public String getMessageBody();

	public Collection<Participant> getParticipants();
	
	public Collection<Participant> getCClist();

	public String getSignature();

	public boolean isSpecialWeek();


	public Participant getSender();

}