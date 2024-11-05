package com.emt.dms1.Services;


import com.emt.dms1.Models.*;
import com.emt.dms1.Repository.*;
import com.emt.dms1.utils.EntityResponse;


import java.util.*;

import com.emt.dms1.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;


import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import javax.crypto.SecretKey;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.time.LocalDate;


@Slf4j
@Service
public class AdminService {

    @Autowired
    private AdminRepository adminRepository;
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private AttendanceRecordRepository attendanceRecordRepository;
    @Autowired
    private UserRepository userRepository;
    private  EmailService emailService;
    private JwtUtil jwtUtil;
    private PrayerRequestRepository prayerRequestRepository;
    @Autowired
    private UserInterestRepository userInterestRepository;
    @Autowired
    private SermonRepository sermonRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    private EventRepository eventRepository;
    @Autowired
    private ChurchLeadersRepository churchLeadersRepository;
    @Autowired
    private WelcomeRepo welcomeRepo;

    private JwtUtils jwtUtils;

    @Autowired
    private AuthenticationManager authenticationManager;

    private final AtomicReference<String> liveStreamUrl = new AtomicReference<>();

    private final SecretKey secretKey;
    private static final long EXPIRATION_TIME = 15 * 60 * 1000; // 15 minutes

    public AdminService(EventRepository eventRepository,EmailService emailService,SecretKey secretKey, JavaMailSender mailSender, AttendanceRecordRepository attendanceRecordRepository, PrayerRequestRepository prayerRequestRepository, UserInterestRepository userInterestRepository, ChurchLeadersRepository churchLeadersRepository) {
        this.eventRepository = eventRepository;
        this.userInterestRepository = userInterestRepository;
        this.churchLeadersRepository = churchLeadersRepository;
        this.prayerRequestRepository = prayerRequestRepository;
        this.attendanceRecordRepository=attendanceRecordRepository;
        this.mailSender = mailSender;
        this.emailService=emailService;
               this.secretKey = Keys.secretKeyFor(SignatureAlgorithm.HS512);
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


    public EntityResponse<Events> saveOrUpdateEvent(String eventName, LocalDate eventDate) {
        EntityResponse<Events> entityResponse = new EntityResponse<>();
        log.info("Saving or updating event...");

        try {
            // Validate input
            validateEventInput(eventName, eventDate);

            // Create new event entity
            Events eventEntity = new Events();
            eventEntity.setEventName(eventName);
            eventEntity.setDate(eventDate);

            // Save event to the repository
            eventRepository.save(eventEntity);

            // Respond with success
            entityResponse.setStatusCode(HttpStatus.CREATED.value());
            entityResponse.setMessage("Event saved successfully.");
            entityResponse.setData(eventEntity);
            log.info("Event saved successfully: {}", eventEntity);

        } catch (IllegalArgumentException e) {
            log.error("Invalid input: {}", e.getMessage());
            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            entityResponse.setMessage(e.getMessage());
        } catch (Exception e) {
            log.error("Failed to save or update event.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("An unexpected error occurred while saving the event.");
        }

        return entityResponse;
    }

    private void validateEventInput(String eventName, LocalDate eventDate) {
        if (eventName == null || eventName.trim().isEmpty()) {
            throw new IllegalArgumentException("Event name cannot be null or empty.");
        }

        if (eventDate == null) {
            throw new IllegalArgumentException("Event date cannot be null.");
        }

        if (eventDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Event date cannot be in the past.");
        }
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
                            eventData.put("name", event.getEventName());
                            eventData.put("date", event.getDate());

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

    public EntityResponse deleteEvent(Long eventId, String eventName) {
        EntityResponse entityResponse = new EntityResponse<>();

        try {
            if (eventId != null) {
                log.info("Deleting event with ID: {}", eventId);
                Optional<Events> eventOptional = eventRepository.findById(eventId);

                if (eventOptional.isPresent()) {
                    eventRepository.deleteById(eventId);
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage("Event deleted successfully by ID.");
                    log.info("Event deleted successfully with ID: {}", eventId);
                } else {
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("Event not found by ID.");
                    log.info("Event not found with ID: {}", eventId);
                }
            } else if (eventName != null && !eventName.isEmpty()) {
                log.info("Deleting event with name: {}", eventName);
                Optional<Events> eventOptional = eventRepository.findByName(eventName);

                if (eventOptional.isPresent()) {
                    eventRepository.deleteById(eventOptional.get().getId());
                    entityResponse.setStatusCode(HttpStatus.OK.value());
                    entityResponse.setMessage("Event deleted successfully by name.");
                    log.info("Event deleted successfully with name: {}", eventName);
                } else {
                    entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                    entityResponse.setMessage("Event not found by name.");
                    log.info("Event not found with name: {}", eventName);
                }
            } else {
                entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
                entityResponse.setMessage("Event ID or Name is required.");
                log.info("No event ID or name provided.");
            }
        } catch (Exception e) {
            log.error("Failed to delete event", e);
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

    public EntityResponse<ChurchLeaderDTO> postChurchLeaders(String leadername, String designation, String bioData, long phoneNo) {
        EntityResponse<ChurchLeaderDTO> entityResponse = new EntityResponse<>();
        log.info("Adding church leader");

        try {
            churchLeaders churchLeader = new churchLeaders();
            churchLeader.setLeadername(leadername);
            churchLeader.setPhoneNo(phoneNo);
            churchLeader.setBioData(bioData);
            churchLeader.setDesignation(designation);


            // Save the leader
            churchLeadersRepository.save(churchLeader);
            log.info("Leader added successfully");

            // Create DTO for response
            ChurchLeaderDTO leaderDTO = new ChurchLeaderDTO();
            leaderDTO.setName(leadername);
            leaderDTO.setPhoneNo(phoneNo);
            leaderDTO.setBioData(bioData);
            leaderDTO.setDesignation(designation);


            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Leader added successfully");
            entityResponse.setData(leaderDTO);  // Send the DTO as the response
        } catch (Exception e) {
            log.error("Failed to add leader", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to add leader");
        }

        return entityResponse;
    }

    public EntityResponse<List<ChurchLeaderDTO>> getChurchLeaders() {
        EntityResponse<List<ChurchLeaderDTO>> entityResponse = new EntityResponse<>();
        log.info("Fetching all church leaders");

        try {
            // Fetch all leaders from the repository
            List<churchLeaders> leadersList = churchLeadersRepository.findAll();

            if (!leadersList.isEmpty()) {
                // Create a list of ChurchLeaderDTOs
                List<ChurchLeaderDTO> leaderDTOs = leadersList.stream().map(leader -> {
                    ChurchLeaderDTO leaderDTO = new ChurchLeaderDTO();
                    leaderDTO.setName(leader.getLeadername());
                    leaderDTO.setPhoneNo(leader.getPhoneNo());
                    leaderDTO.setBioData(leader.getBioData());
                    leaderDTO.setDesignation(leader.getDesignation());


                    return leaderDTO;
                }).collect(Collectors.toList());

                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("Church leaders fetched successfully");
                entityResponse.setData(leaderDTOs);
            } else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("No church leaders found");
            }

        } catch (Exception e) {
            log.error("Failed to fetch church leaders", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to fetch church leaders");
        }

        return entityResponse;
    }

    public EntityResponse<String> deleteChurchLeader(String name, Long id) {
        EntityResponse<String> response = new EntityResponse<>();
        try {
            if (id != null) {
                // Delete by ID
                if (churchLeadersRepository.findById(id).isPresent()) {
                    churchLeadersRepository.deleteById(id);
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Church leader deleted successfully.");
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Church leader not found by ID.");
                }
            } else if (name != null && !name.isEmpty()) {
                // Delete by name
                Optional<churchLeaders> leader = churchLeadersRepository.findByName(name);
                if (leader.isPresent()) {
                    churchLeadersRepository.delete(leader.get());
                    response.setStatusCode(HttpStatus.OK.value());
                    response.setMessage("Church leader deleted successfully.");
                } else {
                    response.setStatusCode(HttpStatus.NOT_FOUND.value());
                    response.setMessage("Church leader not found by name.");
                }
            } else {
                response.setStatusCode(HttpStatus.BAD_REQUEST.value());
                response.setMessage("Invalid input. Neither ID nor name provided.");
            }
        } catch (Exception e) {
            log.error("Failed to delete church leader", e);
            response.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            response.setMessage("Failed to delete church leader.");
        }
        return response;
    }

    public EntityResponse register(UserModel.RegisterRequest request) {
        EntityResponse entityResponse = new EntityResponse<>();

        try {
            Optional<UserModel> existingUser = adminRepository.findByEmail(request.getEmailAddress());
            if (existingUser.isPresent()) {
                entityResponse.setStatusCode(HttpStatus.CONFLICT.value());
                entityResponse.setMessage("User with this email already exists.");
                return entityResponse;
            }

            var user = UserModel.builder()
                    .username(request.getUsername())
                    .email(request.getEmailAddress())
                    .password(passwordEncoder.encode(request.getPassword())) // Encrypt the password
                    .phoneNumber(Long.valueOf(request.getPhoneNumber()))

                    .build();

            adminRepository.save(user);

            log.info("User registered successfully with email: {}", request.getEmailAddress());

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("User registered successfully.");
            return entityResponse;

        } catch (Exception e) {
            log.error("Registration failed for email: {}", request.getEmailAddress(), e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Registration failed. Something went wrong.");
            return entityResponse;
        }
    }

    public EntityResponse authenticate(AuthenticationRequest request) {
        EntityResponse entityResponse = new EntityResponse<>();

        try {
            // Attempt to find the user by username
            Optional<UserModel> userOpt = adminRepository.findByUsername(request.getUsername());
            if (userOpt.isEmpty()) {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("User not found.");
                return entityResponse;
            }

            UserModel user = userOpt.get();

            // Check if the provided password matches the stored password
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                log.warn("Invalid credentials provided for username: {}", request.getUsername());
                entityResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
                entityResponse.setMessage("Invalid credentials.");
                return entityResponse;
            }

            // Log successful login
            log.info("User authenticated successfully with username: {}", request.getUsername());

            // Return success response
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("User authenticated successfully.");
            return entityResponse;

        } catch (AuthenticationException e) {
            log.error("Authentication failed for username: {}", request.getUsername(), e);
            entityResponse.setStatusCode(HttpStatus.UNAUTHORIZED.value());
            entityResponse.setMessage("Authentication failed.");
            return entityResponse;
        } catch (Exception e) {
            // Log general error
            log.error("An error occurred during authentication for username: {}", request.getUsername(), e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Something went wrong.");
            return entityResponse;
        }
    }

    public EntityResponse updateUserRoleToAdmin(String email) {
        EntityResponse entityResponse = new EntityResponse<>();

        try {
            Optional<UserModel> existingUser = adminRepository.findByEmail(email);
            if (existingUser.isEmpty()) {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("User with this email does not exist.");
                return entityResponse;
            }

            UserModel userModel = existingUser.get();
             // Set role to admin
            adminRepository.save(userModel);

            log.info("User role updated to admin for email: {}", email);

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("User role updated to admin successfully.");
            return entityResponse;

        } catch (Exception e) {
            log.error("Failed to update user role to admin for email: {}", email, e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to update user role to admin. Something went wrong.");
            return entityResponse;
        }
    }

    public EntityResponse<List<UserModel>> getAllUsers() {
        EntityResponse<List<UserModel>> entityResponse = new EntityResponse<>();
        try {
            // Fetch all users from the repository
            List<UserModel> users = userRepository.findAll();

            // Check if the list of users is empty
            if (users.isEmpty()) {
                entityResponse.setStatusCode(HttpStatus.NO_CONTENT.value());
                entityResponse.setMessage("No users found.");
                entityResponse.setData(Collections.emptyList());  // Return an empty list if no users are found
                return entityResponse;
            }

            // Successfully retrieved users
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Users retrieved successfully.");
            entityResponse.setData(users);  // Return the list of users
            return entityResponse;

        } catch (Exception e) {
            // Log any error that occurs
            log.error("Error retrieving users.", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to retrieve users. Something went wrong.");
            return entityResponse;
        }
    }

    public EntityResponse<UserModel> findUserByUsername(String username) {
        EntityResponse<UserModel> entityResponse = new EntityResponse<>();

        try {
            // Search for user by username
            Optional<UserModel> userOptional = adminRepository.findByUsername(username);

            if (userOptional.isPresent()) {
                entityResponse.setStatusCode(HttpStatus.OK.value());
                entityResponse.setMessage("User found successfully.");
                entityResponse.setData(userOptional.get());  // Set the found user in the response
            } else {
                entityResponse.setStatusCode(HttpStatus.NOT_FOUND.value());
                entityResponse.setMessage("User with username '" + username + "' not found.");
            }
            return entityResponse;

        } catch (Exception e) {
            // Log the error and return failure response
            log.error("Error searching user with username: {}", username, e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to search user. Something went wrong.");
            return entityResponse;
        }
    }

    public EntityResponse<PrayerRequest> postPrayerRequest(String requestText) {
        EntityResponse<PrayerRequest> entityResponse = new EntityResponse<>();
        log.info("Adding prayer request");

        try {
            PrayerRequest prayerRequest = new PrayerRequest();
            prayerRequest.setRequest(requestText);

            // Save the prayer request
            prayerRequestRepository.save(prayerRequest);
            log.info("Prayer request added successfully");

            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Prayer request added successfully");
            entityResponse.setData(prayerRequest);  // Send the saved request as the response
        } catch (Exception e) {
            log.error("Failed to add prayer request", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to add prayer request");
        }

        return entityResponse;
    }

    // Method to get all prayer requests
    public EntityResponse<List<PrayerRequest>> getAllPrayerRequests() {
        EntityResponse<List<PrayerRequest>> entityResponse = new EntityResponse<>();
        log.info("Retrieving all prayer requests");

        try {
            List<PrayerRequest> prayerRequests = prayerRequestRepository.findAll();
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Prayer requests retrieved successfully");
            entityResponse.setData(prayerRequests);  // Send the list of requests as the response
        } catch (Exception e) {
            log.error("Failed to retrieve prayer requests", e);
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("Failed to retrieve prayer requests");
        }

        return entityResponse;
    }

    public EntityResponse<List<AttendanceRecord>> saveAttendance(AttendanceRecord attendanceRecord) {
        EntityResponse<List<AttendanceRecord>> entityResponse = new EntityResponse<>();

        // Check if an attendance record with the same phone number already exists
        Optional<AttendanceRecord> existingRecord = attendanceRecordRepository.findByPhoneNumber(attendanceRecord.getPhoneNumber());

        if (existingRecord.isPresent()) {
            // If the record exists, return a bad request response
            entityResponse.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
            entityResponse.setMessage("This person has already been recorded.");
        } else {
            // Save the new attendance record
            attendanceRecordRepository.save(attendanceRecord);

            // Fetch all attendance records after saving
            List<AttendanceRecord> allRecords = attendanceRecordRepository.findAll();

            // Set response details
            entityResponse.setStatusCode(HttpStatus.OK.value());
            entityResponse.setMessage("Attendance record saved successfully.");
            entityResponse.setData(allRecords);
        }

        return entityResponse;
    }

    // Method to get all attendance records
    public EntityResponse<List<AttendanceRecord>> getAttendanceRecords() {
        EntityResponse<List<AttendanceRecord>> entityResponse = new EntityResponse<>();

        // Fetch all records from the database
        List<AttendanceRecord> allRecords = attendanceRecordRepository.findAll();

        // Set response details
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setMessage("Attendance records retrieved successfully.");
        entityResponse.setData(allRecords);

        return entityResponse;
    }

    public String getAuthenticatedUsername() {
        // Get the current authentication object from the security context
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the authentication object is not null and is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            // Get the principal, which typically contains user details
            Object principal = authentication.getPrincipal();

            // Check if the principal is an instance of UserDetails
            if (principal instanceof UserDetails) {
                return ((UserDetails) principal).getUsername(); // Return the username
            } else {
                // If principal is not UserDetails, it might be a String (username)
                return principal.toString();
            }
        }

        throw new RuntimeException("User is not authenticated"); // Or handle it as per your application's needs
    }
    public EntityResponse<String> changePassword(String oldPassword, String newPassword) {
        EntityResponse<String> entityResponse = new EntityResponse<>();

        // Fetch the currently authenticated user
        String username = getAuthenticatedUsername();
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Check if the old password matches
        if (!passwordEncoder.matches(oldPassword, userModel.getPassword())) {
            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            entityResponse.setMessage("Old password is incorrect");
            return entityResponse;
        }

        // Update the user's password
        userModel.setPassword(passwordEncoder.encode(newPassword)); // Ensure the new password is encoded
        userRepository.save(userModel);

        // Success response
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setMessage("Password changed successfully.");
        return entityResponse;
    }

    public EntityResponse<String> handleForgotPassword(ForgotPasswordRequest request) {
        EntityResponse<String> entityResponse = new EntityResponse<>();
        String email = request.getEmail();

        // Find the user by email
        UserModel user = findByEmail(email);
        if (user == null) {
            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            entityResponse.setMessage("User not found");
            return entityResponse;
        }

        // Generate a unique token
        String token = generateToken(user);

        // Send password reset email with the generated token
        emailService.sendPasswordResetEmail(user, token);

        // Set success response
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setMessage("Password reset link sent to your email");

        return entityResponse;
    }

    private UserModel findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }



    // Method to generate password reset token
    private String generateToken(UserModel userModel) {
        UUID uuid = UUID.randomUUID();
        return jwtUtil.generatePasswordResetToken(uuid.toString(), userModel.getId());
    }





    // Reset password using the token
    public EntityResponse<String> resetPassword(String token, String newPassword) {
        EntityResponse<String> entityResponse = new EntityResponse<>();

        String username;
        try {
            // Use the instance variable secretKey instead of SECRET_KEY
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(secretKey) // Use secretKey variable
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            username = claims.getSubject();
        } catch (Exception e) {
            entityResponse.setStatusCode(HttpStatus.BAD_REQUEST.value());
            entityResponse.setMessage("Invalid or expired reset token");
            return entityResponse;
        }

        // Fetch the user by username
        UserModel userModel = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Update the user's password
        userModel.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(userModel);

        // Success response
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setMessage("Password reset successfully.");
        return entityResponse;
    }


    public EntityResponse<String> logout(HttpServletRequest request, HttpServletResponse response) {
        EntityResponse<String> entityResponse = new EntityResponse<>();

        // Invalidate the session (if session-based authentication)
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Optionally, clear any authentication stored in SecurityContext
        SecurityContextHolder.clearContext();

        // Set success response
        entityResponse.setStatusCode(HttpStatus.OK.value());
        entityResponse.setMessage("User logged out successfully.");
        return entityResponse;


}




}







