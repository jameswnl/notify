package gsheet;
import com.google.gdata.client.spreadsheet.*;
import com.google.gdata.data.spreadsheet.*;
import com.google.gdata.util.*;

import java.io.IOException;
import java.net.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import notify.*;

public class RosterGsheetSunday implements RosterInfo {
	public static void main(String[] args) throws AuthenticationException, MalformedURLException, IOException, ServiceException, DataSourceException {
		new RosterGsheetSunday("", "", "PM Serving Team").work();
	}
	
	public RosterGsheetSunday(String username, String password, String workbookName) throws ServiceException, MalformedURLException {
		service = new SpreadsheetService("MySpreadsheetIntegration-v1");
		service.setUserCredentials(username, password);
		WORKBOOK_NAME = workbookName;
		this.eventDayOfWeek = Calendar.SUNDAY;
	}
	
	private final static String FEED_URL_STRIN = "https://spreadsheets.google.com/feeds/spreadsheets/private/full";
	private WorksheetEntry roster = null, contacts = null, template = null;
	
	protected SpreadsheetService service;
	protected String WORKBOOK_NAME = null,
			ROSTERSHEET_NAME = "Roster",
			CONTACTSHEET_NAME = "contacts",
			TEMPLATESHEET_NAME = "template";
	protected int eventDayOfWeek = Calendar.SUNDAY;
	
	/* (non-Javadoc)
	 * @see notify.RosterInfo#work(java.lang.String[])
	 */
	@Override
	public void work()
			throws AuthenticationException, MalformedURLException, IOException, ServiceException, DataSourceException {


		findWorksheets();
		try {
			getEventRoster();
			getTemplate();
			getEmails();
			adjustment();
			buildMessage();

		} catch (DataSourceException e) {
			e.printStackTrace();
		}
	}
	
	protected void adjustment() {
		Participant communion = null, singspiration = null, msgTranslator = null;
		for (Participant p: this.participants) {
			if (p.post.equalsIgnoreCase("Communion")) {
				communion = p;
			}
			else if (p.post.equalsIgnoreCase("Singspiration")) {
				singspiration = p;
			}
			else if (p.post.equalsIgnoreCase("Message translator")) {
				msgTranslator = p;
			}
		}
		if (communion == null && singspiration != null) {
			communion = new Participant("Communion", singspiration.name, singspiration.email);
			this.participants.add(communion);
		}
		if (msgTranslator == null) {
			this.participants.add(new Participant("Message Translator", "***TBA***", null));
		}
		
	}
	
	protected void buildMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(getRecipientTitle()+"\n\n");
		Participant remarks = null;
		if (this.isSpecialWeek) {
			sb.append("This coming gathering is a special arrangement.");
		}
		else {
			sb.append(opening +"\n\n");

			for (Participant s : getParticipants()) {
				if (s.post.equalsIgnoreCase("Remarks")) {
					remarks = s;
				}
				else {
					sb.append(s.post + ": " + s.name + "\n");
				}
			}
		}
		if (remarks != null) {
			sb.append("\n\n***** " + remarks.post + ": " + remarks.name + " *****\n");
		}
		sb.append("\n\n"+getSender().name);
		sb.append("\n" + getSignature() + "\n");
		
		messageBody = sb.toString();
		//System.out.println("****\n"+messageBody+"****\n");
	}
	
	private void findWorksheets() throws AuthenticationException, MalformedURLException, IOException, ServiceException, DataSourceException {
		
		// Make a request to the API and get all spreadsheets.
		URL SPREADSHEET_FEED_URL = new URL(FEED_URL_STRIN);
		SpreadsheetFeed feed = service.getFeed(SPREADSHEET_FEED_URL, SpreadsheetFeed.class);
		List<SpreadsheetEntry> spreadsheets = feed.getEntries();
		
		int toFind = 3;
		// Iterate through all of the spreadsheets returned
		for (SpreadsheetEntry spreadsheet : spreadsheets) {
			//System.out.println(spreadsheet.getTitle().getPlainText());
			if (!spreadsheet.getTitle().getPlainText().equalsIgnoreCase(WORKBOOK_NAME)) {
				continue;
			}
			List<WorksheetEntry> worksheets = spreadsheet.getWorksheets();

			// Iterate through each worksheet in the spreadsheet.
			for (WorksheetEntry worksheet : worksheets) {
				// Get the worksheet's title, row count, and column count.
				String title = worksheet.getTitle().getPlainText();
				if (title.equalsIgnoreCase(ROSTERSHEET_NAME)) {
					roster = worksheet;
					toFind--;
				}
				else if (title.equalsIgnoreCase(CONTACTSHEET_NAME)){
					contacts = worksheet;
					toFind--;
				}
				else if (title.equalsIgnoreCase(TEMPLATESHEET_NAME)){
					template = worksheet;
					toFind--;
				}

				if (toFind == 0) {
					return;
				}
			}
			break;
		}
		throw new DataSourceException();
	}
	

	private Map<String, Participant> nameMap = new HashMap<String, Participant>();
	private Collection<Participant> participants = new ArrayList<Participant>();
	private Collection<Participant> ccList = new ArrayList<Participant>();
