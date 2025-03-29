import {ICrateQuizAnswer, ICrateQuizSlide} from './interfaces';

export const slides: ICrateQuizSlide[] = [
  {
    id: 0,
    question: 'Введите ваш вопрос',
    type: "Quiz",
    order: 0,
    timeLimit: 20,
    img: '',
    points: 20,
    answers: [
      {
        answer: 'dfdsf',
        correct: true,
        order: 0,
      },
      {
        answer: '12312',
        correct: false,
        order: 1,
      },
      {
        answer: 'opa',
        correct: false,
        order: 2,
      },
      {
        answer: 'dfsdsd',
        correct: false,
        order: 3,
      },
    ]
  },
  {
    id: 1,
    question: 'Введите ваш вопрос',
    type: "True or False",
    order: 1,
    timeLimit: 20,
    img: '',
    points: 20,
    answers: [
      {
        answer: '23423',
        correct: false,
        order: 0,
      },
      {
        answer: 'sdfgdfg',
        correct: true,
        order: 1,
      },
      {
        answer: 'dfddd',
        correct: false,
        order: 2,
      },
      {
        answer: 'dfsfdfdsd',
        correct: false,
        order: 3,
      },
    ]
  }
]
