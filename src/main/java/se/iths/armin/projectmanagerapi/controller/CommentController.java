package se.iths.armin.projectmanagerapi.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.iths.armin.projectmanagerapi.dto.comment.CommentRequestDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentResponseDto;
import se.iths.armin.projectmanagerapi.dto.comment.CommentUpdateDto;
import se.iths.armin.projectmanagerapi.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/projects/{projectId}/tasks/{taskId}/comments")
public class CommentController {

    private final CommentService commentService;


    @GetMapping
    ResponseEntity<List<CommentResponseDto>> getAllComments(@PathVariable Long projectId, @PathVariable Long taskId) {
        List<CommentResponseDto> responseDto = commentService.findAllByTask(taskId, projectId);

        return ResponseEntity.status(HttpStatus.OK).body(responseDto);
    }

    @PostMapping
    ResponseEntity<CommentResponseDto> createComment(@RequestBody CommentRequestDto commentRequestDto,
                                                     @PathVariable Long taskId,
                                                     @PathVariable Long projectId) {
        CommentResponseDto responseDto = commentService.create(commentRequestDto, taskId, projectId);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @PutMapping("/{commentId}")
    ResponseEntity<Void> updateComment(@RequestBody CommentUpdateDto commentUpdateDto,
                                       @PathVariable Long taskId,
                                       @PathVariable Long projectId,
                                       @PathVariable Long commentId) {
        commentService.update(commentUpdateDto, commentId, taskId, projectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @DeleteMapping("/{commentId}")
    ResponseEntity<Void> deleteComment(@PathVariable Long taskId,
                                       @PathVariable Long projectId,
                                       @PathVariable Long commentId) {
        commentService.delete(commentId, taskId, projectId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }


}
