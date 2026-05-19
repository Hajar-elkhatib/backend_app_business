package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.auth.Entrepreneur;
import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import ma.ensi.backend_businnes_app.Model.auth.User;
import ma.ensi.backend_businnes_app.Repository.user.EntrepreneurRepository;
import ma.ensi.backend_businnes_app.Repository.user.SpecialistRepository;
import ma.ensi.backend_businnes_app.Repository.user.UserRepository;
import ma.ensi.backend_businnes_app.security.JWTUtil;
import ma.ensi.backend_businnes_app.DTOS.request.RegisterEntrepreneurRequest;
import ma.ensi.backend_businnes_app.DTOS.request.RegisterSpecialistRequest;
import ma.ensi.backend_businnes_app.DTOS.request.LoginRequest;
import ma.ensi.backend_businnes_app.DTOS.response.AuthResponse;
import ma.ensi.backend_businnes_app.DTOS.response.LoginResponse;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final EntrepreneurRepository entrepreneurRepository;
    private final SpecialistRepository specialistRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JWTUtil jwtUtil;


    public AuthService(UserRepository userRepository,
                       EntrepreneurRepository entrepreneurRepository,
                       SpecialistRepository specialistRepository,
                       JWTUtil jwtUtil) {
        this.userRepository = userRepository;
        this.entrepreneurRepository = entrepreneurRepository;
        this.specialistRepository = specialistRepository;
        this.jwtUtil = jwtUtil;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public AuthResponse registerEntrepreneur(RegisterEntrepreneurRequest request) {

        // 1. Check email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Create and save User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("ENTREPRENEUR");
        user.setPhone(request.getPhone());
        user.setCreatedAt(new Date());
        User savedUser = userRepository.save(user);

        // 3. Create and save Entrepreneur
        Entrepreneur entrepreneur = new Entrepreneur();
        entrepreneur.setUserId(savedUser.getId());
        entrepreneur.setCompanyName(request.getCompanyName());
        entrepreneur.setBusinessType(request.getBusinessType());
        entrepreneur.setCreatedAt(new Date());
        entrepreneurRepository.save(entrepreneur);

        // 4. Return response
        return new AuthResponse("Entrepreneur registered successfully",
                savedUser.getId(),
                savedUser.getRole());
    }

    public AuthResponse registerSpecialist(RegisterSpecialistRequest request) {

        // 1. Check email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Create and save User
        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole("SPECIALIST");
        user.setPhone(request.getPhone());
        user.setCreatedAt(new Date());
        User savedUser = userRepository.save(user);

        // 3. Create and save Specialist
        Specialist specialist = new Specialist();
        specialist.setUserId(savedUser.getId());
        specialist.setFullName(request.getFullName());
        specialist.setProfession(request.getProfession());
        specialist.setExpertiseDomain(request.getExpertiseDomain());
        specialist.setSkills(request.getSkills());
        specialist.setSectors(request.getSectors());
        specialist.setLocation(request.getLocation());
        specialist.setLanguages(request.getLanguages());
        specialist.setHourlyRate(request.getHourlyRate());
        specialist.setIndustryExperience(request.getIndustryExperience());
        specialist.setBio(request.getBio());
        specialist.setAvailabilityStatus("AVAILABLE");
        specialist.setRating(0.0);
        specialist.setCompletedProjects(0);
        specialist.setCreatedAt(new Date());
        specialistRepository.save(specialist);

        // 4. Return response
        return new AuthResponse("Specialist registered successfully",
                savedUser.getId(),
                savedUser.getRole());
    }
    public LoginResponse login(LoginRequest request) {

        // 1. Find user by email
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Email not found"));

        // 2. Check password
        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        // 3. Generate token
        String token = jwtUtil.generateToken(user.getEmail(), user.getRole());

        // 4. Return response
        return new LoginResponse(
                token,
                user.getId(),
                user.getRole(),
                user.getFullName()
        );
    }
}