package teammates.storage.api;

import static com.googlecode.objectify.ObjectifyService.ofy;

import com.google.appengine.api.search.Results;
import com.google.appengine.api.search.ScoredDocument;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.cmd.Query;

import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import teammates.common.util.Logger;
import com.googlecode.objectify.cmd.LoadType;
import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.storage.entity.Course;
import teammates.storage.entity.Project;
import teammates.common.util.Assumption;
import teammates.common.util.JsonUtils;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;
import teammates.common.exception.EntityAlreadyExistsException;

/**
 *
 * Handles CRUD operations for projects
 *
 */
public class ProjectsDb extends EntitiesDb<Project, ProjectAttributes> {
    
    private static final Logger log = Logger.getLogger();

    @Override
    public ProjectAttributes createEntity(ProjectAttributes project)
        throws InvalidParametersException, EntityAlreadyExistsException {
        
	    ProjectAttributes createdProject = super.createEntity(project);
        log.info("Entity created: " + JsonUtils.toJson(createdProject));
	    return createdProject;
    }
    //getProject
    //updateProject
    //deleteProject


    // 
    @Override
    public boolean hasExistingEntities(ProjectAttributes entityToCreate)
    {
        // access to database ends here
        // further implementation exceeds project requirements and scope
        return false;
    }

    //
    @Override
    LoadType<Project> load() {
        return ofy().load().type(Project.class);
    }

    // 
    @Override
    ProjectAttributes makeAttributes(Project entity) {
        Assumption.assertNotNull(entity);

        return ProjectAttributes.valueOf(entity);
    }
      
}
