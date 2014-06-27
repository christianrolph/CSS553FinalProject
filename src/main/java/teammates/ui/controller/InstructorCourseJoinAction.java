package teammates.ui.controller;

import teammates.common.datatransfer.InstructorAttributes;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.util.Assumption;
import teammates.common.util.Const;
import teammates.common.util.Url;
import teammates.logic.api.GateKeeper;

/**
 * This action handles instructors that attempts to join a course.
 * It asks the instructor for confirmation that the logged in account 
 * belongs to him before redirecting him to the actual join action, 
 * {@link InstructorCourseJoinAuthenticatedAction}.
 * <br/><br/>
 * This is done to prevent instructor from accidentally linking 
 * his registration key with another instructor's google account.
 */
public class InstructorCourseJoinAction extends Action {
    
    private InstructorCourseJoinConfirmationPageData pageData;
    
    @Override
    public ActionResult execute() throws EntityDoesNotExistException {
        
        String key = getRequestParamValue(Const.ParamsNames.REGKEY);
        String institute = getRequestParamValue(Const.ParamsNames.INSTRUCTOR_INSTITUTION);
        
        Assumption.assertNotNull(key);

        new GateKeeper().verifyLoggedInUserPrivileges();
        
        /* Process confirmation for instructor if needed and setup status to be shown to admin */
        statusToAdmin = "Action Instructor Clicked Join Link"
                        + "<br/>Google ID: " + account.googleId
                        + "<br/>Key: " + key;
        
        InstructorAttributes instructor = logic.getInstructorForRegistrationKey(key);

        if (instructor != null && instructor.isRegistered()) {
            // Bypass confirmation if instructor is already registered
            String redirectUrl = Url.addParamToUrl(Const.ActionURIs.INSTRUCTOR_COURSE_JOIN_AUTHENTICATED,
                                                   Const.ParamsNames.REGKEY, key);
            
            //for the link of instructor added by admin, an additional parameter institute is needed  
            //so it must be passed to instructorCourseJoinAuthenticated action
            if (institute != null) {
                redirectUrl = Url.addParamToUrl(redirectUrl, Const.ParamsNames.INSTRUCTOR_INSTITUTION, institute);
            }
            
            return createRedirectResult(redirectUrl);
        } 
        pageData = new InstructorCourseJoinConfirmationPageData(account);
        pageData.regkey = key;
        
        
        //1.For instructors added by admin, institute is passed from the join link and should be passed
        //to the confirmation page and later to authenticated action for account creation
        //2.For instructors added by other instructors, institute is not passed from the link so the value 
        //will be null
        
        pageData.institute = institute;
        
        return createShowPageResult(Const.ViewURIs.INSTRUCTOR_COURSE_JOIN_CONFIRMATION, pageData);
    }
}
