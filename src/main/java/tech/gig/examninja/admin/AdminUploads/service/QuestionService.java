package tech.gig.examninja.admin.AdminUploads.service;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tech.gig.examninja.admin.AdminUploads.DTO.QuestionDTO;
import tech.gig.examninja.admin.AdminUploads.exception.InvalidQuestionException;
import tech.gig.examninja.admin.AdminUploads.model.Question;
import tech.gig.examninja.admin.AdminUploads.repository.QuestionRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    /**
     * Parses the CSV file and maps it to a list of QuestionDTO objects.
     *
     * @param file CSV file containing the questions.
     * @return List of QuestionDTO objects.
     * @throws IOException if there is an error reading the file.
     */
    public List<QuestionDTO> parseCSV(MultipartFile file) throws IOException {
        List<QuestionDTO> questions = new ArrayList<>();
        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream()))) {
            Iterable<CSVRecord> records = CSVFormat.DEFAULT
                    .withHeader("question", "option1", "option2", "option3", "option4", "correct_answer", "answer_description", "category", "level")
                    .withSkipHeaderRecord()
                    .parse(reader);

            for (CSVRecord record : records) {
                QuestionDTO dto = mapToDTO(record);
                questions.add(dto);
            }
        }
        return questions;
    }

    /**
     * Uploads the validated questions to the database.
     *
     * @param questions List of QuestionDTO objects to be uploaded.
     * @param examName Name of the exam.
     * @return Number of successfully uploaded questions.
     */
    public int uploadQuestions(List<QuestionDTO> questions, String examName) {
        int questionCount = 0;
        for (QuestionDTO questionDTO : questions) {
            validateQuestion(questionDTO);  // Validate question before saving
            Question question = convertToEntity(questionDTO, examName);
            questionRepository.save(question);
            questionCount++;
        }
        return questionCount;
    }
    /**
     * Validates the questionDTO based on its type (MCQ or True/False).
     *
     * @param questionDTO QuestionDTO object to validate.
     */
    private void validateQuestion(QuestionDTO questionDTO) {
        if (questionDTO.getQuestionType() == null || questionDTO.getQuestion().isEmpty()) {
            throw new InvalidQuestionException("Question type and content must be provided.");
        }

        // Handle validation logic for MCQ or True/False type questions
        if ("True/False".equalsIgnoreCase(questionDTO.getQuestionType())) {
            validateTrueFalseQuestion(questionDTO);
        } else if ("MCQ".equalsIgnoreCase(questionDTO.getQuestionType())) {
            validateMCQQuestion(questionDTO);
        } else {
            throw new InvalidQuestionException("Unsupported question type: " + questionDTO.getQuestionType());
        }
    }
    /**
     * Converts a QuestionDTO to a Question entity to be saved in the database.
     *
     * @param questionDTO DTO containing the question details.
     * @param examName Name of the exam.
     * @return Question entity.
     */
    private Question convertToEntity(QuestionDTO questionDTO, String examName) {
        Question question = new Question();
        question.setExamName(examName);
        question.setQuestion(questionDTO.getQuestion());
        question.setOption1(questionDTO.getOption1());
        question.setOption2(questionDTO.getOption2());
        question.setOption3(questionDTO.getOption3());
        question.setOption4(questionDTO.getOption4());
        question.setCorrectAnswer(questionDTO.getCorrectAnswer());
        question.setAnswerDescription(questionDTO.getAnswerDescription());
        question.setCategory(questionDTO.getCategory());
        question.setLevel(questionDTO.getLevel());
        question.setQuestionType(questionDTO.getQuestionType());
        return question;
    }
    /**
     * Validates True/False type questions.
     *
     * @param questionDTO QuestionDTO object containing question details.
     */
    private void validateTrueFalseQuestion(QuestionDTO questionDTO) {
        if (questionDTO.getOption1() == null || questionDTO.getOption2() == null) {
            throw new InvalidQuestionException("True/False questions must have two options (True, False)");
        }
        if (!"True".equalsIgnoreCase(questionDTO.getOption1()) || !"False".equalsIgnoreCase(questionDTO.getOption2())) {
            throw new InvalidQuestionException("True/False question options must be 'True' and 'False'");
        }
        if (!"True".equalsIgnoreCase(questionDTO.getCorrectAnswer()) && !"False".equalsIgnoreCase(questionDTO.getCorrectAnswer())) {
            throw new InvalidQuestionException("Correct answer must be 'True' or 'False' for True/False questions");
        }
    }

    /**
     * Validates MCQ type questions.
     *
     * @param questionDTO QuestionDTO object containing question details.
     */
    private void validateMCQQuestion(QuestionDTO questionDTO) {
        if (questionDTO.getOption1() == null || questionDTO.getOption2() == null || questionDTO.getCorrectAnswer() == null) {
            throw new InvalidQuestionException("MCQ questions must have at least two options and a correct answer");
        }
        if (!isCorrectAnswerValidForMCQ(questionDTO)) {
            throw new InvalidQuestionException("Correct answer must be one of the provided options");
        }
    }
    /**
     * Checks if the correct answer for MCQ matches one of the provided options.
     *
     * @param questionDTO QuestionDTO object containing question details.
     * @return true if correct answer matches one of the options, false otherwise.
     */
    private boolean isCorrectAnswerValidForMCQ(QuestionDTO questionDTO) {
        return questionDTO.getCorrectAnswer().equalsIgnoreCase(questionDTO.getOption1()) ||
                questionDTO.getCorrectAnswer().equalsIgnoreCase(questionDTO.getOption2()) ||
                questionDTO.getCorrectAnswer().equalsIgnoreCase(questionDTO.getOption3()) ||
                questionDTO.getCorrectAnswer().equalsIgnoreCase(questionDTO.getOption4());
    }

    /**
     * Maps a CSVRecord to a QuestionDTO.
     *
     * @param record CSVRecord containing a single row of question data.
     * @return QuestionDTO object.
     */
    private QuestionDTO mapToDTO(CSVRecord record) {
        return new QuestionDTO(
                record.get("question"),
                record.get("option1"),
                record.get("option2"),
                record.get("option3"),
                record.get("option4"),
                record.get("correct_answer"),
                record.get("answer_description"),
                record.get("category"),
                record.get("level"),
                record.get("questionType")  // Ensure this line is present
        );
    }


}
