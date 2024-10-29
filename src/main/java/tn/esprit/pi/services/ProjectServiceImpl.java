package tn.esprit.pi.services;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.pi.repositories.IInvestmentRepository;
import tn.esprit.pi.repositories.ProjectRepository;
import tn.esprit.pi.repositories.UserRepository;
import tn.esprit.pi.entities.*;
import java.util.*;
import java.util.stream.Collectors;
import jakarta.persistence.*;

@Service
@AllArgsConstructor
public class ProjectServiceImpl implements  IProjectService{
  @PersistenceContext
  private EntityManager entityManager;
  ProjectRepository projectRepository;
  UserRepository userRepository;
  IInvestmentRepository investmentRepository;
  UserServiceImp userService;

  SMSService smsService;

  public List<Project> retiveAllProjects() {
    return projectRepository.findAll();
  }


  public Project retriveProjectById(Long idProject) {
    return projectRepository.findById(idProject).get();
  }


  public Project addProject(Project project) {
    project.setStatus_Invest(Status_inv.CLOSED);
    project.setStatus_project(statusProject.Still_Not_Approved);
    // Get the current user's ID from security context
    String currentUserId = getCurrentUserIdFromSecurityContext();

    // Find the user
    User user = userRepository.findUserByEmail(currentUserId)
      .orElseThrow(() -> new IllegalArgumentException("Current user not found"));

    // Save the project
    projectRepository.save(project);

    // Add the project to the user's collection
    user.getProject().add(project);
    userRepository.save(user);
    return project;
  }
  public void deleteProject(Long projectId) {
    Project existingProject = projectRepository.findById(projectId)
      .orElseThrow(() -> new IllegalArgumentException("Project not found"));

    // Remove references to the project from user_project table
    List<User> users = userRepository.findByProject(existingProject);
    for (User user : users) {
      user.getProject().remove(existingProject);
      userRepository.save(user);
    }

    // Now you can safely delete the project
    projectRepository.delete(existingProject);
  }
 /* public void deleteProject(Long idProject) {
    // Role userRole = getCurrentUserRole(authentication);
   if (!userRole.equals(Role.CUSTOMER)) {
      throw new AccessDeniedException("You're not authorized");
    } else {
    projectRepository.deleteById(idProject);

  }*/
  private String getCurrentUserIdFromSecurityContext() {
    // Get the current authentication object from the security context
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

    // Check if the authentication object is not null and if the principal is an instance of UserDetails
    if (authentication != null && authentication.getPrincipal() instanceof UserDetails) {
      // Cast the principal to UserDetails
      UserDetails userDetails = (UserDetails) authentication.getPrincipal();

      // Return the username, which could represent the user's ID
      return userDetails.getUsername();
    } else {
      // If the authentication object is null or the principal is not an instance of UserDetails,
      return null;
    }
  }


  /*@Secured({"ADMIN", "AGENT"})
  public void removeProject(Long idProject) {
    projectRepository.deleteById(idProject);
  }*/
 /* @Secured({"ADMIN", "AGENT"})
  public Project modifyProject(Project project) {
    return null;
  }*/

/* public Project validerProjet(Long idProject, User user) {
    Project project = projectRepository.findById(idProject)
      .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

    // Check if the user is an admin
    if (!User_role.ADMIN.equals(user.getRole())) {
      throw new AccessDeniedException("Vous n'êtes pas autorisé à valider ce projet");
    }

    // Mettre à jour le statut du projet
    project.setStatus_project(statusProject.Approved);

    // Retrieve the user's phone number associated with the project
    String recipientPhoneNumber = retrieveUserPhoneNumberForProject(project);

    // Send SMS notification to the customer
    String message = "Your project has been approved. Congratulations!";
    SmsSender.sendSms(recipientPhoneNumber, message);

    return projectRepository.save(project);
  }*/

  /*public String retrieveUserPhoneNumberForProject(Project project) {
    User projectUser = userRepository.findByProject(project);
    return projectUser != null ? projectUser.getUser_phone() : null;
  }*/

  public Map<CategoryProject, Integer> getMostInvestedCategories() {
    // Retrieve all projects
    List<Project> projects = projectRepository.findAll();

    // Create a map to store category investments
    Map<CategoryProject, Integer> categoryInvestments = new HashMap<>();

    // Iterate through each project and update category investments
    for (Project project : projects) {
      CategoryProject category = project.getCategoryProject();
      int investmentsCount = investmentRepository.countByProject(project);
      categoryInvestments.put(category, categoryInvestments.getOrDefault(category, 0) + investmentsCount);
    }

    // Sort the category investments by value in descending order
    List<Map.Entry<CategoryProject, Integer>> sortedCategoryInvestments = categoryInvestments.entrySet().stream()
      .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
      .collect(Collectors.toList());

    // Create a LinkedHashMap to preserve the order of insertion
    Map<CategoryProject, Integer> sortedCategoryInvestmentsMap = new LinkedHashMap<>();
    for (Map.Entry<CategoryProject, Integer> entry : sortedCategoryInvestments) {
      sortedCategoryInvestmentsMap.put(entry.getKey(), entry.getValue());
    }

    // Return the sorted category investments map
    return sortedCategoryInvestmentsMap;
  }

