export interface ICrateQuizSlide {
  id: number;
  question?: string;
  type?: string;
  order: number;
  timeLimit?: number;
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

export interface IQuizConfig {
  id: number,
  name: string,
  owner: {
    id: number,
    username: string
  }
}
