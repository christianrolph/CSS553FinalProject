package teammates.ui.output;

import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.logic.core.StudentsLogic;

import java.util.ArrayList;

/**
 * The API output format of {@link ProjectAttributes}.
 */
public class ProjectData extends ApiOutput {

    private final String projectName;
    private final ArrayList<Milestone> ms;
    private final ArrayList<String> studentList;

    private InstructorPrivilegeData privileges;

    public ProjectData(ProjectAttributes projectAttributes){
        this.projectName = projectAttributes.getProjectName();
        this.ms = projectAttributes.getProjMilestones();
        this.studentList = projectAttributes.getStudentList();
    }

    public String getProjectName() {
        return projectName;
    }

    public ArrayList<Milestone> getMS() {
        return ms;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public void setPrivileges(InstructorPrivilegeData privileges) {
        this.privileges = privileges;
    }







}
