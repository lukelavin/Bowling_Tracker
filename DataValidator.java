package Bowling_Tracker;

/**
 * Created by lukel on 8/1/2018.
 */
public class DataValidator {
    public DataValidator() {
    }

    public static boolean isValidDate(String date) {
        if(date == null || date.length() != 10)
            return false;

        boolean startsWithYear = isValidYear(date.substring(0,4));
        //System.out.println("Starts with Year: " + startsWithYear);
        boolean dashAfterYear = date.substring(4,5).equals("-");
        //System.out.println("Dash After Year: " + dashAfterYear);
        boolean monthInMiddle = isValidNumber(date.substring(5, 7));
        //System.out.println("Month in Middle " + monthInMiddle);
        boolean dashAfterMonth = date.substring(7,8).equals("-");
        //System.out.println("Dash After Month " + dashAfterMonth);
        boolean dayLast = isValidNumber(date.substring(8, 10));
        //System.out.println("Day last: " + dayLast);

        if(date.length() == 10 && startsWithYear && dashAfterYear && monthInMiddle && dashAfterMonth && dayLast)
            return true;
        else
            return false;
    }

    public static boolean isValidYear(String year) {
        if(year == null || year.length() != 4) {
            return false;
        }

        return(isValidNumber(year));
    }

    public static boolean isValidNumber(String string) {
        try {
            Integer.parseInt(string);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }
}
