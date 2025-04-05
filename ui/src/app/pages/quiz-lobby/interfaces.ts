import {EWSEventTypes} from '../models';
import {ICrateQuizAnswer} from '../create-quiz/interfaces';

export interface ICurrentSlide {
  active: boolean,
  answers: ICrateQuizAnswer[]
  event: EWSEventTypes,
  next:boolean
  points: number
  questionId: number
  questionName: string
  timeLeft: number
}

export interface ILeaderBoardPlayers {
  username: string,
  points: number,
}
