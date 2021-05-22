package teammates.logic.core;

import teammates.common.util.EmailSendingStatus;
import teammates.common.util.EmailWrapper;

/**
 * An email sender interface used by services for sending emails.
 */
public interface EmailSenderService {

    /**
     * Parses the {@code wrapper} email object to specific implementations of email object
     * used by the service.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // accounts for the many different Exceptions from different email services
    Object parseToEmail(EmailWrapper wrapper) throws Exception;

    /**
     * Sends the email packaged as a {@code wrapper}.
     */
    @SuppressWarnings("PMD.SignatureDeclareThrowsException")
    // accounts for the many different Exceptions from different email services
    EmailSendingStatus sendEmail(EmailWrapper wrapper) throws Exception;

}
