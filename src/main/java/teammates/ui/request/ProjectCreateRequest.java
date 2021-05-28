package teammates.ui.request;

import teammates.common.datatransfer.attributes.AccountAttributes;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.common.util.JsonUtils;
import teammates.ui.output.Milestone;
import teammates.logic.core.StudentsLogic;

import java.util.ArrayList;

/**
 * The request body format for creation of Project.
 */
public class ProjectCreateRequest extends BasicRequest {
    private String projectName;
    private String courseID;
    private ArrayList<Milestone> projMilestones;
    private ArrayList<String> studentList;

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public String getCourseID() {
        return courseID;
    }
    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public ArrayList<Milestone> getProjMilestones() {
        return projMilestones;
    }
    public void setProjMilestones(ArrayList<Milestone> projMilestones) {
        this.projMilestones = projMilestones;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }
    public void setStudentList(ArrayList<String> studentslist) {
        this.studentList = studentslist;
    }

    public String toString() {
        return '\n' +
                "Project Name:   " + this.getProjectName() + '\n' +
                "Course ID:     " + this.getCourseID() + '\n' +
                "Milestone:     " + getProjMilestones().toString() + '\n' +
                "Student Names: " + getStudentList().toString() + '\n';
    }

    @Override
    public void validate() {
        assertTrue(projectName != null, "Project name cannot be null");
        assertTrue(courseID != null, "Course ID cannot be null");
        assertTrue(projMilestones != null, "Milestone cannot be null");
        assertTrue(studentList != null, "Student list cannot be empty");
    }
}
