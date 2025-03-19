package com.telegrotion.backend.controller;

import com.telegrotion.backend.dto.WorkspaceCreateDTO;
import com.telegrotion.backend.entity.Workspace;
import com.telegrotion.backend.mapper.WorkspaceMapper;
import com.telegrotion.backend.service.WorkspaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/workspaces")
@RequiredArgsConstructor
public class WorkspaceController {

    private final WorkspaceService workspaceService;
    private final WorkspaceMapper workspaceMapper;

    @PostMapping
    public ResponseEntity<Workspace> createWorkspace(@RequestBody WorkspaceCreateDTO createDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(workspaceService.save(workspaceMapper.toWorkSpace(createDTO), createDTO.getUserId()));
    }

    @GetMapping("/users/{userId}")
    public List<Workspace> getByUserId(@PathVariable Long userId) {
        return workspaceService.findByOwner(userId);
    }
}
