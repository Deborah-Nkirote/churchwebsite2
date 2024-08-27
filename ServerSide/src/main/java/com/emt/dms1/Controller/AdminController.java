package com.emt.dms1.Controller;
import java.time.LocalDate;
import java.util.List;

import com.emt.dms1.Models.Donation;
import com.emt.dms1.Models.Events;
import com.emt.dms1.Models.UserInterest;
import com.emt.dms1.Repository.DonationRepository;
import com.emt.dms1.Services.AdminService;
import com.emt.dms1.utils.EntityResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;



@RestController
@RequestMapping("/api/v3/admin")
public class AdminController {

    private final AdminService adminService;
    @Autowired
    private DonationRepository donationRepository;


    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }



    @PutMapping("/update-welcome-message")
    public EntityResponse updateWelcomeMessage(@RequestParam String message) {
        return adminService.updateWelcomeMessage(message);
    }
    @PostMapping("/welcome-message")
    public EntityResponse postWelcomeMessage(@RequestParam String message) {
        return adminService.postWelcomeMessage(message);
    }
    @GetMapping("/welcome-message")
    public EntityResponse getWelcomeMessage() {
        return adminService.getWelcomeMessage();
    }

    @GetMapping("/events/upcoming")
    public EntityResponse getUpcomingEvents() {
        return adminService.getUpcomingEvents();
    }


    @PostMapping(value = "/events", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EntityResponse<Events>> saveOrUpdateEvent(
           // @RequestParam(required = false) Long eventId,
            @RequestParam String event,
            @RequestParam MultipartFile imageFile,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        EntityResponse<Events> response = adminService.saveOrUpdateEvent( event, imageFile, date);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }


    @DeleteMapping("/events/{id}")
    public ResponseEntity<EntityResponse> deleteEventById(@PathVariable("id") Long eventId) {
        EntityResponse response = adminService.deleteEventById(eventId);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }

    @GetMapping("/sermons")
    public EntityResponse getSermons() {
        return adminService.getSermons();
    }
    @PutMapping("/live-stream-url")
    public EntityResponse putLiveStreamUrl(@RequestParam String url) {
        return adminService.putLiveStreamUrl(url);
    }
    @GetMapping("/live-stream-url")
    public EntityResponse getLiveStreamUrl() {
        return adminService.getLiveStreamUrl();
    }





    @PostMapping(value = "/sermons", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EntityResponse uploadSermon(
            @RequestParam String title,
            @RequestParam String videoUrl,
            @RequestParam(required = false) MultipartFile notesFile) {
        return adminService.uploadSermon(title, videoUrl, notesFile);

    }
    @GetMapping("/interests")
    public EntityResponse getUserInterest() {
        return adminService.getUserInterest();
    }
    @PostMapping("/interests")
    public EntityResponse<UserInterest> postUserInterest(
            @RequestParam String name,
            @RequestParam String interest,
            @RequestParam long phoneNumber) {
        return adminService.postUserInterest(name, phoneNumber, interest);
    }

    @PostMapping("/donations")
    public ResponseEntity<Donation> createDonation(@RequestBody Donation donation) {
        Donation savedDonation = donationRepository.save(donation);
        return new ResponseEntity<>(savedDonation, HttpStatus.CREATED);
    }
    @GetMapping("/donations")
    public ResponseEntity<List<Donation>> getAllDonations() {
        List<Donation> donations = donationRepository.findAll();
        return new ResponseEntity<>(donations, HttpStatus.OK);
    }
    @PostMapping(value = "/leaders", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EntityResponse addChurchLeader(
            @RequestParam("name") String name,
            @RequestParam("designation") String designation,
            @RequestParam("biodata") String bioData,
            @RequestParam("image") MultipartFile image,
            @RequestParam("phoneNo") long phoneNo) {

        // Call the service method to add the church leader
        return adminService.postChurchLeaders(name, designation, bioData, image, phoneNo);
    }
    @GetMapping("/leaders")
    public EntityResponse  getChurchLeaders() {
        return adminService.getChurchLeaders();
    }

}
