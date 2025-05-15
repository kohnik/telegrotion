import {EWSEventQuizTypes} from '../models';
import {ICrateQuizAnswer} from '../interfaces';

export interface ICurrentSlide {
  active: boolean,
  answers: ICrateQuizAnswer[]
  event: EWSEventQuizTypes,
  next:boolean
  points: number
  questionId: number
  questionName: string
  timeLeft: number
}

export interface ILeaderBoardPlayers {
  playerName: string,
  points: number,
}
