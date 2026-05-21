package ma.ensi.backend_businnes_app.Controller;

import ma.ensi.backend_businnes_app.DTOS.request.ComplaintRequest;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateComplaintRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ComplaintResponse;
import ma.ensi.backend_businnes_app.Service.ComplaintService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin(origins = "*")
public class ComplaintController {

    private final ComplaintService complaintService;

    public ComplaintController(ComplaintService complaintService) {
        this.complaintService = complaintService;
    }


    @PostMapping
    public ResponseEntity<ComplaintResponse> createComplaint(
            @RequestBody ComplaintRequest request) {
        return ResponseEntity.ok(complaintService.createComplaint(request));
    }


    @GetMapping
    public ResponseEntity<List<ComplaintResponse>> getAllComplaints() {
        return ResponseEntity.ok(complaintService.getAllComplaints());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<ComplaintResponse>> getByUser(
            @PathVariable String userId) {
        return ResponseEntity.ok(complaintService.getComplaintsByUser(userId));
    }


    @GetMapping("/status/{status}")
    public ResponseEntity<List<ComplaintResponse>> getByStatus(
            @PathVariable String status) {
        return ResponseEntity.ok(complaintService.getComplaintsByStatus(status));
    }


    @GetMapping("/category/{category}")
    public ResponseEntity<List<ComplaintResponse>> getByCategory(
            @PathVariable String category) {
        return ResponseEntity.ok(complaintService.getComplaintsByCategory(category));
    }


    @GetMapping("/{id}")
    public ResponseEntity<ComplaintResponse> getById(
            @PathVariable String id) {
        return ResponseEntity.ok(complaintService.getComplaintById(id));
    }


    @PutMapping("/{id}")
    public ResponseEntity<ComplaintResponse> updateComplaint(
            @PathVariable String id,
            @RequestBody UpdateComplaintRequest request) {
        return ResponseEntity.ok(complaintService.updateComplaint(id, request));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComplaint(
            @PathVariable String id) {
        complaintService.deleteComplaint(id);
        return ResponseEntity.ok("Complaint deleted successfully");
    }
}