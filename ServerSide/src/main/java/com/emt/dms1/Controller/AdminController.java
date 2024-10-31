package com.emt.dms1.Controller;

import java.time.LocalDate;
import java.util.List;

import com.emt.dms1.Models.*;
import com.emt.dms1.Repository.AdminRepository;
import com.emt.dms1.Repository.DonationRepository;

import com.emt.dms1.Services.AdminService;
import com.emt.dms1.utils.EntityResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v3/admin")
public class AdminController {

   private AdminService adminService;
    @Autowired
    private DonationRepository donationRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AdminRepository adminRepository;

    @Autowired
    private AuthenticationManager authenticationManager;




    @Autowired
    public AdminController(AdminService adminService ) {
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

    @PostMapping("/events")
    public ResponseEntity<EntityResponse<Events>> saveOrUpdateEvent(
            @RequestParam String event,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        EntityResponse<Events> response = adminService.saveOrUpdateEvent(event, date);
        return ResponseEntity.status(response.getStatusCode()).body(response);
    }

    @DeleteMapping("/event")
    public ResponseEntity<EntityResponse> deleteEvent(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String eventName) {
        EntityResponse response = adminService.deleteEvent(id, eventName);
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

    @PostMapping("/leaders")
    public EntityResponse addChurchLeader(
            @RequestParam("name") String name,
            @RequestParam("designation") String designation,
            @RequestParam("biodata") String bioData,
            @RequestParam("phoneNo") long phoneNo) {
        return adminService.postChurchLeaders(name, designation, bioData, phoneNo);
    }

    @GetMapping("/leaders")
    public EntityResponse getChurchLeaders() {
        return adminService.getChurchLeaders();
    }

    @DeleteMapping("/church-leader")
    public ResponseEntity<EntityResponse<String>> deleteChurchLeader(
            @RequestParam(required = false) Long id,
            @RequestParam(required = false) String name) {
        EntityResponse<String> response = adminService.deleteChurchLeader(name, id);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatusCode()));
    }
    @PostMapping("/PrayerRequest")
    public EntityResponse PostprayerRequest(@RequestParam  String requestText){return  adminService.postPrayerRequest(requestText);}
    @GetMapping("/PrayerRequest")
    public  EntityResponse getPrayerRequest(){ return adminService.getAllPrayerRequests();}


    @PostMapping("/record")
    public EntityResponse<List<AttendanceRecord>> recordAttendance(@RequestBody AttendanceRecord attendanceRecord) {
        return adminService.saveAttendance(attendanceRecord);
    }


    @GetMapping("/allrecords")
    public EntityResponse getattendanceRecords(){return adminService.getAttendanceRecords();

    }

}