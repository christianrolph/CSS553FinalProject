package teammates.ui.output;

import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.logic.core.StudentsLogic;

import java.util.ArrayList;
import teammates.common.util.Const;

import javax.annotation.Nullable;
import java.time.Instant;

/**
 * The API output format of {@link ProjectAttributes}.
 */
public class ProjectData extends ApiOutput {

    private final String projectName;
    private final Milestone ms;
    private final ArrayList<StudentsLogic> studentList;

    private InstructorPrivilegeData privileges;

    public ProjectData(ProjectAttributes projectAttributes){
        this.projectName = projectAttributes.getProjectName();
        this.ms = projectAttributes.getMs();
        this.studentList = projectAttributes.getStudentList();
    }

    public String getProjectName() {
        return projectName;
    }

    public Milestone getMS() {
        return ms;
    }

    public ArrayList<StudentsLogic> getStudentList() {
        return studentList;
    }

    public void setPrivileges(InstructorPrivilegeData privileges) {
        this.privileges = privileges;
    }







}
