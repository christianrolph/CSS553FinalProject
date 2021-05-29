package teammates.storage.entity;

import java.time.Instant;

import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;
import com.googlecode.objectify.annotation.Translate;
import java.util.ArrayList;

import teammates.common.util.Const;
import teammates.ui.output.Milestone;

/**
 *
 * Project entity definition
 */
public class Project extends BaseEntity {
    //variables
    private String projectName;
    
    private ArrayList<Milestone> projMilestones;
    
    private ArrayList<String> studentEmails;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public ArrayList<Milestone> getProjMilestones() {
        return projMilestones;
    }

    public void setProjMilestones(ArrayList<Milestone> projMilestones) {
        this.projMilestones = projMilestones;
    }

    public ArrayList<String> getStudentEmails() {
        return studentEmails;
    }

    public void setStudentEmails(ArrayList<String> studentEmails) {
        this.studentEmails = studentEmails;
    }
  
    public Project(String projectName, ArrayList<Milestone> projMilestones, ArrayList<String> studentEmails){{
        this.setProjectName(projectName);
        this.setProjMilestones(projMilestones);
        this.setStudentEmails(studentEmails);
    }
        
    }
    //constructor
    //getters
    //setters
    //copy paste of ProjectAttributes?
}
