package teammates.logic.core;

import java.time.Instant;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

import teammates.common.datatransfer.AttributesDeletionQuery;
import teammates.common.datatransfer.CourseRoster;
import teammates.common.datatransfer.FeedbackParticipantType;
import teammates.common.datatransfer.UserRole;
import teammates.common.datatransfer.attributes.FeedbackQuestionAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseAttributes;
import teammates.common.datatransfer.attributes.FeedbackResponseCommentAttributes;
import teammates.common.datatransfer.attributes.FeedbackSessionAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.datatransfer.attributes.StudentAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.storage.api.FeedbackResponseCommentsDb;

/**
 * Handles operations related to feedback response comments.
 *
 * @see FeedbackResponseCommentAttributes
 * @see FeedbackResponseCommentsDb
 */
public final class FeedbackResponseCommentsLogic {

    private static FeedbackResponseCommentsLogic instance = new FeedbackResponseCommentsLogic();

    private static final FeedbackResponseCommentsDb frcDb = new FeedbackResponseCommentsDb();

    private static final CoursesLogic coursesLogic = CoursesLogic.inst();
    private static final FeedbackResponsesLogic frLogic = FeedbackResponsesLogic.inst();
    private static final FeedbackSessionsLogic fsLogic = FeedbackSessionsLogic.inst();
    private static final InstructorsLogic instructorsLogic = InstructorsLogic.inst();
    private static final StudentsLogic studentsLogic = StudentsLogic.inst();

    private FeedbackResponseCommentsLogic() {
        // prevent initialization
    }

    public static FeedbackResponseCommentsLogic inst() {
        return instance;
    }

