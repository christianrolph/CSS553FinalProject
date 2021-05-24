package teammates.common.datatransfer.attributes;

import java.util.ArrayList;

import teammates.logic.core.StudentsLogic;
import teammates.ui.output.Milestone;

/**
 *
 * @author Tri27
 */
public class ProjectAttributes {

    private String projectName;
    private Milestone ms;
    private ArrayList<StudentsLogic> studentList;

    public ProjectAttributes(){

    }

    public boolean assignStudent(StudentsLogic student){
        studentList.add(student);
        return true;
    }

    public boolean removeStudent(StudentsLogic student){
        studentList.remove(student);
        return true;
    }

    public ArrayList<StudentsLogic> getStudentList() {
        return studentList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String teamName) {
        this.projectName = projectName;
    }

    public Milestone getMs() {
        return ms;
    }

    public void setMs(Milestone ms) {
        this.ms = ms;
    }

    public boolean checkMilestone(){
        return ms.isIsFinished();
    }
}