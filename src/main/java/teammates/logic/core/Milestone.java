package Main;

import java.util.Date;

/**
 *
 * @author Tri27
 */
public class Milestone {
    
    private String name;
    private String description;
    private Date date;
    private boolean isFinished = false;

    public Milestone(){
        
    }
    
    public Milestone(String name, String des, Date date){
        this.name = name;
        this.description = des;
        this.date = date;
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isIsFinished() {
        return isFinished;
    }

    public void setIsFinished(boolean isFinished) {
        this.isFinished = isFinished;
    }
    
    
}
