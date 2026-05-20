package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.social.Complaint;
import ma.ensi.backend_businnes_app.Repository.social.ComplaintRepository;
import ma.ensi.backend_businnes_app.DTOS.request.ComplaintRequest;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateComplaintRequest;
import ma.ensi.backend_businnes_app.DTOS.response.ComplaintResponse;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ComplaintService {

    private final ComplaintRepository complaintRepository;

    public ComplaintService(ComplaintRepository complaintRepository) {
        this.complaintRepository = complaintRepository;
    }

    // Create Complaint
    public ComplaintResponse createComplaint(ComplaintRequest request) {
        Complaint complaint = new Complaint();
        complaint.setUserId(request.getUserId());
        complaint.setSubject(request.getSubject());
        complaint.setBody(request.getBody());
        complaint.setDescription(request.getDescription());
        complaint.setCategory(request.getCategory());
        complaint.setChatType(request.getChatType());
        complaint.setStatus("OPEN");
        complaint.setCreatedAt(new Date());
        Complaint saved = complaintRepository.save(complaint);
        return mapToResponse(saved);
    }

    //  Get All Complaints (Admin)
    public List<ComplaintResponse> getAllComplaints() {
        return complaintRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get Complaints by User
    public List<ComplaintResponse> getComplaintsByUser(String userId) {
        return complaintRepository.findByUserId(userId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get Complaints by Status
    public List<ComplaintResponse> getComplaintsByStatus(String status) {
        return complaintRepository.findByStatus(status)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get Complaints by Category
    public List<ComplaintResponse> getComplaintsByCategory(String category) {
        return complaintRepository.findByCategory(category)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    //  Get Single Complaint
    public ComplaintResponse getComplaintById(String id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        return mapToResponse(complaint);
    }

    //  Update Complaint (Admin treats or closes)
    public ComplaintResponse updateComplaint(String id, UpdateComplaintRequest request) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));

        if (request.getStatus() != null) {
            complaint.setStatus(request.getStatus());
            // Set resolvedAt when closed or resolved
            if (request.getStatus().equals("RESOLVED") ||
                    request.getStatus().equals("CLOSED")) {
                complaint.setResolvedAt(new Date());
            }
        }

        if (request.getAiSuggestedResponse() != null) {
            complaint.setAiSuggestedResponse(request.getAiSuggestedResponse());
        }

        Complaint saved = complaintRepository.save(complaint);
        return mapToResponse(saved);
    }

    // Delete Complaint
    public void deleteComplaint(String id) {
        Complaint complaint = complaintRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Complaint not found"));
        complaintRepository.delete(complaint);
    }

    //  Helper
    private ComplaintResponse mapToResponse(Complaint complaint) {
        ComplaintResponse response = new ComplaintResponse();
        response.setId(complaint.getId());
        response.setUserId(complaint.getUserId());
        response.setSubject(complaint.getSubject());
        response.setBody(complaint.getBody());
        response.setDescription(complaint.getDescription());
        response.setCategory(complaint.getCategory());
        response.setStatus(complaint.getStatus());
        response.setChatType(complaint.getChatType());
        response.setAiSuggestedResponse(complaint.getAiSuggestedResponse());
        response.setCreatedAt(complaint.getCreatedAt());
        response.setResolvedAt(complaint.getResolvedAt());
        return response;
    }
}