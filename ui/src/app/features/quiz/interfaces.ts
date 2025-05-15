export interface ICrateQuizSlide {
  questionId: number;
  question?: string;
  type?: string;
  order: number;
  seconds?: number;
  img?: string;
  points: number;
  answers: ICrateQuizAnswer[]
}

export interface ICrateQuizAnswer {
  answer: string;
  correct: boolean;
  order: number;
  type?: string;
  color?: string
}

export interface IAddQuizBody  {
  name: string,
  userId: number,
  questions: ICrateQuizSlide[]
}

export interface IStartQuizBody  {
  quizId: number,
  userId: number
}

export interface IQuizPlayer  {
  playerName: string,
  playerId: string
}

export interface IStartedQuizConfig {
  roomId: string,
  quizId: number,
  joinCode: string,
  active: boolean
}

export interface IStartedQuizParticipants {
  userCount: number,
  joinCode: string,
  users: string[]
}

export interface IQuizConfig {
  id: number,
  name: string,
  owner: {
    id: number,
    username: string
  }
}

export interface IGoToLobbyBody {
  playerName: string,
  joinCode: string,
}

export interface IQuizCreatePlayerResponse {
  roomId: string,
  joinCode: string,
  playerName: string,
  playerId: string
}
