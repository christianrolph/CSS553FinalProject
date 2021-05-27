package teammates.ui.webapi;

import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.UnauthorizedAccessException;
import teammates.common.util.Const;
import teammates.common.util.SanitizationHelper;
import teammates.ui.output.ProjectData;
import teammates.ui.output.InstructorPrivilegeData;
import teammates.ui.request.ProjectCreateRequest;

/**
 * Create a feedback session.
 */
class CreateProjectAction extends Action {

    @Override
    AuthType getMinAuthLevel() {
        return AuthType.LOGGED_IN;
    }

    @Override
    void checkSpecificAccessControl() throws UnauthorizedAccessException {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        gateKeeper.verifyAccessible(instructor, course, Const.InstructorPermissions.CAN_MODIFY_SESSION);
    }

    @Override
    JsonResult execute() {
        String courseId = getNonNullRequestParamValue(Const.ParamsNames.COURSE_ID);

        InstructorAttributes instructor = logic.getInstructorForGoogleId(courseId, userInfo.getId());
        CourseAttributes course = logic.getCourse(courseId);

        // Reads HTTP json data and returns a ProjectCreateRequest and validates
        // data by using createRequest.validate()
        ProjectCreateRequest createRequest =
                getAndValidateRequestBody(ProjectCreateRequest.class);

        // removes whitespace from project name received in HTTP data
        String projectName = SanitizationHelper.sanitizeTitle(createRequest.getProjectName());

        ProjectAttributes ps =
                ProjectAttributes
                        .builder(projectName)
                        .withMilestones(createRequest.getProjMilestones())
                        .build();

        try {
            //TODO: get method name from Michael for createProject
            logic.createProject(ps);
        } catch (EntityAlreadyExistsException | InvalidParametersException e) {
            throw new InvalidHttpRequestBodyException(e.getMessage(), e);
        }

        //TODO: update with method names from ProjectAttribute class
        //ps = getNonNullFeedbackSession(ps.getProjectName(), ps.getCourseId());
        ProjectData output = new ProjectData(ps);
        InstructorPrivilegeData privilege = constructInstructorPrivileges(instructor, projectName);
        output.setPrivileges(privilege);


        //return new JsonResult(output);
        return new JsonResult("Test");
    }

}
