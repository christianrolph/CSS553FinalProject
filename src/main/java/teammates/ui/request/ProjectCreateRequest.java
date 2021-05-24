package teammates.ui.request;

import teammates.ui.output.Milestone;
import teammates.logic.core.StudentsLogic;

import java.util.ArrayList;

/**
 * The request body format for creation of Project.
 */
public class ProjectCreateRequest extends BasicRequest {
    private String projectName;
    private Milestone ms;
    private ArrayList<StudentsLogic> studentList;

    public String getProjectName() {
        return projectName;
    }
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Milestone getMs() {
        return ms;
    }
    public void setMs(Milestone milestone) {
        this.ms = milestone;
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
        assertTrue(ms != null, "Milestone cannot be null");
        assertTrue(studentList != null, "Student list cannot be empty");
    }
}
