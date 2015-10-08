package notify;

public class Participant {
	public Participant(String postIn, String nameIn, String emailIn, boolean isAdminIn) {
		this(postIn, nameIn, emailIn);
		isAdmin = isAdminIn;
	}
	
	public Participant(String postIn, String nameIn, String emailIn) {
		post = postIn;
		name = nameIn;
		email = emailIn;
	}
	public String post, name, email;
	public boolean isAdmin = false;
	
	@Override
	public String toString() {
		return "post=" + post + ", name=" + name + ", email=" + email + ", admin=" + isAdmin;
	}
	
	@Override
	public int hashCode() {
		return toString().hashCode();
	}
}
