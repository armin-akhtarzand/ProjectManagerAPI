package se.iths.armin.projectmanagerapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    ResponseEntity<List<CommentResponseDto>> getAllComments(@PathVariable Long projectId, @PathVariable Long taskId) {
        List<CommentResponseDto> responseDto = commentService.findAllByTask(taskId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

}
