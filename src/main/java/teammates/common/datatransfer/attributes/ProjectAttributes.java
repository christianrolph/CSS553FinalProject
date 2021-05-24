package teammates.common.datatransfer.attributes;

import java.util.ArrayList;
import java.util.List;

import teammates.common.util.Assumption;
import teammates.logic.core.StudentsLogic;
import teammates.ui.output.Milestone;
import teammates.storage.entity.Project;

/**
 *
 * @author Tri27
 */
public class ProjectAttributes extends EntityAttributes<Project> {

    private String projectName;
    private Milestone projMilestone;
    private ArrayList<StudentsLogic> studentList;

    private ProjectAttributes(){
    }

    private ProjectAttributes(String projectName) {
        this.projectName = projectName;
    }

    private ProjectAttributes(String projectName, Milestone projMilestone, ArrayList<StudentsLogic> studentList) {
        this.projectName = projectName;
        this.projMilestone = projMilestone;
        this.studentList = studentList;
    }

    /**
     * Return a builder for {@link ProjectAttributes}
     * @param projectName name of the project
     * @return a builder
     */
    public static Builder builder(String projectName) {return new Builder(projectName);}

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

    public Milestone getProjMilestone() {
        return projMilestone;
    }

    public void setProjMilestone(Milestone projMilestone) {
        this.projMilestone = projMilestone;
    }

    public boolean checkMilestone(){
        return projMilestone.isIsFinished();
    }

    @Override
    public String toString(){
        return "projectName = " + this.projectName
                + ", milestones = " + this.projMilestone
                + ", studentList = " + this.studentList;
    }

    // TODO finish implementing method
    @Override
    public List<String> getInvalidityInfo() {
        return null;
    }

    // TODO finish implementing method
    @Override
    public Project toEntity() {
        return null;
    }

    // TODO finish implementing method
    @Override
    public void sanitizeForSaving() {
    }

    /**
     * Updates with {@link UpdateOptions}
     */
    public void update(UpdateOptions updateOptions) {
        updateOptions.milestoneOption.ifPresent(pM -> projMilestone = pM);
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

        private UpdateOption<Milestone> milestoneOption = UpdateOption.empty();
        private UpdateOption<ArrayList<StudentsLogic>> studentsListOption = UpdateOption.empty();

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
                    + ", milestones =" + milestoneOption
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

            public Builder withNewMilestone(Milestone ms) {
                Assumption.assertNotNull(ms);

                updateOptions.milestoneOption = UpdateOption.of(ms);
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

        public B withMilestone(Milestone ms) {
            Assumption.assertNotNull(ms);

            updateOptions.milestoneOption = UpdateOption.of(ms);
            return thisBuilder;
        }

        public B withStudentList(ArrayList<StudentsLogic> studentList) {
            Assumption.assertNotNull(studentList);

            updateOptions.studentsListOption = UpdateOption.of(studentList);
            return thisBuilder;
        }

        public abstract T build();
    }
}