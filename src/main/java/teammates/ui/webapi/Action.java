package teammates.ui.webapi;

import java.lang.reflect.Type;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import teammates.common.datatransfer.UserInfo;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.EntityNotFoundException;
import teammates.common.exception.InvalidHttpParameterException;
import teammates.common.exception.NullHttpParameterException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Config;
import teammates.common.util.Const;
import teammates.common.util.HttpRequestHelper;
import teammates.common.util.JsonUtils;
import teammates.common.util.RecaptchaVerifier;
import teammates.common.util.StringHelper;
import teammates.logic.api.EmailGenerator;
import teammates.logic.api.EmailSender;
import teammates.logic.api.FileStorage;
import teammates.logic.api.Logic;
import teammates.logic.api.LogsProcessor;
import teammates.logic.api.TaskQueuer;
import teammates.logic.api.UserProvision;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.BasicRequest;

/**
 * An "action" to be performed by the system.
 * If the requesting user is allowed to perform the requested action,
 * this object can talk to the back end to perform that action.
 */
public abstract class Action {

    Logic logic = new Logic();
    UserProvision userProvision = new UserProvision();
    GateKeeper gateKeeper = new GateKeeper();
    EmailGenerator emailGenerator = new EmailGenerator();
    TaskQueuer taskQueuer = new TaskQueuer();
    EmailSender emailSender = new EmailSender();
    FileStorage fileStorage = new FileStorage();
    RecaptchaVerifier recaptchaVerifier = new RecaptchaVerifier(Config.CAPTCHA_SECRET_KEY);
    LogsProcessor logsProcessor = new LogsProcessor();

    HttpServletRequest req;
    UserInfo userInfo;
    AuthType authType;

    // buffer to store the request body
    private String requestBody;

    /**
     * Initializes the action object based on the HTTP request.
     */
    public void init(HttpServletRequest req) {
        this.req = req;
        initAuthInfo();
    }

    public void setUserProvision(UserProvision userProvision) {
        this.userProvision = userProvision;
    }

    public void setTaskQueuer(TaskQueuer taskQueuer) {
        this.taskQueuer = taskQueuer;
    }

    public void setEmailSender(EmailSender emailSender) {
        this.emailSender = emailSender;
    }

    public void setFileStorage(FileStorage fileStorage) {
        this.fileStorage = fileStorage;
    }

    public void setRecaptchaVerifier(RecaptchaVerifier recaptchaVerifier) {
        this.recaptchaVerifier = recaptchaVerifier;
    }

    public void setLogsProcessor(LogsProcessor logsProcessor) {
        this.logsProcessor = logsProcessor;
    }

    /**
     * Checks if the requesting user has sufficient authority to access the resource.
     */
    void checkAccessControl() throws UnauthorizedAccessException {
        String userParam = getRequestParamValue(Const.ParamsNames.USER_ID);
        if (userInfo != null && userParam != null && !userInfo.isAdmin && !userInfo.id.equals(userParam)) {
            throw new UnauthorizedAccessException("User " + userInfo.id
                    + " is trying to masquerade as " + userParam + " without admin permission.");
        }

        if (authType.getLevel() < getMinAuthLevel().getLevel()) {
            // Access control level lower than required
            throw new UnauthorizedAccessException("Not authorized to access this resource.");
        }

        if (authType == AuthType.ALL_ACCESS) {
            // All-access pass granted
            return;
        }

        // All other cases: to be dealt in case-by-case basis
        checkSpecificAccessControl();
    }

    private void initAuthInfo() {
        if (Config.BACKDOOR_KEY.equals(req.getHeader("Backdoor-Key"))) {
            authType = AuthType.ALL_ACCESS;
            userInfo = userProvision.getAdminOnlyUser(getRequestParamValue(Const.ParamsNames.USER_ID));
            userInfo.isStudent = true;
            userInfo.isInstructor = true;
            return;
        }

        // The header X-AppEngine-QueueName cannot be spoofed as GAE will strip any user-sent X-AppEngine-QueueName headers.
        // Reference: https://cloud.google.com/appengine/docs/standard/java/taskqueue/push/creating-handlers#reading_request_headers
        String queueNameHeader = req.getHeader("X-AppEngine-QueueName");
        boolean isRequestFromAppEngineQueue = queueNameHeader != null;
        if (isRequestFromAppEngineQueue) {
            userInfo = userProvision.getAdminOnlyUser("AppEngine-" + queueNameHeader);
        } else {
            userInfo = userProvision.getCurrentUser();
        }

        authType = userInfo == null ? AuthType.PUBLIC : AuthType.LOGGED_IN;

        String userParam = getRequestParamValue(Const.ParamsNames.USER_ID);
        if (userInfo != null && userParam != null && userInfo.isAdmin) {
            userInfo = userProvision.getMasqueradeUser(userParam);
            authType = AuthType.MASQUERADE;
        }
    }

