package tech.gig.examninja.admin.AdminUploads.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {


    @NotBlank(message = "Question is required")
    private String question;

    @NotBlank(message = "Option1 is required")
    private String option1;

    @NotBlank(message = "Option2 is required")
    private String option2;

    private String option3;  // Optional for descriptive questions

    private String option4;  // Optional for descriptive questions

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    @NotBlank(message = "Answer description is required")
    private String answerDescription;

    @NotBlank(message = "Category is required")
    private String category;

    @Pattern(regexp = "easy|medium|hard", message = "Level must be defined - easy, medium, or hard")
    private String level;
    @NotBlank(message = "Question type is required")
    @NotEmpty(message = "Type must be defined")
    @Pattern(regexp = "MCQ|True/False", message = "Type can be MCQ or True/False only")
    private String questionType;  // "MCQ" or "True/False"
}
