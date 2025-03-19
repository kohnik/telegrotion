package com.telegrotion.backend.mapper;

import com.telegrotion.backend.dto.WorkspaceCreateDTO;
import com.telegrotion.backend.entity.Workspace;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface WorkspaceMapper {

    Workspace toWorkSpace(WorkspaceCreateDTO createDTO);
}