    /**
     * Returns the first value for the specified parameter in the HTTP request, or null if such parameter is not found.
     */
    String getRequestParamValue(String paramName) {
        return req.getParameter(paramName);
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request.
     */
    String getNonNullRequestParamValue(String paramName) {
        String value = req.getParameter(paramName);
        if (value == null) {
            throw new NullHttpParameterException(String.format("The [%s] HTTP parameter is null.", paramName));
        }
        return value;
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request as boolean.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    boolean getBooleanRequestParamValue(String paramName) {
        String value = getNonNullRequestParamValue(paramName);
        try {
            return Boolean.parseBoolean(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected boolean value for " + paramName + " parameter, but found: [" + value + "]");
        }
    }

    /**
     * Returns the first value for the specified parameter expected to be present in the HTTP request as long.
     */
    @SuppressWarnings("PMD.PreserveStackTrace")
    long getLongRequestParamValue(String paramName) {
        String value = getNonNullRequestParamValue(paramName);
        try {
            return Long.parseLong(value);
        } catch (IllegalArgumentException e) {
            throw new InvalidHttpParameterException(
                    "Expected long value for " + paramName + " parameter, but found: [" + value + "]");
        }
    }

    /**
     * Returns the request body payload.
     */
    String getRequestBody() {
        if (requestBody == null) {
            requestBody = HttpRequestHelper.getRequestBody(req);
        }
        return requestBody;
    }

    FeedbackSessionAttributes getNonNullFeedbackSession(String feedbackSessionName, String courseId) {
        FeedbackSessionAttributes feedbackSession = logic.getFeedbackSession(feedbackSessionName, courseId);
        if (feedbackSession == null) {
            throw new EntityNotFoundException(new EntityDoesNotExistException("Feedback session not found"));
        }
        return feedbackSession;
    }

    /**
     * Deserializes and validates the request body payload.
     */
    <T extends BasicRequest> T getAndValidateRequestBody(Type typeOfBody) {
        T requestBody = JsonUtils.fromJson(getRequestBody(), typeOfBody);
        if (requestBody == null) {
            throw new NullHttpParameterException("The request body is null");
        }
        requestBody.validate();
        return requestBody;
    }

    /**
     * Gets the unregistered student by the HTTP param.
     */
    Optional<StudentAttributes> getUnregisteredStudent() {
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        if (!StringHelper.isEmpty(key)) {
            StudentAttributes studentAttributes = logic.getStudentForRegistrationKey(key);
            if (studentAttributes == null) {
                return Optional.empty();
            }
            return Optional.of(studentAttributes);
        }
        return Optional.empty();
    }

    InstructorPrivilegeData constructInstructorPrivileges(InstructorAttributes instructor, String feedbackSessionName) {
        InstructorPrivilegeData privilege = new InstructorPrivilegeData();
        privilege.constructCourseLevelPrivilege(instructor.privileges);
        if (feedbackSessionName != null) {
            privilege.setCanSubmitSessionInSections(
                    instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(
                            feedbackSessionName, Const.InstructorPermissions.CAN_SUBMIT_SESSION_IN_SECTIONS));
            privilege.setCanViewSessionInSections(
                    instructor.isAllowedForPrivilege(Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(
                            feedbackSessionName, Const.InstructorPermissions.CAN_VIEW_SESSION_IN_SECTIONS));
            privilege.setCanModifySessionCommentsInSections(
                    instructor.isAllowedForPrivilege(
                            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS)
                            || instructor.isAllowedForPrivilegeAnySection(feedbackSessionName,
                            Const.InstructorPermissions.CAN_MODIFY_SESSION_COMMENT_IN_SECTIONS));
        }
        return privilege;
    }

    /**
     * Gets the minimum access control level required to access the resource.
     */
    abstract AuthType getMinAuthLevel();

    /**
     * Checks the specific access control needs for the resource.
     */
    abstract void checkSpecificAccessControl() throws UnauthorizedAccessException;

    /**
     * Executes the action.
     */
    abstract ActionResult execute();

}
