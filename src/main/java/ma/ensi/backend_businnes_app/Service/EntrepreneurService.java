package ma.ensi.backend_businnes_app.Service;



import ma.ensi.backend_businnes_app.Model.auth.Entrepreneur;
import ma.ensi.backend_businnes_app.Model.auth.User;
import ma.ensi.backend_businnes_app.Repository.user.EntrepreneurRepository;
import ma.ensi.backend_businnes_app.Repository.user.UserRepository;
import ma.ensi.backend_businnes_app.DTOS.request.UpdateEntrepreneurRequest;
import ma.ensi.backend_businnes_app.DTOS.response.EntrepreneurProfileResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntrepreneurService {

    private final EntrepreneurRepository entrepreneurRepository;
    private final UserRepository userRepository;

    public EntrepreneurService(EntrepreneurRepository entrepreneurRepository,
                               UserRepository userRepository) {
        this.entrepreneurRepository = entrepreneurRepository;
        this.userRepository = userRepository;
    }


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


    public EntrepreneurProfileResponse updateProfile(String userId,
                                                     UpdateEntrepreneurRequest request) {


        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (request.getFullName() != null) user.setFullName(request.getFullName());
        if (request.getPhone() != null) user.setPhone(request.getPhone());
        userRepository.save(user);


        Entrepreneur entrepreneur = entrepreneurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Entrepreneur not found"));

        if (request.getCompanyName() != null) entrepreneur.setCompanyName(request.getCompanyName());
        if (request.getBusinessType() != null) entrepreneur.setBusinessType(request.getBusinessType());
        entrepreneurRepository.save(entrepreneur);


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
    public List<EntrepreneurProfileResponse> getAllEntrepreneurs() {
        return entrepreneurRepository.findAll()
                .stream()
                .map(entrepreneur -> {
                    User user = userRepository.findById(entrepreneur.getUserId())
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    EntrepreneurProfileResponse response = new EntrepreneurProfileResponse();
                    response.setId(entrepreneur.getId());
                    response.setUserId(entrepreneur.getUserId());
                    response.setFullName(user.getFullName());
                    response.setEmail(user.getEmail());
                    response.setPhone(user.getPhone());
                    response.setCompanyName(entrepreneur.getCompanyName());
                    response.setBusinessType(entrepreneur.getBusinessType());
                    return response;
                })
                .collect(Collectors.toList());
    }


    public void deleteEntrepreneur(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Entrepreneur entrepreneur = entrepreneurRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Entrepreneur not found"));

        entrepreneurRepository.delete(entrepreneur);
        userRepository.delete(user);
    }
}
