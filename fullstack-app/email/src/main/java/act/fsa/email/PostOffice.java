package act.fsa.email;

import act.conf.AppConfig;
import act.job.Every;
import act.mail.Mailer;
import org.osgl.logging.L;
import org.osgl.logging.Logger;
import org.osgl.util.E;
import org.osgl.util.S;

import javax.inject.Inject;

/**
 * This class demonstrate how to write an email sender
 * in act framework.
 */
@Mailer
public class PostOffice extends Mailer.Util {

    private static Logger logger = L.get(PostOffice.class);

    @Inject
    private AppConfig conf;

    /**
     * This method set the {@code recipients} to {@code who}
     * and it will use the template located at
     * {@code resources/rythm/act/fsa/email/PostOffice/sendWelcome.html}
     * to render the mail content
     * @param who the recipient
     */
    public void sendWelcome(String who) {
        mailer().to(emailOf(who)).subject("Welcome");
        send(who);
    }

    /**
     * This method set the {@code recipients} to {@code who}
     * and it will use the template located at
     * {@code resources/rythm/act/fsa/email/PostOffice/sendBye.html}
     * to render the mail content
     * @param who the recipient
     */
    public void sendBye(String who) {
        to(emailOf(who));
        send(who);
    }

    private String emailOf(String who) {
        if (!isValidEmail(who)) {
            logger.warn("Invalid email address: %s", who);
            who = (String) conf.get("app.default.recipient");
            if (!isValidEmail(who)) {
                throw E.invalidConfiguration("configuration of 'app.default.recipient' is not a valid email: %s", who);
            }
        }
        return who;
    }

    private static boolean isValidEmail(String who) {
        if (S.blank(who)) return false;
        return who.toLowerCase().matches("^[_a-z0-9-']+(\\.[_a-z0-9-']+)*(\\+[0-9]+)?@[a-z0-9-]+(\\.[a-z0-9-]+)*(\\.[a-z]{2,4})$");
    }

    /**
     * This will get invoked every 60 seconds.
     * <p>The method will fetch the mail configuration from "notification" to get the
     * recipients, the subject. And it will use template located at
     * {code resources/rythm/act/fsa/email/PostOffice/sendNotification.html} to
     * generate the mail content</p>
     */
    @Every("60s")
    @Mailer("notification")
    public static void sendNotification() {
        logger.info("Sending out notification...");
        send();
    }

}
