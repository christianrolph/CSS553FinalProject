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
    private ArrayList<Milestone> milestones;
    private InstructorPrivilegeData privileges;

    public ProjectData(ProjectAttributes projectAttributes){
        this.projectName = projectAttributes.getProjectName();
        this.courseID = projectAttributes.getCourseId();
        this.studentList = projectAttributes.getStudentList();
        this.milestones = projectAttributes.getProjMilestones();
    }

    public String getProjectName() {
        return projectName;
    }

    public String getCourseID(){ return courseID; }

    public ArrayList<Milestone> getMilestones() {
        return milestones;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public void setPrivileges(InstructorPrivilegeData privileges) {
        this.privileges = privileges;
    }

    public String toString(){
        return '\n' +
                "Project Name:   " + this.getProjectName() + '\n' +
                "Course ID:     " + this.getCourseID() + '\n' +
                "Milestone:     " + getMilestones().toString() + '\n' +
                "Student Names: " + getStudentList().toString() + '\n';

    }
}
