import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {
  IAddQuizBody,
  IGoToLobbyBody,
  IQuizConfig, IQuizCreatePlayerResponse, IQuizPlayer,
  IStartedQuizConfig,
  IStartedQuizParticipants,
  IStartQuizBody
} from './interfaces';

@Injectable()
export class QuizDataService {

  private readonly baseApi = 'https://fizzly-7dba31943cb3.herokuapp.com'
  private readonly sessionApi = 'https://fizzly-websocket-50575b6868ed.herokuapp.com'

  constructor(private readonly http: HttpClient) { }

  public getQuiz(quizId: number): Observable<IAddQuizBody> {
    return this.http.get<IAddQuizBody>(`${this.baseApi}/full-quiz/${quizId}`)
  }

  public addQuiz(body: IAddQuizBody): Observable<IQuizConfig> {
    return this.http.post<IQuizConfig>(`${this.baseApi}/full-quiz`, body)
  }

  public getAllQuizzesByUserId(userId: number): Observable<IQuizConfig[]> {
    return this.http.get<IQuizConfig[]>(`${this.baseApi}/quizzes/users/${userId}`)
  }

  public startQuiz(body: IStartQuizBody): Observable<IStartedQuizConfig> {
    return this.http.post<IStartedQuizConfig>(`${this.sessionApi}/quiz-session/start`, body)
  }

  public getParticipantsByCurrentSession(joinCode: string): Observable<IQuizPlayer[]> {
    return this.http.get<IQuizPlayer[]>(`${this.sessionApi}/quiz-session/${joinCode}/players`)
  }

  public gotToLobby(body: IGoToLobbyBody): Observable<IQuizCreatePlayerResponse> {
    return this.http.post<IQuizCreatePlayerResponse>(`${this.sessionApi}/quiz-session/join`, body)
  }
}
