import {ICrateQuizAnswer, ICrateQuizSlide} from '../interfaces';

export const slides: ICrateQuizSlide[] = [
  {
    questionId: 0,
    questionName: 'Введите ваш вопрос',
    type: "Quiz",
    order: 0,
    seconds: 10,
    img: '',
    points: 500,
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
  }
]
