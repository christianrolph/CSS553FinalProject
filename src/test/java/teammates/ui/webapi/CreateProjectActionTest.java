package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.*;
import teammates.logic.core.StudentsLogic;
import teammates.ui.output.Milestone;
import teammates.ui.output.ProjectData;
import teammates.ui.output.ResponseVisibleSetting;
import teammates.ui.output.SessionVisibleSetting;
import teammates.ui.request.ProjectCreateRequest;

import java.util.ArrayList;
import java.util.Date;


/**
 * SUT: {@link CreateFeedbackSessionAction}.
 */
public class CreateProjectActionTest extends BaseActionTest<CreateProjectAction> {

    private static final Logger log = Logger.getLogger();

/*
    @Override
    protected String getActionUri() {
        return null;
    }

    @Override
    protected String getRequestMethod() {
        return null;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        log.info("hello");
        assertEquals("HELLO Assert Test",4, 5);
    }

    @Override
    protected void testAccessControl() throws Exception {

    }
*/

    @Override
    protected String getActionUri() {
        return Const.ResourceURIs.PROJECT;
    }

    @Override
    protected String getRequestMethod() {
        return POST;
    }

    @Test
    @Override
    protected void testExecute() throws Exception {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");

        loginAsInstructor(instructor1ofCourse1.getGoogleId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        ProjectCreateRequest createRequest = getTypicalCreateRequest();

        CreateProjectAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        ProjectData response = (ProjectData) r.getOutput();
/*
        ProjectAttributes createdProject =
                //TODO: get logic method name from Michael
                logic.getFeedbackSession(createRequest.getProjectName(), course.getId());
        assertEquals(createdProject.getCourseId(), response.getCourseId());
        assertEquals(createdProject.getTimeZone().getId(), response.getTimeZone());
        assertEquals(createdProject.getFeedbackSessionName(), response.getProjectName());

        assertEquals(createdProject.getInstructions(), response.getInstructions());

        assertEquals(createdProject.getStartTime().toEpochMilli(), response.getSubmissionStartTimestamp());
        assertEquals(createdProject.getEndTime().toEpochMilli(), response.getSubmissionEndTimestamp());
        assertEquals(createdProject.getGracePeriodMinutes(), response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(createdProject.getSessionVisibleFromTime().toEpochMilli(),
                response.getCustomSessionVisibleTimestamp().longValue());
        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(createdProject.getResultsVisibleFromTime().toEpochMilli(),
                response.getCustomResponseVisibleTimestamp().longValue());

        assertEquals(createdProject.isClosingEmailEnabled(), response.getIsClosingEmailEnabled());
        assertEquals(createdProject.isPublishedEmailEnabled(), response.getIsPublishedEmailEnabled());

        assertEquals(createdProject.getCreatedTime().toEpochMilli(), response.getCreatedAtTimestamp());
        assertNull(createdProject.getDeletedTime());

        assertEquals("new feedback session", response.getProjectName());
        assertEquals("instructions", response.getInstructions());
        assertEquals(1444003051000L, response.getSubmissionStartTimestamp());
        assertEquals(1546003051000L, response.getSubmissionEndTimestamp());
        assertEquals(5, response.getGracePeriod().longValue());

        assertEquals(SessionVisibleSetting.CUSTOM, response.getSessionVisibleSetting());
        assertEquals(1440003051000L, response.getCustomSessionVisibleTimestamp().longValue());

        assertEquals(ResponseVisibleSetting.CUSTOM, response.getResponseVisibleSetting());
        assertEquals(1547003051000L, response.getCustomResponseVisibleTimestamp().longValue());

        assertFalse(response.getIsClosingEmailEnabled());
        assertFalse(response.getIsPublishedEmailEnabled());

        assertNotNull(response.getCreatedAtTimestamp());
        assertNull(response.getDeletedAtTimestamp());

        ______TS("Error: try to add the same session again");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            getJsonResult(getAction(getTypicalCreateRequest(), params));
        });

        ______TS("Error: Invalid parameters (invalid session name > 38 characters)");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            ProjectCreateRequest request = getTypicalCreateRequest();
            request.setProjectName(StringHelperExtension.generateStringOfLength(39));
            getJsonResult(getAction(request, params));
        });

        ______TS("Unsuccessful case: test null session name");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            ProjectCreateRequest request = getTypicalCreateRequest();
            request.setProjectName(null);

            getJsonResult(getAction(request, params));
        });

        ______TS("Add course with extra space (in middle and trailing)");

        createRequest = getTypicalCreateRequest();
        createRequest.setProjectName("Name with extra  space ");

        a = getAction(createRequest, params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        response = (ProjectData) r.getOutput();

        assertEquals("Name with extra space", response.getFeedbackSessionName());
*/
    }

    @Test
    public void testExecute_masqueradeMode_shouldCreateFeedbackSession() {
        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        loginAsAdmin();

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };
        params = addUserIdToParams(instructor1ofCourse1.getGoogleId(), params);

        ProjectCreateRequest createRequest = getTypicalCreateRequest();

        CreateProjectAction a = getAction(createRequest, params);
        JsonResult r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
    }

    private ProjectCreateRequest getTypicalCreateRequest() {
        ProjectCreateRequest createRequest =
                new ProjectCreateRequest();
        createRequest.setProjectName("new project");
        createRequest.setMs(new Milestone("Milestone 1", "Assignment 1 complete", new Date()));
        createRequest.setStudentList(new ArrayList<StudentsLogic>());



        //createRequest.setInstructions("instructions");

        //createRequest.setSubmissionStartTimestamp(1444003051000L);
        //createRequest.setSubmissionEndTimestamp(1546003051000L);
        //createRequest.setGracePeriod(5);

        //createRequest.setSessionVisibleSetting(SessionVisibleSetting.CUSTOM);
        //createRequest.setCustomSessionVisibleTimestamp(1440003051000L);

        //createRequest.setResponseVisibleSetting(ResponseVisibleSetting.CUSTOM);
        //createRequest.setCustomResponseVisibleTimestamp(1547003051000L);

        //createRequest.setClosingEmailEnabled(false);
        //createRequest.setPublishedEmailEnabled(false);

        return createRequest;
    }

    @Test
    @Override
    protected void testAccessControl() throws Exception {
        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        verifyOnlyInstructorsOfTheSameCourseWithCorrectCoursePrivilegeCanAccess(
                Const.InstructorPermissions.CAN_MODIFY_SESSION, params);
    }

}
