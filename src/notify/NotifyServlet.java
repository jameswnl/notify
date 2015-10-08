package notify;


import gsheet.GMailer;
import gsheet.RosterGsheetFriday;
import gsheet.RosterGsheetSunday;
import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class NotifyServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5786573676021906844L;
	private static final Logger log = Logger.getLogger(NotifyServlet.class.getName());
	private String USERNAME = "";
	private String PASSWORD = "";
	private static String DEFAULT_SPREADSHEET = "SundayTeam";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {

		resp.setContentType("text/plain");
		String event = req.getParameter("event");
		String sheet = req.getParameter("sheet");
		String send = req.getParameter("send");
		boolean test = send == null || !send.equalsIgnoreCase("true");
		resp.getWriter().printf("Input: \nEvent=%s, toSend=%s, sheet=%s\n", event, send, sheet);
		
		try {
			resp.getWriter().println(work(event, sheet, test));
			resp.getWriter().println("\n\nDone");
		} catch (Exception e) {
			e.printStackTrace(resp.getWriter());
		}
		
	}


	private String work(String event, String sheetname, boolean test) throws Exception {
		if (event.equals("friday")) {
			log.info("Calling friday Notifier.main()");
			if (sheetname == null || sheetname.length() == 0) {
				sheetname = "Smile Bible Study";
			}
			return new Notifier().notify(new RosterGsheetFriday(USERNAME, PASSWORD, sheetname), new GMailer(), test);
		}
		else if (event.equals("sunday")) {
			log.info("Calling sunday Notifier.main()");
			if (sheetname == null || sheetname.length() == 0) {
				sheetname = NotifyServlet.DEFAULT_SPREADSHEET;
			}
			return new Notifier().notify(new RosterGsheetSunday(USERNAME, PASSWORD, sheetname), new GMailer(), test);
		}
		return "Unknown Event";
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		doGet(req, resp);
	}

}