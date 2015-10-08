package gsheet;

import java.util.*;
import javax.mail.*;
import javax.mail.internet.*;

import notify.*;


public class GMailer implements Mailer {

	/* (non-Javadoc)
	 * @see gsheet.Mailer#send()
	 */
	@Override
	public void send(boolean test) throws Exception {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		Message msg = new MimeMessage(session);
		msg.setFrom(new InternetAddress(fromEmail, fromName));
		for (Participant recipient: recipients) {
			if (recipient.email != null) {
				msg.addRecipient(Message.RecipientType.TO,
					new InternetAddress(recipient.email, recipient.name));
			}
		}
		for (Participant recipient: ccList) {
			if (recipient.email != null) {
				msg.addRecipient(Message.RecipientType.CC,
					new InternetAddress(recipient.email, recipient.name));
			}
		}

		msg.setSubject(subject);
		msg.setText(messageBody);
		//System.out.println("To send email");
		if (!test) Transport.send(msg);
		//System.out.println("Done sending email");
		//return dumpMsg(msg);
	}
	
	/*private String dumpMsg(Message msg) {
		StringBuilder sb = new StringBuilder();
		try {
			sb.append(msg.getFrom());
			sb.append(msg.getRecipients(Message.RecipientType.TO));
			sb.append(msg.getRecipients(Message.RecipientType.CC));
			sb.append(msg.getSubject());
			sb.append(msg.getContent());
		} catch (MessagingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sb.append(e.toString());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			sb.append(e.toString());
		}
		return sb.toString();
	}*/

	private Collection<Participant> recipients;
	private String fromEmail, fromName;
	private String messageBody;
	private String subject;
	private Collection<Participant> ccList;



	/* (non-Javadoc)
	 * @see gsheet.Mailer#setRecipients(java.util.Map)
	 */
	@Override
	public Mailer setRecipients(Collection<Participant> recipients) {
		this.recipients = recipients;
		return this;
	}


	/* (non-Javadoc)
	 * @see gsheet.Mailer#setFromEmail(java.lang.String)
	 */
	@Override
	public Mailer setSenderEmail(String fromEmail) {
		this.fromEmail = fromEmail;
		return this;
	}


	/* (non-Javadoc)
	 * @see gsheet.Mailer#setFromName(java.lang.String)
	 */
	@Override
	public Mailer setSenderName(String fromName) {
		this.fromName = fromName;
		return this;
	}


	/* (non-Javadoc)
	 * @see gsheet.Mailer#setMessageBody(java.lang.String)
	 */
	@Override
	public Mailer setMessageBody(String messageBody) {
		this.messageBody = messageBody;
		return this;
	}

	@Override
	public Mailer setSubject(String subject) {
		this.subject = subject;
		return this;
	}


	@Override
	public Mailer setCClist(Collection<Participant> recipients) {
		this.ccList = recipients;
		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("To: ");
		for (Participant recipient: recipients) {
			if (recipient.email != null) {
				sb.append(recipient.name+ "<"+recipient.email +">, ");
			}
		}
		sb.append("\nCC: ");
		if (ccList != null) {
			for (Participant recipient: ccList) {
				if (recipient.email != null) {
					sb.append(recipient.name+ "<"+recipient.email +">, ");
				}
			}
		}
		sb.append("\nFrom: " + fromName+ "<"+fromEmail +">\n");
		sb.append("\nSubject : " + subject + "\n\n");
		sb.append(messageBody+"\n\n");
		return sb.toString();
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
}
