package teammates.ui.request;

/**
 * The request body format for creation of feedback session.
 */
public class ProjectCreateRequest extends ProjectBasicRequest {
    private String projectName;

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    @Override
    public void validate() {
        super.validate();

        assertTrue(projectName != null, "Session name cannot be null");
        assertTrue(!projectName.isEmpty(), "Session name cannot be empty");
    }
}
