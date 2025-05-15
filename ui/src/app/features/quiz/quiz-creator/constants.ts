import {SpriteIdsType} from '../../../../sprites-ids.type';

export enum ESlideAnswersColor {
  PURPLE = '#be92ff',
  RED = '#ff6a6c',
  GREEN = '#8ce0c7',
  BLUE = 'rgb(97 155 245)',

  // PURPLE = '#D9A300',
  // RED = '#E6193C',
  // GREEN = '#198C19',
  // BLUE = '#1368CE',
}

export enum ESlideAnswersCircleColor {
  PURPLE = '#9d44f4',
  RED = '#fc364f',
  GREEN = '#40cdb7',
  BLUE = 'rgb(154 187 255)',
}

export enum ESlideAnswersCircleColor {
  TRIANGLE = 'triangle-with-shadow',
  SQUARE = 'square-with-shadow',
  CIRCLE = 'circle-with-shadow',
  RHOMBUS = 'rhombus-with-shadow'
}

export const bgAnswerColor = [ESlideAnswersColor.RED, ESlideAnswersColor.GREEN, ESlideAnswersColor.PURPLE, ESlideAnswersColor.BLUE];
export const bgAnswerCircleColor = [ESlideAnswersCircleColor.RED, ESlideAnswersCircleColor.GREEN, ESlideAnswersCircleColor.PURPLE, ESlideAnswersCircleColor.BLUE];
export const answerFigureIcon = [ESlideAnswersCircleColor.TRIANGLE, ESlideAnswersCircleColor.SQUARE, ESlideAnswersCircleColor.CIRCLE, ESlideAnswersCircleColor.RHOMBUS];

export const setAnswerBackground = (index: number) => {
  return bgAnswerColor[index];
}

export const setAnswerFigureIcon = (index: number) => {
  return answerFigureIcon[index] as SpriteIdsType;
}

export const setAnswerSecondaryBackground =(index: number) => {
  return bgAnswerCircleColor[index];
}
