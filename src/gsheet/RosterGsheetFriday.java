/**
 * 
 */
package gsheet;

import java.net.MalformedURLException;
import java.util.Calendar;
import com.google.gdata.util.ServiceException;

/**
 * @author jwong
 *
 */
public class RosterGsheetFriday extends RosterGsheetSunday {

	/**
	 * @param username
	 * @param password
	 * @param workbookName
	 * @throws ServiceException
	 * @throws MalformedURLException
	 */
	public RosterGsheetFriday(String username, String password,
			String workbookName) throws ServiceException, MalformedURLException {
		super(username, password, workbookName);
		eventDayOfWeek = Calendar.FRIDAY;
	}
	
	@Override
	protected void adjustment() {
		//do nothing
	}
}