    /**
     * Creates a feedback response comment.
     *
     * <p>If the comment is given by feedback participant, ownership of the corresponding response
     * of the comment is not checked.</p>
     */
    public FeedbackResponseCommentAttributes createFeedbackResponseComment(FeedbackResponseCommentAttributes frComment)
            throws InvalidParametersException, EntityDoesNotExistException, EntityAlreadyExistsException {
        verifyIsCoursePresent(frComment.courseId);
        verifyIsUserOfCourse(frComment.courseId, frComment.commentGiver, frComment.commentGiverType,
                frComment.isCommentFromFeedbackParticipant);
        verifyIsFeedbackSessionOfCourse(frComment.courseId, frComment.feedbackSessionName);

        return frcDb.createEntity(frComment);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(Long feedbackResponseCommentId) {
        return frcDb.getFeedbackResponseComment(feedbackResponseCommentId);
    }

    public FeedbackResponseCommentAttributes getFeedbackResponseComment(
            String responseId, String giverEmail, Instant creationDate) {
        return frcDb.getFeedbackResponseComment(responseId, giverEmail, creationDate);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForResponse(String feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentsForResponse(feedbackResponseId);
    }

    /**
     * Gets comment associated with the response.
     *
     * <p>The comment is given by a feedback participant to explain the response</p>
     *
     * @param feedbackResponseId the response id
     */
    public FeedbackResponseCommentAttributes getFeedbackResponseCommentForResponseFromParticipant(
            String feedbackResponseId) {
        return frcDb.getFeedbackResponseCommentForResponseFromParticipant(feedbackResponseId);
    }

    /**
     * Gets all feedback response comments for session in a section.
     *
     * @param courseId the course ID of the feedback session
     * @param feedbackSessionName the feedback session name
     * @param section if null, will retrieve all comments in the session
     * @return a list of feedback response comments
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForSessionInSection(
            String courseId, String feedbackSessionName, @Nullable String section) {
        if (section == null) {
            return frcDb.getFeedbackResponseCommentsForSession(courseId, feedbackSessionName);
        }
        return frcDb.getFeedbackResponseCommentsForSessionInSection(courseId, feedbackSessionName, section);
    }

    /**
     * Gets all feedback response comments for a question in a section.
     *
     * @param questionId the ID of the question
     * @param section if null, will retrieve all comments for the question
     * @return a list of feedback response comments
     */
    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentForQuestionInSection(
            String questionId, @Nullable String section) {
        if (section == null) {
            return frcDb.getFeedbackResponseCommentsForQuestion(questionId);
        }
        return frcDb.getFeedbackResponseCommentsForQuestionInSection(questionId, section);
    }

    /*
     * Updates all email fields of feedback response comments with the new email
     */
    public void updateFeedbackResponseCommentsEmails(String courseId, String oldEmail, String updatedEmail) {
        frcDb.updateGiverEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
        frcDb.updateLastEditorEmailOfFeedbackResponseComments(courseId, oldEmail, updatedEmail);
    }

    // right now this method only updates comment's giverSection and receiverSection for a given response
    public void updateFeedbackResponseCommentsForResponse(String feedbackResponseId)
            throws InvalidParametersException, EntityDoesNotExistException {
        List<FeedbackResponseCommentAttributes> comments = getFeedbackResponseCommentForResponse(feedbackResponseId);
        FeedbackResponseAttributes response = frLogic.getFeedbackResponse(feedbackResponseId);
        for (FeedbackResponseCommentAttributes comment : comments) {
            frcDb.updateFeedbackResponseComment(
                    FeedbackResponseCommentAttributes.updateOptionsBuilder(comment.getId())
                            .withGiverSection(response.giverSection)
                            .withReceiverSection(response.recipientSection)
                            .build()
            );
        }
    }

    /**
     * Updates a feedback response comment by {@link FeedbackResponseCommentAttributes.UpdateOptions}.
     *
     * @return updated comment
     * @throws InvalidParametersException if attributes to update are not valid
     * @throws EntityDoesNotExistException if the comment cannot be found
     */
    public FeedbackResponseCommentAttributes updateFeedbackResponseComment(
            FeedbackResponseCommentAttributes.UpdateOptions updateOptions)
            throws InvalidParametersException, EntityDoesNotExistException {

        return frcDb.updateFeedbackResponseComment(updateOptions);
    }

    public List<FeedbackResponseCommentAttributes> getFeedbackResponseCommentsForGiver(String courseId,
                                                                                       String giverEmail) {
        return frcDb.getFeedbackResponseCommentForGiver(courseId, giverEmail);
    }

    /**
     * Deletes a comment.
     */
    public void deleteFeedbackResponseComment(long commentId) {
        frcDb.deleteFeedbackResponseComment(commentId);
    }

    /**
     * Deletes comments using {@link AttributesDeletionQuery}.
     */
    public void deleteFeedbackResponseComments(AttributesDeletionQuery query) {
        frcDb.deleteFeedbackResponseComments(query);
    }

    /**
     * Returns true if the comment's giver name is visible to certain user.
     */
    public boolean isNameVisibleToUser(FeedbackResponseCommentAttributes comment, FeedbackResponseAttributes response,
                                   String userEmail, CourseRoster roster) {
        List<FeedbackParticipantType> showNameTo = comment.showGiverNameTo;
        //in the old ver, name is always visible
        if (showNameTo == null || comment.isVisibilityFollowingFeedbackQuestion) {
            return true;
        }

        //comment giver can always see
        if (userEmail.equals(comment.commentGiver)) {
            return true;
        }

        return isFeedbackParticipantNameVisibleToUser(response, userEmail, roster, showNameTo);
    }

    private boolean isFeedbackParticipantNameVisibleToUser(FeedbackResponseAttributes response,
            String userEmail, CourseRoster roster, List<FeedbackParticipantType> showNameTo) {
        String responseGiverTeam = "giverTeam";
        if (roster.getStudentForEmail(response.giver) != null) {
            responseGiverTeam = roster.getStudentForEmail(response.giver).team;
        }
        String responseRecipientTeam = "recipientTeam";
        if (roster.getStudentForEmail(response.recipient) != null) {
            responseRecipientTeam = roster.getStudentForEmail(response.recipient).team;
        }
        String currentUserTeam = "currentUserTeam";
        if (roster.getStudentForEmail(userEmail) != null) {
            currentUserTeam = roster.getStudentForEmail(userEmail).team;
        }
        for (FeedbackParticipantType type : showNameTo) {
            switch (type) {
            case INSTRUCTORS:
                if (roster.getInstructorForEmail(userEmail) != null) {
                    return true;
                }
                break;
            case OWN_TEAM_MEMBERS:
                if (responseGiverTeam.equals(currentUserTeam)) {
                    return true;
                }
                break;
            case RECEIVER:
                if (userEmail.equals(response.recipient)) {
                    return true;
                }
                break;
            case RECEIVER_TEAM_MEMBERS:
                if (responseRecipientTeam.equals(currentUserTeam)) {
                    return true;
                }
                break;
            case STUDENTS:
                if (roster.getStudentForEmail(userEmail) != null) {
                    return true;
                }
                break;
            case GIVER:
                if (userEmail.equals(response.giver)) {
                    return true;
                }
                break;
            default:
                break;
            }
        }
        return false;
    }

    /**
     * Verifies whether the comment is visible to certain user.
     * @return true/false
     */
    public boolean isResponseCommentVisibleForUser(String userEmail, UserRole role,
            StudentAttributes student, Set<String> studentsEmailInTeam, FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, FeedbackResponseCommentAttributes relatedComment) {

        if (response == null || relatedQuestion == null) {
            return false;
        }

        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.isVisibilityFollowingFeedbackQuestion;
        boolean isVisibleToGiver = isVisibilityFollowingFeedbackQuestion
                                 || relatedComment.isVisibleTo(FeedbackParticipantType.GIVER);

        boolean isUserInstructor = role == UserRole.INSTRUCTOR;
        boolean isUserStudent = role == UserRole.STUDENT;

        boolean isVisibleToUser = isVisibleToUser(userEmail, response, relatedQuestion, relatedComment,
                isVisibleToGiver, isUserInstructor, isUserStudent);

        boolean isVisibleToUserTeam = isVisibleToUserTeam(student, studentsEmailInTeam, response,
                relatedQuestion, relatedComment, isUserStudent);

        return isVisibleToUser || isVisibleToUserTeam;
    }

    private boolean isVisibleToUserTeam(StudentAttributes student, Set<String> studentsEmailInTeam,
            FeedbackResponseAttributes response, FeedbackQuestionAttributes relatedQuestion,
            FeedbackResponseCommentAttributes relatedComment, boolean isUserStudent) {

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients =
                isUserStudent
                && relatedQuestion.recipientType == FeedbackParticipantType.TEAMS
                && isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              FeedbackParticipantType.RECEIVER)
                && response.recipient.equals(student.team);

        boolean isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers =
                (relatedQuestion.giverType == FeedbackParticipantType.TEAMS
                || isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                              FeedbackParticipantType.OWN_TEAM_MEMBERS))
                && (studentsEmailInTeam.contains(response.giver)
                        || (isUserStudent && student.getTeam().equals(response.giver)));

        boolean isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers =
                isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                           FeedbackParticipantType.RECEIVER_TEAM_MEMBERS)
                && studentsEmailInTeam.contains(response.recipient);

