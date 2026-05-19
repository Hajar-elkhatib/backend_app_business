package ma.ensi.backend_businnes_app.Service;



import ma.ensi.backend_businnes_app.Model.auth.Entrepreneur;
import ma.ensi.backend_businnes_app.Model.auth.User;
import ma.ensi.backend_businnes_app.Repository.user.EntrepreneurRepository;
import ma.ensi.backend_businnes_app.Repository.user.UserRepository;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateEntrepreneurRequest;
import ma.ensi.backend_businnes_app.DTOS.response.EntrepreneurProfileResponse;
import org.springframework.stereotype.Service;

@Service
public class EntrepreneurService {

    private final EntrepreneurRepository entrepreneurRepository;
    private final UserRepository userRepository;

    public EntrepreneurService(EntrepreneurRepository entrepreneurRepository,
                               UserRepository userRepository) {
        this.entrepreneurRepository = entrepreneurRepository;
        this.userRepository = userRepository;
    }

    // ✅ Get Profile
    public EntrepreneurProfileResponse getProfile(String userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Entrepreneur entrepreneur = entrepreneurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Entrepreneur not found"));

        EntrepreneurProfileResponse response = new EntrepreneurProfileResponse();
        response.setId(entrepreneur.getId());
        response.setUserId(userId);
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCompanyName(entrepreneur.getCompanyName());
        response.setBusinessType(entrepreneur.getBusinessType());

        return response;
    }

    // ✅ Edit Profile
    public EntrepreneurProfileResponse updateProfile(String userId,
                                                     UpdateEntrepreneurRequest request) {

        // 1. Find and update User
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);

        // 2. Find and update Entrepreneur
        Entrepreneur entrepreneur = entrepreneurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Entrepreneur not found"));

        if (request.getCompanyName() != null) entrepreneur.setCompanyName(request.getCompanyName());
        if (request.getBusinessType() != null) entrepreneur.setBusinessType(request.getBusinessType());
        entrepreneurRepository.save(entrepreneur);

        // 3. Return updated profile
        EntrepreneurProfileResponse response = new EntrepreneurProfileResponse();
        response.setId(entrepreneur.getId());
        response.setUserId(userId);
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        response.setPhone(user.getPhone());
        response.setCompanyName(entrepreneur.getCompanyName());
        response.setBusinessType(entrepreneur.getBusinessType());

        return response;
    }
}
