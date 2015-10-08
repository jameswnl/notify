package notify;

import gsheet.GMailer;
import gsheet.RosterGsheetFriday;
import gsheet.RosterGsheetSunday;

import java.net.MalformedURLException;
import com.google.gdata.util.ServiceException;


public class Notifier {

	public static void main(String[] args) {
		try {
			//new Notifier().notify(new RosterGsheet(args[0], args[1]), new GMailer());
			String dump = new Notifier().notify(new RosterGsheetSunday(args[0], args[1], "2013 PM Serving Team"), new GMailer(), true);
			
			//new Notifier().notify(new RosterGsheetFriday(args[0], args[1], "Smile Bible Study"), new GMailer());
			System.out.println(dump);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String notify(RosterInfo info, Mailer mailer, boolean bTest) throws Exception{

		info.work();
		/*
			StringBuilder sb = new StringBuilder();
			sb.append(info.getRecipientTitle()+"\n\n");
			sb.append(info.getMessageBody()+"\n\n");

			for (Participant s : info.getParticipants()) {
				sb.append(s.post + ": " + s.name + "\n");
			}

			sb.append("\n\n"+info.getSender().name);
			sb.append("\n" + info.getSignature() + "\n");

			String messageBody = sb.toString();
			//System.out.println(messageBody);
		 */
		mailer.setSenderName(info.getSender().name)
		.setSenderEmail(info.getSender().email)
		.setMessageBody(info.getMessageBody())
		.setRecipients(info.getParticipants())
		.setCClist(info.getCClist())
		.setSubject(info.getSubject());
		mailer.send(bTest);
		return mailer.toString();
	}
}
