package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.ui.output.Milestone;
import teammates.storage.entity.Project;
import teammates.common.util.*;
/**
 *
 * @author Tri27
 */
public class ProjectAttributes extends EntityAttributes<Project> {

    private String projectName;
    private String courseID;
    private ArrayList<String> studentList;
    private ArrayList<Milestone> projMilestones;

    private ProjectAttributes(String projectName) {
        this.projectName = projectName;
    }

    private ProjectAttributes(String projectName, ArrayList<Milestone> projMilestones, ArrayList<String> studentEmails) {
        this.projectName = projectName;
        this.projMilestones = projMilestones;
        this.studentList = studentEmails;
    }

    /**
     * Return a builder for {@link ProjectAttributes}
     * @param projectName name of the project
     * @return a builder
     */
    public static Builder builder(String projectName) {return new Builder(projectName);}

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

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }
    
    public ArrayList<Milestone> getProjMilestones() {
        return projMilestones;
    }

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
                + ", milestones = " + this.projMilestones
                + ", studentList = " + this.studentList;
    }

    // TODO finish implementing method
    @Override
    public List<String> getInvalidityInfo() {
        List<String> invalidityList = new ArrayList();
        if (this.getProjectName() == null){
            invalidityList.add("INVALID_PROJECT_NAME");
        }
        if (this.getStudentList().isEmpty()){
            invalidityList.add("INVALID_STUDENT_LIST");
        }
        if (this.getCourseID().equals(null) || this.getCourseID() == ""){
            invalidityList.add("INVALID_COURSE_ID");
        }
        if(invalidityList.isEmpty()){
            return invalidityList;
        }
        else{
            return null;
        }
    }

    // TODO finish implementing method
    @Override
    public Project toEntity() {
        return new Project(this.projectName,this.projMilestones,this.studentList);
    }

    // TODO finish implementing method
    @Override
    public void sanitizeForSaving() {
        this.projectName.trim();
        
    }

    /**
     * Updates with {@link UpdateOptions}
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.milestonesOption.ifPresent(pM -> projMilestones = pM);
        updateOptions.studentsListOption.ifPresent(sL -> studentList = sL);
    }

    /**
     * Returns a {@link UpdateOptions.Builder} to build {@link UpdateOptions} for a project.
     */
    public static UpdateOptions.Builder updateOptionsBuilder(String projectName) {
        return new UpdateOptions.Builder(projectName);
    }

    /**
     *  A builder class for {@link ProjectAttributes}.
     */
    public static class Builder extends BasicBuilder<ProjectAttributes, Builder>{
        private final ProjectAttributes projectAttributes;

        private Builder(String projectName) {
            super(new UpdateOptions(projectName));
            thisBuilder = this;

            projectAttributes = new ProjectAttributes(projectName);
        }

        @Override
        public ProjectAttributes build() {
            projectAttributes.update(updateOptions);

            return projectAttributes;
        }
    }


    /**
     * Helper class to specific the fields to update in {@link AccountAttributes}.
     */
    public static class UpdateOptions {
        private String projectName;

        private UpdateOption<ArrayList<Milestone>> milestonesOption = UpdateOption.empty();
        private UpdateOption<ArrayList<String>> studentsListOption = UpdateOption.empty();

        // Constructor requires non-null project name
        private UpdateOptions(String projectName) {
            Assumption.assertNotNull(projectName);
            this.projectName = projectName;
        }

        public String getProjectName() { return projectName; };

        @Override
        public String toString() {
            return "ProjectAttributes.UpdateOptions["
                    + "projectName =" + projectName
                    + ", milestones =" + milestonesOption
                    + ", studentList = " + studentsListOption
                    + "]";
        }

        /**
         * Builder class to build {@link UpdateOptions}.
         */
        public static class Builder extends BasicBuilder<UpdateOptions, Builder>{
            private Builder(String projectName){
                super(new UpdateOptions(projectName));
                thisBuilder = this;
            }

            public Builder withNewMilestone(ArrayList<Milestone> ms) {
                Assumption.assertNotNull(ms);

                updateOptions.milestonesOption = UpdateOption.of(ms);
                return thisBuilder;
            }

            @Override
            public UpdateOptions build() { return updateOptions; }
        }

    }

    /**
     * Basic builder to build {@link ProjectAttributes} related classes.
     *
     * @param <T> type to be built
     * @param <B> type of the builder
     */
    private abstract static class BasicBuilder<T, B extends BasicBuilder<T, B>>{
        UpdateOptions updateOptions;
        B thisBuilder;

        BasicBuilder(UpdateOptions updateOptions) {
            this.updateOptions = updateOptions;
        }

        public B withMilestone(ArrayList<Milestone> ms) {
            Assumption.assertNotNull(ms);

            updateOptions.milestonesOption = UpdateOption.of(ms);
            return thisBuilder;
        }

        public B withStudentList(ArrayList<String> studentList) {
            Assumption.assertNotNull(studentList);

            updateOptions.studentsListOption = UpdateOption.of(studentList);
            return thisBuilder;
        }

        public abstract T build();
    }
}