  public Project updateProject(Project project) {
   /* Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = (User) authentication.getPrincipal();
    User_role userRole = getCurrentUserRole(currentUser);

    if (!(Arrays.asList(User_role.AGENT, User_role.ADMIN, User_role.CUSTOMER).contains(userRole))) {
      throw new AccessDeniedException("You're not authorized to modify a project");
    }*/

    // Retrieve the existing project from the database
    Project existingProject = projectRepository.findById(project.getIdProject()).get();

    // Check if the current user has permission to update this project
   /* if (userRole == User_role.CUSTOMER && !existingProject.getStatus_project().equals(statusProject.Still_Not_Approved)) {
      throw new AccessDeniedException("Customers can only update projects with status 'STILL_NOT_Approved'");
    }*/

    // Update the project fields
    existingProject.setNomProject(project.getNomProject());
    existingProject.setDescription(project.getDescription());
    existingProject.setAmount_inv(project.getAmount_inv());
    existingProject.setDate_debut(project.getDate_debut());
    existingProject.setDate_fin(project.getDate_fin());
    // Update other fields as needed...

    // Save the updated project to the database
    Project updatedProject = projectRepository.save(existingProject);

    return updatedProject;
  }

  private Role getCurrentUserRole(User currentUser) {

    return currentUser.getRole();
  }



  // Modify the method signature to accept user ID instead of User object

  public Project validerProjet(Long idProject, SMSService smsService) {
  /*  Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User currentUser = (User) authentication.getPrincipal();
    User_role userRole = getCurrentUserRole(currentUser);

    if (!(Arrays.asList(User_role.AGENT, User_role.ADMIN).contains(userRole))) {
      throw new AccessDeniedException("You're not authorized to delete a project");
    }/*/
    Project project = projectRepository.findById(idProject)
      .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

    // Mettre à jour le statut du projet
    project.setStatus_project(statusProject.Approved);


    // Retrieve the user ID associated with the project from the association table
    Integer id_user = Math.toIntExact(getUserIdByProjectId(idProject)); // Implement this method to retrieve the user ID based on the project ID

    // Retrieve the user using the user ID
    User customer = userService.getUserByid(id_user);

    if (customer == null || customer.getUser_phone() == null || customer.getUser_phone().isEmpty()) {
      throw new IllegalArgumentException("Customer's phone number is missing or invalid");
    }
    String customerPhoneNumber = customer.getUser_phone();
    String message = "Your project has been approved!";

    // Debugging: Print customer's phone number and message
    System.out.println("Customer Phone Number: " + customerPhoneNumber);
    System.out.println("Message: " + message);

    // Send SMS
    smsService.sendSms(customerPhoneNumber, message);

    return projectRepository.save(project);
  }

  // Method to retrieve the user ID associated with a project ID using a native SQL query
  public Long getUserIdByProjectId(Long idProject) {
    Query query = entityManager.createNativeQuery("SELECT user_id FROM user_project WHERE project_id_project = :projectId");
    query.setParameter("projectId", idProject);
    Object result = query.getSingleResult();
    if (result != null) {
      return ((Integer) result).longValue();
    }
    return null;
  }


  public List<Project> getProjectsSortedByInvestNeed() {
    // Fetch projects from the repository sorted by invest_need in ascending order
    List<Project> projects = projectRepository.findAllByOrderByInvestNeedAsc();
    return projects;
  }

  /*public void rateProject(Project project, Integer rating, String feedback, UserDetails userDetails) {
    // Check if the user has the "agent" role
    if (userDetails.getAuthorities().contains(new SimpleGrantedAuthority("AGENT"))) {
      // Set the rating and feedback
      project.setRating(rating);
      project.setFeedback(feedback);
      // Save the project
      projectRepository.save(project);
    } else {
      throw new AccessDeniedException("You do not have permission to rate this project.");
    }
  }*/
  public void rateProject(Project project, Integer rating, String feedback) {
    // Check if the user has the "agent" role

    // Set the rating and feedback
    project.setRating(rating);
    project.setFeedback(feedback);
    // Save the project
    projectRepository.save(project);

  }

  // Method to count all projects

  public long countProjects() {
    return projectRepository.count();
  }

  public List<Project> searchProjectsByNom(String nom) {
    return projectRepository.findByNomProjectContainingIgnoreCase(nom);
  }

  public Float getAverageRating() {
    return projectRepository.calculateAverageRating();
  }


}