/*	private String[] posts = {
			"Singspiration", "Accompanist", "Communion",
			"Translator", "BreadCups", "Watchman", "SundaySchool",
			"Speaker", "Speaker2", "Speaker3", "MsgTranslator"};*/
	private boolean isSpecialWeek = false;

	private String subject, recipientTitle;
	private String signature;
	private String eventDateString;
	private Participant sender;
	
	protected String messageBody;
	protected String opening;
	
	private void getEmails() throws IOException, ServiceException {
		URL listFeedUrl = contacts.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		
		for (ListEntry row : listFeed.getEntries()) {
			String name = row.getCustomElements().getValue("Name");
			String email = row.getCustomElements().getValue("Email");
			Participant p = nameMap.get(name);
			if (p != null) {
				p.email = email;
				continue;
			}
		}
	}
	
	
	private void getTemplate() throws IOException, ServiceException, DataSourceException {
		URL listFeedUrl = template.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);
		
		List<ListEntry> rows = listFeed.getEntries();
		if (rows.isEmpty()) throw new DataSourceException();
		ListEntry row = rows.get(0);
		this.subject = row.getCustomElements().getValue("subject") + " " + this.eventDateString;
		this.recipientTitle = row.getCustomElements().getValue("greetings");
		this.opening = row.getCustomElements().getValue("message");
		this.signature = row.getCustomElements().getValue("signature");
		
		//System.out.print("getTemplate::tag=\n");
		for (String tag : row.getCustomElements().getTags()) {
			String value = row.getCustomElements().getValue(tag);
			if (value == null) continue;
			Participant p = null;
			if (tag.toLowerCase().startsWith("cc")) {
				//System.out.print(":::" + tag + "=" + name + "\t");
				p = new Participant(tag, value, null);
			}
			else if (tag.equalsIgnoreCase("sender")) {
				p = new Participant(tag, value, null);
				sender = p;
			}
			if (p != null) {
				ccList.add(p);
				nameMap.put(value, p);
			}
		}
	}

	
	private Date getEventDate() {
		// expected event date
		Calendar c = Calendar.getInstance();
		c.set(Calendar.DAY_OF_WEEK, eventDayOfWeek);
		Calendar eventDay = Calendar.getInstance();
		eventDay.clear();
		eventDay.set(Calendar.YEAR, c.get(Calendar.YEAR));
		eventDay.set(Calendar.MONTH, c.get(Calendar.MONTH));
		eventDay.set(Calendar.DAY_OF_MONTH, c.get(Calendar.DAY_OF_MONTH));
		if (eventDay.before(Calendar.getInstance())) {
			//sunday is start of week
			eventDay.add(Calendar.DAY_OF_MONTH, 7);
		}
		return eventDay.getTime();
	}
	
	private void getEventRoster() throws IOException, ServiceException, DataSourceException{
		URL listFeedUrl = roster.getListFeedUrl();
		ListFeed listFeed = service.getFeed(listFeedUrl, ListFeed.class);

		Date eventDate = getEventDate();
		
		ListEntry matchedRow = null;
		List<ListEntry> dataset = listFeed.getEntries();
		if (!dataset.isEmpty()) {
			for (ListEntry row : listFeed.getEntries()) {
				String rowDate = row.getCustomElements().getValue("date");
				eventDateString = rowDate;
				//System.out.println("row date="+rowDate);
				SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");      
				try {
					Date date = sdf.parse(rowDate);
					if (date.equals(eventDate)) {
						//System.out.println("date="+date + "|" + match);
						matchedRow = row;
						break;
					}
				} catch (ParseException e) {
					//e.printStackTrace();
				}
			}
		}
		
		if (matchedRow == null) {
			System.out.println("Error, no schedule found");
			throw new DataSourceException();
		}
		
		for (String tag : matchedRow.getCustomElements().getTags()) {
			//System.out.print(":::" + tag+ "=" + row.getCustomElements().getValue(tag) + "\t");
			//System.out.print(":::tag=" + tag + "\t");
			if (tag.equalsIgnoreCase("date")) continue;
			
			String name = matchedRow.getCustomElements().getValue(tag);
			if (name == null || name.length() == 0) {
				continue;
			}
			
			if ("Special".equalsIgnoreCase(name)) {
				System.out.println("Special week");
				isSpecialWeek = true;
			}
			String post = normalizeColumnName(tag);
			Participant s = new Participant(post, name, null);
			participants.add(s);
			nameMap.put(name, s);
		}
	}
	
	protected static String normalizeColumnName(String post) {
		post = post.replace('-', ' ');
		post = post.substring(0, 1).toUpperCase() + post.substring(1);
		return post;
	}
	
	/* (non-Javadoc)
	 * @see notify.RosterInfo#getRecipients()
	 */
	@Override
	public Collection<Participant> getParticipants() {
		return this.participants;
	}
	
	
	/* (non-Javadoc)
	 * @see notify.RosterInfo#getSenderName()
	 */
	@Override
	public Participant getSender() {
		return sender;
	}
	
	
	/* (non-Javadoc)
	 * @see notify.RosterInfo#getSubject()
	 */
	@Override
	public String getSubject() {
		return subject;
	}

	/* (non-Javadoc)
	 * @see notify.RosterInfo#getRecipientTitle()
	 */
	@Override
	public String getRecipientTitle() {
		return recipientTitle;
	}

	/* (non-Javadoc)
	 * @see notify.RosterInfo#getMessageBody()
	 */
	@Override
	public String getMessageBody() {
		return messageBody;
	}

	/* (non-Javadoc)
	 * @see notify.RosterInfo#getSignature()
	 */
	@Override
	public String getSignature() {
		return signature;
	}
	
	/* (non-Javadoc)
	 * @see notify.RosterInfo#isSpecialWeek()
	 */
	@Override
	public boolean isSpecialWeek() {
		return isSpecialWeek;
	}

	@Override
	public Collection<Participant> getCClist() {
		return ccList;
	}
	
	
}