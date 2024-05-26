package com.storage.drive.storagedrive.controller;

import com.storage.drive.storagedrive.model.Note;
import com.storage.drive.storagedrive.services.NoteService;
import com.storage.drive.storagedrive.services.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {

    private UserService userService;
    private NoteService noteService;

    public NoteController(UserService userService, NoteService noteService) {
        this.userService = userService;
        this.noteService = noteService;
    }

    @PostMapping(path = "/note/new")
    public String saveNote(Note note, Model model) {
        Long currentUserId = userService.getCurrentUserId();

        if (currentUserId != null && note != null) {
            try {
                // Prepare the JSON payload for the Cloud Function
                String jsonPayload = "{\"noteContent\":\"" + note.getNoteDescription() + "\"}";

                // Create HttpHeaders and set the content type to application/json
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                // Create HttpEntity with the JSON payload and headers
                HttpEntity<String> requestEntity = new HttpEntity<>(jsonPayload, headers);

            // Send a request to the Cloud Function
                ResponseEntity<String> response = new RestTemplate().postForEntity(
                    "https://us-central1-fabled-emblem-417120.cloudfunctions.net/function-note-lenght-check",
                    requestEntity,
                    String.class
                );

            // Check if the Cloud Function response is successful
                if (response.getStatusCode().is2xxSuccessful()) {
                // Proceed with saving the note if the Cloud Function response indicates success
                    if (note.getNoteId() != null) {
                        int noOfUpdatedNote = noteService.updateNoteDetails(note, currentUserId);
                        if (noOfUpdatedNote == 1) {
                            model.addAttribute("result", "success");
                        }
                    } else {
                        int noOfAddedNote = noteService.addNoteByUserId(note, currentUserId);
                        if (noOfAddedNote == 1) {
                            model.addAttribute("result", "success");
                        }
                    }
                } else {
                // Handle the error response from the Cloud Function
                    model.addAttribute("result", "error");
                    model.addAttribute("message", "Failed to save note: Cloud Function returned an error");
                }
            } catch (Exception e) {
            // Handle any exceptions that occur during the request
                model.addAttribute("result", "error");
                model.addAttribute("message", "Failed to save note: " + e.getMessage());
            }
        } else {
        // Handle scenarios where the currentUserId or note is null
            model.addAttribute("result", "error");
            model.addAttribute("message", "Failed to save note: Invalid input data");
        }
        return "result";
    }

    @GetMapping(path = "/note/delete")
    public String deleteNote(@ModelAttribute("note") Note note, @RequestParam(required = false, name = "noteId") Long noteId,
                                    Model model) {

        Long currentUserId = userService.getCurrentUserId();

        //Checking to see if the note to be deleted is under the current user
        //First, getting the note to be deleted
        Note noteByNoteId = noteService.getNoteByNoteId(noteId);
        //Then, if the note is NOT under the current user, then we don't delete the note
        if (noteByNoteId.getUserId() != currentUserId) {
            model.addAttribute("result", "error");
            return "result";
        }


        if (currentUserId != null && noteId != null) {
            int noOfDeletedNote = noteService.deleteNoteById(noteId);

            if (noOfDeletedNote == 1) {
                model.addAttribute("result", "success");
            }
        }
        return "result";
    }
}
