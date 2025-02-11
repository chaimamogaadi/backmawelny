package tn.esprit.pi.services;

import tn.esprit.pi.entities.Event;
import tn.esprit.pi.entities.ShareHolder;
import tn.esprit.pi.entities.User;

import java.util.List;

public interface EventService {
    List<Event> retrieveAllEvents();

    Event AddEvent(Event event, User authentication);
   // Event createEvent(Event event);
   void  addEvent(Event event, User authentication);

    void removeEvent(Integer numEvent, User authentication);
 Integer getEventIdByName(String eventName);

    Event retrieveEvent(Integer numEvent, User authentication);

    Event updateEvent(Event event,User authentication);
    Event getEventById(Integer EventId);
    void assignshrtoevent(Integer EventId, ShareHolder shareHolder, User authentication);

    List<Event> findByShareHolders_LastNameShareholderAndShareHolders_FirstNameShareholder(String lastNameShareholder, String FirstNameShareholder,User authentication);

    Double getTotalInvestmentInEvent(int eventId);

    List<Event> getEventsWithin24Hours();
     List<Event> getArchiveEvent();
    void voteLike(int eventId, int shareholderId);
    void voteDislike(int eventId, int shareholderId);
    double calculIndiceRentabilite(int idEvent);


    List<Event> historiqueEvent(int idUser);
     Event recommendEventByLikes();
    String getRecommendationResponse();

    Event findMostFrequentEvent();
    Event findLessFrequentEvent();
    List<Event> findEventsWithoutShareholders();
    Long countEventsWithAtLeastOneShareholder();
    double getEventsShareholdersPercentages();
    double getEventsShareholdersPercentages1();
    List<Event> getEventsInTwoDays();

}