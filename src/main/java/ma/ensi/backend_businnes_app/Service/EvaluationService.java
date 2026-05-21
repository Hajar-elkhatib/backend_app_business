package ma.ensi.backend_businnes_app.Service;

import ma.ensi.backend_businnes_app.Model.social.Evaluation;
import ma.ensi.backend_businnes_app.Model.auth.Specialist;
import ma.ensi.backend_businnes_app.Repository.social.EvaluationRepository;
import ma.ensi.backend_businnes_app.Repository.user.SpecialistRepository;
import ma.ensi.backend_businnes_app.DTOS.request.EvaluationRequest;
import ma.ensi.backend_businnes_app.DTOS.response.EvaluationResponse;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class EvaluationService {

    private final EvaluationRepository evaluationRepository;
    private final SpecialistRepository specialistRepository;

    public EvaluationService(EvaluationRepository evaluationRepository,
                             SpecialistRepository specialistRepository) {
        this.evaluationRepository = evaluationRepository;
        this.specialistRepository = specialistRepository;
    }


    public EvaluationResponse createEvaluation(EvaluationRequest request) {


        Specialist specialist = specialistRepository.findById(request.getSpecialistId())
                .orElseThrow(() -> new RuntimeException("Specialist not found"));


        if (request.getScore() < 0 || request.getScore() > 5) {
            throw new RuntimeException("Score must be between 0 and 5");
        }


        Evaluation evaluation = new Evaluation();
        evaluation.setSpecialistId(request.getSpecialistId());
        evaluation.setEntrepreneurId(request.getEntrepreneurId());
        evaluation.setScore(request.getScore());
        evaluation.setComment(request.getComment());
        evaluation.setStartTime(request.getStartTime());
        evaluation.setEndTime(request.getEndTime());
        evaluation.setAvailableDate(request.getAvailableDate());
        evaluation.setStatus("COMPLETED");
        evaluation.setCurrentSessions(1);
        Evaluation saved = evaluationRepository.save(evaluation);


        recalculateRating(request.getSpecialistId());

        return mapToResponse(saved);
    }


    public List<EvaluationResponse> getEvaluationsBySpecialist(String specialistId) {
        return evaluationRepository.findBySpecialistId(specialistId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public List<EvaluationResponse> getEvaluationsByEntrepreneur(String entrepreneurId) {
        return evaluationRepository.findByEntrepreneurId(entrepreneurId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }


    public EvaluationResponse getEvaluationById(String id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        return mapToResponse(evaluation);
    }


    public void deleteEvaluation(String id) {
        Evaluation evaluation = evaluationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
        evaluationRepository.delete(evaluation);


        recalculateRating(evaluation.getSpecialistId());
    }


    private void recalculateRating(String specialistId) {
        List<Evaluation> evaluations = evaluationRepository.findBySpecialistId(specialistId);

        if (evaluations.isEmpty()) return;


        double avgRating = evaluations.stream()
                .mapToDouble(Evaluation::getScore)
                .average()
                .orElse(0.0);


        double roundedRating = Math.round(avgRating * 10.0) / 10.0;


        Specialist specialist = specialistRepository.findById(specialistId)
                .orElseThrow(() -> new RuntimeException("Specialist not found"));
        specialist.setRating(roundedRating);
        specialist.setReviewsCount(evaluations.size());
        specialist.setCompletedProjects(evaluations.size());
        specialistRepository.save(specialist);
    }


    private EvaluationResponse mapToResponse(Evaluation evaluation) {
        EvaluationResponse response = new EvaluationResponse();
        response.setId(evaluation.getId());
        response.setSpecialistId(evaluation.getSpecialistId());
        response.setEntrepreneurId(evaluation.getEntrepreneurId());
        response.setScore(evaluation.getScore());
        response.setComment(evaluation.getComment());
        response.setStatus(evaluation.getStatus());
        response.setStartTime(evaluation.getStartTime());
        response.setEndTime(evaluation.getEndTime());
        response.setAvailableDate(evaluation.getAvailableDate());
        response.setCurrentSessions(evaluation.getCurrentSessions());
        return response;
    }
}