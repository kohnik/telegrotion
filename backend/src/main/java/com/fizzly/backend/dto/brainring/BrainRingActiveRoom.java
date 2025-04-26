package com.fizzly.backend.dto.brainring;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BrainRingActiveRoom {
    private boolean ready;
    private String joinCode;
    private List<BrainRingPlayer> players;
}
