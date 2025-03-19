package com.telegrotion.backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class WorkspaceCreateDTO {

    private String name;
    private Long userId;
}
