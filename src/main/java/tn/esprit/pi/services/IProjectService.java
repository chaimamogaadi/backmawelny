package tn.esprit.pi.services;


import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PathVariable;
import tn.esprit.pi.entities.CategoryProject;
import tn.esprit.pi.entities.Project;
import tn.esprit.pi.entities.User;
import tn.esprit.pi.services.SMSService;

import java.util.List;
import java.util.Map;

public interface IProjectService {
  List<Project> retiveAllProjects();
  // void rateProject(Project project, Integer rating, String feedback, UserDetails userDetails);
  void rateProject(Project project, Integer rating, String feedback);
  Project retriveProjectById (Long idProject);
  Project addProject(Project project);
  //void removeProject(Long idProject);
  // Project modifyProject(Project project);

  Map<CategoryProject, Integer> getMostInvestedCategories();
  Project updateProject(Project project);
  //void deleteProject(@PathVariable Long projectId);
  //void deleteProject(Long idProject, User authentication);
 // void deleteProject(Long idProject);
  void deleteProject( Long projectId);

  long countProjects();
  List<Project> searchProjectsByNom(String nom);
  Float getAverageRating();
  Project validerProjet(Long idProject, SMSService smsService);
  List<Project> getProjectsSortedByInvestNeed();


}
