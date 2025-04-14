export interface IBrainRingRoomConfig {
  roomId: string,
  joinCode: string
}

export interface IBrainRingRoomInfo {
  roomId: string,
  joinCode: string
  teams: IBrainRingTeam[]
}

export interface IBrainRingTeam {
  teamName: string,
  teamId: string
}

export interface IBrainRingCreateTeamResponse {
  roomId: string,
  joinCode: string,
  teamName: string,
  teamId: string
}

export interface IBrainRingTeamAnswerData {
  teamName: string,
  teamId: string
  answerTime: number
}

export interface IBrainRingJoinBody {
  teamName: string,
  joinCode: string
}
