package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.util.*;
import teammates.logic.core.StudentsLogic;
import teammates.test.MockHttpServletRequest;
import teammates.ui.output.Milestone;
import teammates.ui.output.ProjectData;
import teammates.ui.request.ProjectCreateRequest;

import java.util.ArrayList;
import java.util.Arrays;
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
    protected void testActionFactoryCreateProjectAction() throws Exception {
        log.info("Test Begins.");
        log.info("Test to verify ActionFactory returns CreateProjectAction class");

        ActionFactory actionFactory = new ActionFactory();

        ______TS("Action exists and is retrieved");

        MockHttpServletRequest existingActionServletRequest = new MockHttpServletRequest(
                HttpPost.METHOD_NAME, Const.ResourceURIs.PROJECT);
        existingActionServletRequest.addHeader("Backdoor-Key", Config.BACKDOOR_KEY);
        Action existingAction = actionFactory.getAction(existingActionServletRequest, HttpPost.METHOD_NAME);
        assertTrue(existingAction instanceof CreateProjectAction);
        log.info("Test class type: " + existingAction);
        log.info("Test complete.");
    }


    @Test
    protected void testProjectAttributesBuilder() throws Exception {
        log.info("Test Begins.");
        log.info("The testExecute method is running for the CreateProjectActionTest");
        assertEquals("Test assertion for the CreateProjectActionTest method: 5==5", 5, 5);

        // create mock objects
        String testProjectName = "CSS 553 Final Project";
        ArrayList<Milestone> milestonesList = new ArrayList<Milestone>();
        Milestone testMilestone1 = new Milestone("Final Presentation Milestone", "Description of final presentation requirements.", new Date());
        Milestone testMilestone2 = new Milestone("Final Implementation Milestone", "Description of implementaiton requirements.", new Date());
        milestonesList.add(testMilestone1);
        milestonesList.add(testMilestone2);
        ArrayList<String> mockStudentList = new ArrayList<String>(Arrays.asList("bob@gmail.com", "alice@gmail.com", "sally@gmail.com"));

        // create project attributes with given mock data
        log.info("Creating the ProjectAttributes class with project name " + testProjectName);
        ProjectAttributes ps =
                ProjectAttributes
                        .builder(testProjectName)
                        .withCourseId("86753")
                        .withMilestones(milestonesList)
                        .withStudentList(mockStudentList)
                        .build();

        log.info("Printing the Project Attributes: " + ps);
        log.info("Test complete.");
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
        ArrayList<Milestone> milestonesList = new ArrayList<Milestone>();
        Milestone testMilestone = new Milestone("Milestone 1", "Description for Milestone 1", new Date());
        milestonesList.add(testMilestone);

        ProjectCreateRequest createRequest =
                new ProjectCreateRequest();
        createRequest.setProjectName("new project");
        createRequest.setProjMilestones(milestonesList);
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
