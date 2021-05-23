package Main;

import java.util.ArrayList;

/**
 *
 * @author Tri27
 */
public class ProjectAttributes {

    private String teamName; 
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
    
    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public Milestone getMs() {
        return ms;
    }

    public void setMs(Milestone ms) {
        this.ms = ms;
    }
    
    public boolean checkMilstone(){
        return ms.isIsFinished();
    }
}
