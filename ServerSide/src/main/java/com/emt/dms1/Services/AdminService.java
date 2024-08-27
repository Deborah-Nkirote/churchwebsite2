package com.emt.dms1.Services;


import com.emt.dms1.Models.*;
import com.emt.dms1.Repository.*;
import com.emt.dms1.utils.EntityResponse;

import java.nio.file.StandardCopyOption;
import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.multipart.MultipartFile;

import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;


@Slf4j
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private UserInterestRepository userInterestRepository;
    @Autowired
    private SermonRepository sermonRepository;
    private EventRepository eventRepository;
    @Autowired
    private ChurchLeadersRepository churchLeadersRepository;
    @Autowired
    private WelcomeRepo welcomeRepo;
    private final AtomicReference<String> liveStreamUrl = new AtomicReference<>();


    public AdminService(EventRepository eventRepository, UserInterestRepository userInterestRepository, ChurchLeadersRepository churchLeadersRepository) {
        this.eventRepository = eventRepository;
        this.userInterestRepository = userInterestRepository;
        this.churchLeadersRepository = churchLeadersRepository;
    }

    public EntityResponse postWelcomeMessage(String message) {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Posting or updating welcome message...");

        try {
            // Check if a welcome message already exists
            Optional<WelcomeMessageRequest> existingMessage = welcomeRepo.findById(1L); // Assuming ID 1 is used for the welcome message

            if (existingMessage.isPresent()) {
                // Update the existing welcome message
                WelcomeMessageRequest messageToUpdate = existingMessage.get();
                messageToUpdate.setMessage(message);

                // Save the updated welcome message
                welcomeRepo.save(messageToUpdate);

                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Welcome message updated successfully.");
                entityResponse.setData(messageToUpdate);
                log.info("Welcome message updated successfully.");
            } else {
                // Create a new welcome message
                WelcomeMessageRequest newMessage = new WelcomeMessageRequest();
                newMessage.setId(1L); // Assuming ID 1 is used for the welcome message
                newMessage.setMessage(message);

                // Save the new welcome message
                welcomeRepo.save(newMessage);

                entityResponse.setStatusCode(HttpStatus.CREATED.value());
                entityResponse.setMessage("Welcome message posted successfully.");
                entityResponse.setData(newMessage);
                log.info("Welcome message posted successfully.");
            }
        } catch (Exception e) {
            log.error("Failed to post or update welcome message.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to post or update welcome message.");
        }

        return entityResponse;
    }

    public EntityResponse getWelcomeMessage() {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Fetching welcome message...");

        try {
            // Assuming ID 1 is used for the welcome message
            Optional<WelcomeMessageRequest> welcomeMessageRequest = welcomeRepo.findById(1L);

            if (welcomeMessageRequest.isPresent()) {
                String message = welcomeMessageRequest.get().getMessage();
                entityResponse.setData(message);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Welcome message fetched successfully.");
                log.info("Welcome message fetched successfully: {}", message);
            } else {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No welcome message found.");
                log.info("No welcome message found.");
            }
        } catch (Exception e) {
            log.error("Failed to fetch welcome message.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to fetch welcome message.");
        }

        return entityResponse;
    }


    public EntityResponse updateWelcomeMessage(String message) {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Fetching welcome message...");

        // Fetch the first available welcome message (assuming there's only one)
        Optional<WelcomeMessageRequest> messageOptional = welcomeRepo.findAll().stream().findFirst();

        log.info("Updating welcome message...");
        try {
            if (messageOptional.isPresent()) {
                WelcomeMessageRequest existingMessage = messageOptional.get();
                log.info("Found welcome message: {}", existingMessage.getMessage());

                // Update the message
                existingMessage.setMessage(message);
                log.info("Setting new welcome message: {}", message);

                // Save the updated message
                welcomeRepo.save(existingMessage);
                log.info("Welcome message saved: {}", existingMessage.getMessage());

                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Welcome message updated successfully.");
                entityResponse.setData(existingMessage);

                log.info("Welcome message updated successfully.");
            } else {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No welcome message found to update.");
                log.info("No welcome message found to update.");
            }
        } catch (Exception e) {
            log.error("Failed to update welcome message.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to update welcome message.");
        }

        return entityResponse;
    }


    public EntityResponse<Events> saveOrUpdateEvent(String event, MultipartFile imageFile, LocalDate date) {
        EntityResponse<Events> entityResponse = new EntityResponse<>();
        log.info("Saving or updating event...");

        try {
            if (event == null || event.isEmpty() || date == null) {
                throw new IllegalArgumentException("Event name or date cannot be null or empty");
            }

            if (date.isBefore(LocalDate.now())) {
                throw new IllegalArgumentException("Event date cannot be in the past.");
            }

            // Save event details to the database
            Events eventEntity = new Events();
            eventEntity.setName(event);
            eventEntity.setDate(date);

            if (imageFile != null && !imageFile.isEmpty()) {
                // Convert image file to byte array and store in the database
                byte[] imageData = imageFile.getBytes();
                eventEntity.setImageData(imageData);
                String imageType = imageFile.getContentType();
                eventEntity.setImagetype(imageType);
            }

            eventRepository.save(eventEntity);

            entityResponse.setStatusCode(HttpStatus.CREATED.value());
            entityResponse.setMessage("Event saved successfully.");
            entityResponse.setData(eventEntity);
            log.info("Event saved successfully.");

        } catch (IOException e) {
            log.error("Failed to process image file.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to process image file.");
        } catch (IllegalArgumentException e) {
            log.error("Invalid input.", e);
            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            entityResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save or update event.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to save or update event.");
        }

        return entityResponse;
    }


    public EntityResponse getUpcomingEvents() {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Fetching upcoming events...");

        try {
            // Fetch upcoming events
            List<Events> upcomingEvents = eventRepository.findAll(); // Ensure Event class is used here

            if (!upcomingEvents.isEmpty()) {
                // Convert image data from byte[] to Base64 string for front-end compatibility
                List<Map<String, Object>> eventsWithImages = upcomingEvents.stream()
                        .map(event -> {
                            Map<String, Object> eventData = new HashMap<>();
                            eventData.put("name", event.getName());
                            eventData.put("date", event.getDate());
                            if (event.getImageData() != null) {
                                eventData.put("imageData", event.getImageData());
                                eventData.put("imageType", event.getImagetype());
                            }
                            return eventData;
                        })
                        .collect(Collectors.toList());

                entityResponse.setData(eventsWithImages);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Upcoming events fetched successfully.");
                log.info("Upcoming events fetched successfully.");
            } else {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No upcoming events found.");
                log.info("No upcoming events found.");
            }
        } catch (Exception e) {
            log.error("Failed to fetch upcoming events.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to fetch upcoming events.");
        }

        return entityResponse;
    }

    public EntityResponse deleteEventById(Long eventId) {
        EntityResponse entityResponse = new EntityResponse<>();
        log.info("Deleting event with ID: {}", eventId);

        try {
            Optional<Events> eventOptional = eventRepository.findById(eventId);

            if (eventOptional.isPresent()) {
                eventRepository.deleteById(eventId);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Event deleted successfully.");
                log.info("Event deleted successfully with ID: {}", eventId);
            } else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("Event not found.");
                log.info("Event not found with ID: {}", eventId);
            }
        } catch (Exception e) {
            log.error("Failed to delete event with ID: {}", eventId, e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to delete event.");
        }

        return entityResponse;
    }


    public EntityResponse putLiveStreamUrl(String url) {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Putting live stream URL: {}", url);

        try {
            // Validate the YouTube URL
            if (url == null || !url.matches("^https://www\\.youtube\\.com/watch\\?v=.*")) {
                entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                entityResponse.setMessage("Invalid YouTube URL.");
                log.warn("Invalid YouTube URL: {}", url);
                return entityResponse;
            }

            // Save the URL (in this case, in-memory)
            liveStreamUrl.set(url);

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Live stream URL updated successfully.");
            entityResponse.setData(url);
            log.info("Live stream URL updated successfully: {}", url);
        } catch (Exception e) {
            log.error("Failed to update live stream URL.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to update live stream URL.");
        }

        return entityResponse;
    }

    public EntityResponse getLiveStreamUrl() {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Fetching live stream URL...");

        try {
            // Retrieve the URL (from in-memory storage)
            String url = liveStreamUrl.get();

            if (url == null || url.isEmpty()) {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No live stream URL found.");
                log.info("No live stream URL found.");
            } else {
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Live stream URL fetched successfully.");
                entityResponse.setData(url);
                log.info("Live stream URL fetched successfully: {}", url);
            }
        } catch (Exception e) {
            log.error("Failed to fetch live stream URL.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to fetch live stream URL.");
        }

        return entityResponse;
    }


    public EntityResponse uploadSermon(String title, String videoUrl, MultipartFile notesFile) {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Uploading sermon: {}", title);

        try {
            Sermons sermon = new Sermons();
            sermon.setTitle(title);
            sermon.setVideoUrl(videoUrl);

            if (notesFile != null && !notesFile.isEmpty()) {
                sermon.setNotesFile(notesFile.getBytes());
            }

            sermonRepository.save(sermon);

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Sermon uploaded successfully.");
            log.info("Sermon uploaded successfully: {}", title);
        } catch (Exception e) {
            log.error("Failed to upload sermon.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to upload sermon.");
        }


        return entityResponse;
    }

    public EntityResponse getSermons() {
        EntityResponse entityResponse = new EntityResponse();
        log.info("Fetching sermons...");

        try {
            List<Sermons> sermons = sermonRepository.findAll();

            if (!sermons.isEmpty()) {
                entityResponse.setData(sermons);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Sermons fetched successfully.");
                log.info("Sermons fetched successfully.");
            } else {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No sermons found.");
                log.info("No sermons found.");
            }
        } catch (Exception e) {
            log.error("Failed to fetch sermons.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to fetch sermons.");
        }

        return entityResponse;
    }

    public EntityResponse<UserInterest> postUserInterest(String name, long phoneNumber, String Interest) {
        EntityResponse<UserInterest> entityResponse = new EntityResponse<>();
        log.info("Updating user interests");

        try {
            UserInterest userInterest = new UserInterest();
            userInterest.setName(name);
            userInterest.setPhoneNumber(phoneNumber);
            userInterest.setInterest(Interest);

            // Save the user interest using the repository
            userInterestRepository.save(userInterest);

            // Set the response details
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("User interest set successfully.");
            log.info("User interest saved successfully");

        } catch (Exception e) {
            log.error("Failed to set user interest", e);

            // Handle the error in the response
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to set user interests");
        }

        // Return the response entity
        return entityResponse;
    }

    public EntityResponse<List<UserInterest>> getUserInterest() {
        EntityResponse<List<UserInterest>> entityResponse = new EntityResponse<>();
        log.info("Getting user interests");

        try {
            // Retrieve all user interests from the repository
            List<UserInterest> userInterests = userInterestRepository.findAll();

            if (!userInterests.isEmpty()) {
                // If the list is not empty, set the data and a success status
                entityResponse.setData(userInterests);
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("User interests retrieved successfully");
            } else {
                // If the list is empty, return a no content status
                log.info("No user interests found");
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No user interests found");
            }
        } catch (Exception e) {
            // Handle any exceptions that occur
            log.error("Error retrieving user interests", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Error retrieving user interests: " + e.getMessage());
        }

        return entityResponse;
    }

    public EntityResponse<churchLeaders> postChurchLeaders(String Name, String Designation, String BioData, MultipartFile image, long PhoneNo) {
        EntityResponse<churchLeaders> entityResponse = new EntityResponse<>();
        log.info("Adding church leader");

        try {
            churchLeaders CL = new churchLeaders();
            CL.setName(Name);
            CL.setPhoneNo(PhoneNo);
            CL.setBioData(BioData);
            CL.setDesignation(Designation);

            byte[] imageData = image.getBytes();
            CL.setImageData(imageData);

            String imageType = image.getContentType();
            CL.setImagetype(imageType);

            // Save the leader
            churchLeadersRepository.save(CL);
            log.info("Leader added successfully");

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Leader added successfully");
        } catch (Exception e) {
            log.error("Failed to add leader", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to add leader");
        }

        return entityResponse;
    }

    public EntityResponse<List<Map<String, Object>>> getChurchLeaders() {
        EntityResponse<List<Map<String, Object>>> entityResponse = new EntityResponse<>();
        try {
            List<churchLeaders> leaders = churchLeadersRepository.findAll();

            if (leaders.isEmpty()) {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No church leaders found.");
                return entityResponse;
            }

            // Process leaders with images
            List<Map<String, Object>> leadersWithImages = leaders.stream()
                    .map(leader -> {
                        Map<String, Object> leaderData = new HashMap<>();
                        leaderData.put("name", leader.getName());
                        leaderData.put("designation", leader.getDesignation());
                        leaderData.put("bioData", leader.getBioData());
                        leaderData.put("phoneNo", leader.getPhoneNo());

                        // Reconstruct image
                        if (leader.getImageData() != null && leader.getImagetype() != null) {
                            String base64Image = Base64.getEncoder().encodeToString(leader.getImageData());
                            String imageUrl = "data:" + leader.getImagetype() + ";base64," + base64Image;
                            leaderData.put("imageUrl", imageUrl);
                        }

                        return leaderData;
                    })
                    .collect(Collectors.toList());

            // Set data and status
            entityResponse.setData(leadersWithImages);
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Church leaders retrieved successfully.");
        } catch (Exception e) {
            log.error("Failed to retrieve church leaders", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to retrieve church leaders.");
        }
        return entityResponse;
    }
}