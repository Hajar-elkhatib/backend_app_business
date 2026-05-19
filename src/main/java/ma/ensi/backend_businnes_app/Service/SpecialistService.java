package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import ma.ensi.backend_businnes_app.Model.auth.User;
import ma.ensi.backend_businnes_app.Repository.user.SpecialistRepository;
import ma.ensi.backend_businnes_app.Repository.user.UserRepository;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateSpecialistRequest;
import ma.ensi.backend_businnes_app.DTOS.response.SpecialistProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class SpecialistService {

    private final SpecialistRepository specialistRepository;
    private final UserRepository userRepository;

    public SpecialistService(SpecialistRepository specialistRepository,
                             UserRepository userRepository) {
        this.specialistRepository = specialistRepository;
        this.userRepository = userRepository;
    }


    public SpecialistProfileResponse getProfile(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Specialist specialist = specialistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        return mapToResponse(user, specialist);
    }


    public SpecialistProfileResponse updateProfile(String userId,
                                                   UpdateSpecialistRequest request) {

        // 1. Find and update User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        // 2. Find and update Specialist
        Specialist specialist = specialistRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));

        if (request.getProfession() != null) specialist.setProfession(request.getProfession());
        if (request.getExpertiseDomain() != null) specialist.setExpertiseDomain(request.getExpertiseDomain());
        if (request.getSkills() != null) specialist.setSkills(request.getSkills());
        if (request.getSectors() != null) specialist.setSectors(request.getSectors());
        if (request.getLocation() != null) specialist.setLocation(request.getLocation());
        if (request.getLanguages() != null) specialist.setLanguages(request.getLanguages());
        if (request.getBio() != null) specialist.setBio(request.getBio());
        if (request.getAvailabilityStatus() != null) specialist.setAvailabilityStatus(request.getAvailabilityStatus());
        if (request.getHourlyRate() != 0) specialist.setHourlyRate(request.getHourlyRate());
        if (request.getIndustryExperience() != 0) specialist.setIndustryExperience(request.getIndustryExperience());
        specialistRepository.save(specialist);

        // 3. Return updated profile
        return mapToResponse(user, specialist);
    }


    private SpecialistProfileResponse mapToResponse(User user, Specialist specialist) {
        SpecialistProfileResponse response = new SpecialistProfileResponse();
        response.setId(specialist.getId());
        response.setUserId(user.getId());
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setProfession(specialist.getProfession());
        response.setExpertiseDomain(specialist.getExpertiseDomain());
        response.setSkills(specialist.getSkills());
        response.setSectors(specialist.getSectors());
        response.setLocation(specialist.getLocation());
        response.setLanguages(specialist.getLanguages());
        response.setHourlyRate(specialist.getHourlyRate());
        response.setIndustryExperience(specialist.getIndustryExperience());
        response.setBio(specialist.getBio());
        response.setRating(specialist.getRating());
        response.setAvailabilityStatus(specialist.getAvailabilityStatus());
        response.setCompletedProjects(specialist.getCompletedProjects());
        return response;
    }
}