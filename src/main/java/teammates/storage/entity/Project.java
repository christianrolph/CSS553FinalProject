package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;
import teammates.ui.output.Milestone;
import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Const;

/**
 *
 * Project entity definition
 */
@Entity
public class Project extends BaseEntity {
    //variables
  
    
    private String projectName;
    private String courseID;
    private ArrayList<String> studentList;
    private ArrayList<Milestone> projMilestones;

    @SuppressWarnings("unused")
    private Project(){
        // required by Objectify
    }

    private Project(String projectName) {
        this.projectName = projectName;
    }

    private Project(String projectName, Milestone projMilestone, ArrayList<String> studentEmails) {
        this.projectName = projectName;
        this.projMilestones.add(projMilestone);
        this.studentList = studentEmails;
    }

    /**
     * Generates an unique ID for the student.
     */
    public static String generateId(String projectName, String courseId) {
        return projectName + '%' + courseId;
    }



    public boolean assignStudent(String studentEmail){
        studentList.add(studentEmail);
        return true;
    }

    public boolean removeStudent(String studentEmail){
        studentList.remove(studentEmail);
        return true;
    }

    public ArrayList<String> getStudentList() {
        return studentList;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<Milestone> getProjMilestones() {
        return projMilestones;
    }

    public String getCourseId() { return this.courseID; }

    public void setCourseId(String courseID) { this.courseID = courseID; }

    public void addProjMilestone(Milestone projMilestone) {
        projMilestones.add(projMilestone);
    }
    
    public void removeProjMilestone(Milestone projMilestone) {
        projMilestones.remove(projMilestone);
    }

    public boolean checkMilestone(Milestone projMilestone){
        return projMilestone.isIsFinished();
    }

    @Override
    public String toString(){
        return "projectName = " + this.projectName
                + ", courseID = " + this.courseID
                + ", milestones = " + this.projMilestones
                + ", studentList = " + this.studentList;
    }

    

    


    
    
}
