import java.util.Calendar;
import java.util.Date;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.DateField;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Form;
import javax.microedition.lcdui.StringItem;
import javax.microedition.lcdui.TextField;
import javax.microedition.midlet.MIDlet;
import javax.microedition.midlet.MIDletStateChangeException;

public class MojMidlet1 extends MIDlet implements CommandListener {

	private static Display wyswietlacz;
	private Form form = new Form("Podaj pesel");
	private Command submit = new Command("OK", Command.SCREEN, 0);
	private Command exit = new Command("Wylacz", Command.EXIT, 1);
	private TextField id_text = new TextField("Podaj Pesel: ", "", 11, TextField.NUMERIC);
	private TextField name_text = new TextField("Podaj Imie: ", "", 11, TextField.ANY);
	private StringItem output = new StringItem("Dane: \n", "");
	private Alert alert = new Alert("");
	private Calendar currentDate = Calendar.getInstance();

	public MojMidlet1() {
		wyswietlacz = Display.getDisplay(this);
		form.addCommand(exit);
		form.addCommand(submit);
		form.append(name_text);
		form.append(id_text);
		form.append(output);
		alert.setTimeout(2000);
		wyswietlacz.setCurrent(form);
	}

	public void setAlert(String text, AlertType type) {
		alert.setString(text);
		alert.setType(type);
	}

	public void setCalendarDate(Calendar cal) {
		int month = Integer.parseInt(id_text.getString().substring(2, 4)) - 1;
		int dayOfMonth = Integer.parseInt(id_text.getString().substring(4, 6));
		int century = getCentury();
		
		if (century == 1800) {
			month -= 80;
		} else if (century == 2000) {
			month -= 20;
		} else if (century == 2100) {
			month -= 40;
		} else if( century == 2200)
			month -= 60;
		
		cal.set(Calendar.MONTH, month);
		cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
	}

	public int countDaysToNextBirthday(Calendar cal) {
		Date dCurrentDate = currentDate.getTime();
		int currentYear = currentDate.get(Calendar.YEAR);
		Calendar countDaysHelper = Calendar.getInstance();
		setCalendarDate(countDaysHelper);
		
		if (hadBirthday(countDaysHelper)) {
			countDaysHelper.set(Calendar.YEAR, (currentYear + 1));
		}
		
		Date dIdDate = countDaysHelper.getTime();

		return (int) Math.abs(((dIdDate.getTime() - dCurrentDate.getTime()) / (1000 * 60 * 60 * 24)));
	}

	public boolean hadBirthday(Calendar cal) {
		return cal.before(currentDate);
	}

	public int getCentury() {
		int checkCentury = id_text.getString().charAt(2) - '0';
		int century = 0;
		if (checkCentury == 0 || checkCentury == 1) {
			century = 1900;
		} else if (checkCentury == 8 || checkCentury == 9) {
			century = 1800;
		} else if (checkCentury == 2 || checkCentury == 3) {
			century = 2000;
		} else if (checkCentury == 4 || checkCentury == 5) {
			century = 2100;
		} else
			century = 2200;
		return century;
	}

	public int getNextBirthdayAge(Calendar cal) {
		int currentYear = currentDate.get(Calendar.YEAR);
		int century = getCentury();
		int idYear = Integer.parseInt(id_text.getString().substring(0, 2));

		int age = currentYear - (idYear + century);
		if (hadBirthday(cal))
			age++;
		return age;
	}

	public boolean verifyIdText() {
		return id_text.getString().length() == 11;
	}

	public char getSex() {
		int sex = id_text.getString().charAt(9) - '0';
		return sex % 2 == 0 ? 'K' : 'M';
	}

	public static Display mojDisplay() {
		return wyswietlacz;
	}

	protected void destroyApp(boolean unconditional) throws MIDletStateChangeException {
		System.err.println("*** DESTROYED ***");
	}

	protected void pauseApp() {
		// TODO Auto-generated method stub
	}

	protected void startApp() throws MIDletStateChangeException {
		form.setCommandListener((CommandListener) this);
	}

	public void commandAction(Command c, Displayable d) {
		if (c == exit) {
			try {
				destroyApp(false);
				notifyDestroyed();
			} catch (MIDletStateChangeException e) {
				e.printStackTrace();
			}
		} else if (c == submit) {
			if (verifyIdText()) {
				Calendar idDate = Calendar.getInstance();
				setCalendarDate(idDate);
				output.setText("Witaj, " + name_text.getString() + "\n" + "Plec: " + getSex() + "\n" + "Dni do urodzin: "
						+ countDaysToNextBirthday(idDate) + "\n" + "Beda to " + getNextBirthdayAge(idDate)
						+ " urodziny!");
			} else {
				setAlert("Niepoprawny numer PESEL", AlertType.ERROR);
				wyswietlacz.setCurrent(alert);
			}

		}
	}
}
