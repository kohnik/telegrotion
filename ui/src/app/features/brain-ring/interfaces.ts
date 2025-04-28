export interface IBrainRingRoomConfig {
  roomId: string,
  joinCode: string
}

export interface IBrainRingRoomInfo {
  roomId: string,
  joinCode: string
  players: IBrainRingTeam[]
}

export interface IBrainRingTeam {
  playerName: string,
  playerId: string
}

export interface IBrainRingCreateTeamResponse {
  roomId: string,
  joinCode: string,
  playerName: string,
  playerId: string
}

export interface IBrainRingTeamAnswerData {
  playerName: string,
  teamId: string
  answerTime: number
}

export interface IBrainRingJoinBody {
  playerName: string,
  joinCode: string
}

export interface IBrainRingPlayerExistsBody {
  playerId: string,
  roomId: string
}

export interface IBrainRingPlayerExistsResponse{
  roomId: string,
  exists: boolean,
  playerName: string,
  playerId: string
}

export interface IBrainRingEndSessionBody {
  roomId: string,
}