        return isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipients
                || isUserInResponseGiverTeamAndRelatedResponseCommentVisibleToGiversTeamMembers
                || isUserInResponseRecipientTeamAndRelatedResponseCommentVisibleToRecipientsTeamMembers;
    }

    private boolean isVisibleToUser(String userEmail, FeedbackResponseAttributes response,
            FeedbackQuestionAttributes relatedQuestion, FeedbackResponseCommentAttributes relatedComment,
            boolean isVisibleToGiver, boolean isUserInstructor, boolean isUserStudent) {

        boolean isUserInstructorAndRelatedResponseCommentVisibleToInstructors =
                isUserInstructor && isResponseCommentVisibleTo(relatedQuestion, relatedComment,
                                                               FeedbackParticipantType.INSTRUCTORS);

        boolean isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients =
                response.recipient.equals(userEmail) && isResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, FeedbackParticipantType.RECEIVER);

        boolean isUserResponseGiverAndRelatedResponseCommentVisibleToGivers =
                response.giver.equals(userEmail) && isVisibleToGiver;

        boolean isUserRelatedResponseCommentGiver = relatedComment.commentGiver.equals(userEmail);

        boolean isUserStudentAndRelatedResponseCommentVisibleToStudents =
                isUserStudent && isResponseCommentVisibleTo(relatedQuestion,
                        relatedComment, FeedbackParticipantType.STUDENTS);

        return isUserInstructorAndRelatedResponseCommentVisibleToInstructors
                || isUserResponseRecipientAndRelatedResponseCommentVisibleToRecipients
                || isUserResponseGiverAndRelatedResponseCommentVisibleToGivers
                || isUserRelatedResponseCommentGiver
                || isUserStudentAndRelatedResponseCommentVisibleToStudents;
    }

    private boolean isResponseCommentVisibleTo(FeedbackQuestionAttributes relatedQuestion,
                                               FeedbackResponseCommentAttributes relatedComment,
                                               FeedbackParticipantType viewerType) {
        boolean isVisibilityFollowingFeedbackQuestion = relatedComment.isVisibilityFollowingFeedbackQuestion;
        return isVisibilityFollowingFeedbackQuestion
                ? relatedQuestion.isResponseVisibleTo(viewerType)
                : relatedComment.isVisibleTo(viewerType);
    }

    private void verifyIsCoursePresent(String courseId) throws EntityDoesNotExistException {
        if (!coursesLogic.isCoursePresent(courseId)) {
            throw new EntityDoesNotExistException(
                    "Trying to create feedback response comments for a course that does not exist.");
        }
    }

    /**
     * Verifies if comment giver is registered user/team of course.
     *
     * @param courseId id of course
     * @param commentGiver person/team who gave comment
     * @param commentGiverType type of comment giver
     */
    private void verifyIsUserOfCourse(String courseId, String commentGiver, FeedbackParticipantType commentGiverType,
            boolean isCommentFromFeedbackParticipant) throws EntityDoesNotExistException {
        if (!isCommentFromFeedbackParticipant) {
            InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, commentGiver);
            if (instructor == null) {
                throw new EntityDoesNotExistException("User " + commentGiver
                        + " is not a registered instructor for course " + courseId + ".");
            }
            return;
        }
        switch (commentGiverType) {
        case STUDENTS:
            StudentAttributes student = studentsLogic.getStudentForEmail(courseId, commentGiver);
            if (student == null) {
                throw new EntityDoesNotExistException("User " + commentGiver + " is not a registered student for course "
                        + courseId + ".");
            }
            break;
        case INSTRUCTORS:
            InstructorAttributes instructor = instructorsLogic.getInstructorForEmail(courseId, commentGiver);
            if (instructor == null) {
                throw new EntityDoesNotExistException("User " + commentGiver
                        + " is not a registered instructor for course " + courseId + ".");
            }
            break;
        case TEAMS:
            List<String> teams = coursesLogic.getTeamsForCourse(courseId);
            boolean isTeamPresentInCourse = false;
            for (String team : teams) {
                if (team.equals(commentGiver)) {
                    isTeamPresentInCourse = true;
                    break;
                }
            }
            if (!isTeamPresentInCourse) {
                throw new EntityDoesNotExistException("User " + commentGiver + " is not a registered team for course "
                        + courseId + ".");
            }
            break;
        default:
            throw new EntityDoesNotExistException("Unknown giver type: " + commentGiverType);
        }
    }

    private void verifyIsFeedbackSessionOfCourse(String courseId, String feedbackSessionName)
            throws EntityDoesNotExistException {
        FeedbackSessionAttributes session = fsLogic.getFeedbackSession(feedbackSessionName, courseId);
        if (session == null) {
            throw new EntityDoesNotExistException("Feedback session " + feedbackSessionName
                                                + " is not a session for course " + courseId + ".");
        }
    }

}
