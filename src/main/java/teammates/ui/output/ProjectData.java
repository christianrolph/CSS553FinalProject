package teammates.ui.output;

import teammates.common.datatransfer.attributes.ProjectAttributes;

import java.util.ArrayList;

/**
 * The API output format of {@link ProjectAttributes}.
 */
public class ProjectData extends ApiOutput {

    private final String projectName;
    private String courseID;
    private ArrayList<String> studentList;
    private ArrayList<Milestone> ms;

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
