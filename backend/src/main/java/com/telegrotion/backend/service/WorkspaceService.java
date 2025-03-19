package com.telegrotion.backend.service;

import com.telegrotion.backend.entity.User;
import com.telegrotion.backend.entity.Workspace;
import com.telegrotion.backend.exception.UserNotFoundException;
import com.telegrotion.backend.repository.UserRepository;
import com.telegrotion.backend.repository.WorkspaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WorkspaceService {

    private final WorkspaceRepository workspaceRepository;
    private final UserRepository userRepository;

    @Transactional
    public Workspace save(Workspace workspace, Long userId) {
        workspace.setOwner(userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId)));

        return workspaceRepository.save(workspace);
    }

    public List<Workspace> findByOwner(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new UserNotFoundException(userId);
        }

        return workspaceRepository.findByOwnerId(userId);
    }

}
