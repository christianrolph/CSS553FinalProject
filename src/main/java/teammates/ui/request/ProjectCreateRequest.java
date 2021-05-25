package teammates.ui.request;

import teammates.ui.output.Milestone;
import teammates.logic.core.StudentsLogic;

import java.util.ArrayList;

/**
 * The request body format for creation of Project.
 */
public class ProjectCreateRequest extends BasicRequest {
    private String projectName;
    private Milestone milestone;
    private ArrayList<StudentsLogic> studentList;

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Milestone getMilestone() {
        return milestone;
    }
    public void setMilestone(Milestone milestone) {
        this.milestone = milestone;
    }

    public ArrayList<StudentsLogic> getStudentList() {
        return studentList;
    }
    public void setStudentList(ArrayList<StudentsLogic> studentslist) {
        this.studentList = studentslist;
    }



    @Override
    public void validate() {
        assertTrue(projectName != null, "Project name cannot be null");
        assertTrue(milestone != null, "Milestone cannot be null");
        assertTrue(studentList != null, "Student list cannot be empty");
    }
}
