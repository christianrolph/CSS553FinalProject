
package teammates.storage.api;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Instant;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

//Import CSV/file database writer/reader

import teammates.common.datatransfer.attributes.ProjectAttributes;
import teammates.storage.entity.Project;
import teammates.common.util.Assumption;
import teammates.common.exception.EntityDoesNotExistException;
import teammates.common.exception.InvalidParametersException;

/**
 *
 * Handles CRUD operations for projects
 *
 */
public class ProjectsDb extends EntitiesDb<Project, ProjectAttributes> {
  //no parameters
  
  
  //createProject
    //see the EntitiesDb class for createEntity flow
    //createEntity calls out for the *Attributes class to call a method
    //.toEntity() which returns an entity E. If we reuse the super class
    //method, then we would need to ensure our attributes class covers
    //this functionality

  //getProject
  //updateProject
  //deleteProject
    
}
