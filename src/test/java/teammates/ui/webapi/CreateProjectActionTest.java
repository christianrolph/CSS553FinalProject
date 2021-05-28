package teammates.ui.webapi;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.testng.annotations.Test;
import teammates.common.datatransfer.attributes.CourseAttributes;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.common.datatransfer.attributes.InstructorAttributes;
import teammates.common.exception.InvalidHttpRequestBodyException;
import teammates.common.util.*;
import teammates.logic.core.StudentsLogic;
import teammates.test.MockHttpServletRequest;
import teammates.ui.output.Milestone;
import teammates.ui.output.ProjectData;
import teammates.ui.request.ProjectCreateRequest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;


/**
 * SUT: {@link CreateFeedbackSessionAction}.
 */
public class CreateProjectActionTest extends BaseActionTest<CreateProjectAction> {

    private static final Logger log = Logger.getLogger();

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
        log.info("Creating Mock HTTP Servlet Request: " + "method = " + existingActionServletRequest.getMethod() +
                " -- uri = " + existingActionServletRequest.getRequestURI());

        Action existingAction = actionFactory.getAction(existingActionServletRequest, HttpPost.METHOD_NAME);
        assertTrue(existingAction instanceof CreateProjectAction);
        log.info("[FEATURE] Test class type returned from ActionFactory: " + existingAction);
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
        log.info("Test Begins.");
        log.info("The testExecute method is running for the CreateProjectActionTest");

        InstructorAttributes instructor1ofCourse1 = typicalBundle.instructors.get("instructor1OfCourse1");
        log.info("Creating Instructor: " + instructor1ofCourse1.getCourseId());

        loginAsInstructor(instructor1ofCourse1.getGoogleId());
        log.info("Logging in as instructor: " + instructor1ofCourse1.getCourseId());

        ______TS("Not enough parameters");

        verifyHttpParameterFailure();

        ______TS("Typical case");

        CourseAttributes course = typicalBundle.courses.get("typicalCourse1");
        log.info("Creating Course: " + instructor1ofCourse1.getCourseId());

        String[] params = {
                Const.ParamsNames.COURSE_ID, course.getId(),
        };

        ProjectCreateRequest createRequest = getTypicalCreateRequest(course.getId());
        log.info("[FEATURE] Creating Project Request: " + createRequest.toString());


        CreateProjectAction a = getAction(createRequest, params);
        log.info("[FEATURE] Creating Create Project Action: " + a);

        JsonResult r = getJsonResult(a);
        log.info("[FEATURE] Performing Create Project Action 'Execute()'");

        // Test HTTP Status code returned
        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        log.info("[FEATURE] Testing HTTP Response Status Code" + r.getStatusCode());

        ProjectData response = (ProjectData) r.getOutput();
        log.info("[FEATURE] Response Data: " + response.toString());


        // test CreateProjectAction object (ProjectDB facing) and ProjectData object returned from CreateProjectAction
        assertEquals(createRequest.getProjectName(), response.getProjectName());
        assertEquals(createRequest.getCourseID(), response.getCourseID());
        assertEquals(createRequest.getProjMilestones(), response.getMilestones());
        assertEquals(createRequest.getStudentList(), response.getStudentList());
        ______TS("Error: Invalid parameters (invalid Project name > 38 characters)");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            ProjectCreateRequest request = getTypicalCreateRequest(course.getId());
            request.setProjectName(StringHelperExtension.generateStringOfLength(39));
            getJsonResult(getAction(request, params));
        });

        ______TS("Unsuccessful case: test null project name");

        assertThrows(InvalidHttpRequestBodyException.class, () -> {
            ProjectCreateRequest request = getTypicalCreateRequest(course.getId());
            request.setProjectName(null);

            getJsonResult(getAction(request, params));
        });

        ______TS("Add project with extra space (in middle and trailing)");

        createRequest = getTypicalCreateRequest(course.getId());
        createRequest.setProjectName("Name with extra  space ");

        a = getAction(createRequest, params);
        r = getJsonResult(a);

        assertEquals(HttpStatus.SC_OK, r.getStatusCode());
        response = (ProjectData) r.getOutput();

        assertEquals("Name with extra space", response.getProjectName());
    }

    private ProjectCreateRequest getTypicalCreateRequest(String courseId) {
        // Create Milestones
        ArrayList<Milestone> milestonesList = new ArrayList<Milestone>();

        // Using Calendar class to obtain 3 dates: today(), today() + 14 days, today() + 28 days
        Calendar c = Calendar.getInstance();

        // Add milestone 1
        Date date1 = new Date();
        Milestone testMilestone1 = new Milestone("Milestone 1", "Description for Milestone 1", date1);
        milestonesList.add(testMilestone1);

        // Add milestone 2
        c.setTime(date1);
        c.add(Calendar.DATE, 14);
        Date date2 = new Date();
        date2 = c.getTime();
        Milestone testMilestone2 = new Milestone("Milestone 2", "Description for Milestone 2", date2);
        milestonesList.add(testMilestone2);

        // Add milestone 3
        c.setTime(date1);
        c.add(Calendar.DATE, 28);
        Date date3 = new Date();
        date3 = c.getTime();
        Milestone testMilestone3 = new Milestone("Milestone 3", "Description for Milestone 2", date3);
        milestonesList.add(testMilestone3);

        // create student list
        ArrayList<String> mockStudentList = new ArrayList<String>(Arrays.asList("bob@gmail.com", "alice@gmail.com", "sally@gmail.com"));

        // Create ProjectCreateObject
        ProjectCreateRequest createRequest =
                new ProjectCreateRequest();
        createRequest.setProjectName("new project");
        createRequest.setCourseID(courseId);
        createRequest.setProjMilestones(milestonesList);
        createRequest.setStudentList(mockStudentList);

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
