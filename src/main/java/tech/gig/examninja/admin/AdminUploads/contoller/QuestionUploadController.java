package tech.gig.examninja.admin.AdminUploads.contoller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.gig.examninja.admin.AdminUploads.DTO.ApiResponse;
import tech.gig.examninja.admin.AdminUploads.DTO.QuestionDTO;
import tech.gig.examninja.admin.AdminUploads.exception.InvalidQuestionException;
import tech.gig.examninja.admin.AdminUploads.service.QuestionService;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class QuestionUploadController {
    @Autowired
    private QuestionService questionService;
    @PostMapping("/upload")
    public ResponseEntity<ApiResponse> uploadQuestions(
            @RequestPart("file") MultipartFile file,
            @RequestParam("examName") String examName) throws IOException {

        // Parse the CSV file and validate the DTOs
        List<@Valid QuestionDTO> questions = questionService.parseCSV(file);

        // Upload the validated questions
        int questionCount = questionService.uploadQuestions(questions, examName);

        // Return a response
        return ResponseEntity.ok(new ApiResponse("Uploaded " + questionCount + " questions successfully", true));
    }
}
