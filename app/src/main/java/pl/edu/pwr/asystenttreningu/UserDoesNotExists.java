package pl.edu.pwr.asystenttreningu;

/**
 * Created by michalos on 16.11.14.
 */
public class UserDoesNotExists extends Exception {
    UserDoesNotExists(String txt){
        super(txt);
    }
}
