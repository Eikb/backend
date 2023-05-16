package de.vikz.wumtbackend.exam;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Controller
@RequestMapping("/api/v1/file")
public class UploadController {

    //Save the uploaded file to this folder
    private static final String UPLOADED_FOLDER = "";
    private final ExamRepository examRepository;

    public UploadController(ExamRepository examRepository) {
        this.examRepository = examRepository;
    }

    @GetMapping("/")
    public String index() {
        return "upload";
    }

    @PostMapping("/upload/{examName}") // //new annotation since 4.3
    public ResponseEntity<String> singleFileUpload(@RequestPart MultipartFile file,
                                                   RedirectAttributes redirectAttributes, @PathVariable String examName) throws IOException {

        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("message", "Please select a file to upload");
            return ResponseEntity.ok("Datei ist leer");
        }



        // Get the file and save it somewhere
        byte[] bytes = file.getBytes();

        Exam exam =  examRepository.findByName(examName);
        exam.setPdfDocument(bytes);
        examRepository.save(exam);

        return ResponseEntity.ok("Erfolgreich");

    }

    @GetMapping("/uploadStatus")
    public String uploadStatus() {
        return "uploadStatus";
    }

}