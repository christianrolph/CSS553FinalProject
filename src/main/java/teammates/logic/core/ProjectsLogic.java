package teammates.logic.core;

import teammates.common.exception.EntityAlreadyExistsException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.storage.api.ProjectsDb;

/*
 *This is a sub class to the Logic class series. This handles the logic of transferring the front end input data
 * through the backend into the system database
 */

public final class ProjectsLogic {
    private static ProjectsLogic instance = new ProjectsLogic();
    private static final ProjectsDb projDb = new ProjectsDb();
    
    private ProjectsLogic(){
    	// prevent initialization
    }
    //inst()
    public static ProjectsLogic inst() {
        return instance;
    }
    
    //createProject method
    //definition: creates a project
    //parameters: projectToAdd: ProjectAttributes
    //returns: ProjectAttributes
    //throws InvalidParametersException if the project is not valid
    //throws EntityAlreadyExistsException if the project already exists
    public ProjectAttributes createProject(ProjectAttributes projectToAdd)
            throws InvalidParametersException, EntityAlreadyExistsException {
        return projDb.createEntity(projectToAdd);
        
    }   
}
