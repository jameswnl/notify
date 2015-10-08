package notify;

import java.util.Collection;


public interface Mailer {


	public Mailer setRecipients(Collection<Participant> recipients);
	
	public Mailer setCClist(Collection<Participant> recipients);

	public Mailer setSenderEmail(String fromEmail);

	public Mailer setSenderName(String fromName);

	public Mailer setMessageBody(String messageBody);
	
	public Mailer setSubject(String subject);

	public void send(boolean test) throws Exception;
	
}