import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  IAddQuizBody,
  IQuizConfig,
  IStartedQuizConfig,
  IStartedQuizParticipants,
  IStartQuizBody
} from '../../features/quiz/interfaces';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private readonly baseApi = 'https://206.189.1.17:8090'

  constructor(private readonly http: HttpClient) { }

  public getQuiz(quizId: number): Observable<IAddQuizBody> {
    return this.http.get<IAddQuizBody>(`${this.baseApi}/full-quiz/${quizId}`)
  }

  public addQuiz(body: IAddQuizBody): Observable<IQuizConfig> {
    return this.http.post<IQuizConfig>(`${this.baseApi}/full-quiz`, body)
  }

  public startQuiz(body: IStartQuizBody): Observable<IStartedQuizConfig> {
    return this.http.post<IStartedQuizConfig>(`${this.baseApi}/quiz-session/start`, body)
  }

  public getAllQuizzesByUserId(userId: number): Observable<IQuizConfig[]> {
    return this.http.get<IQuizConfig[]>(`${this.baseApi}/quizzes/users/${userId}`)
  }

  public getParticipantsByCurrentSession(joinCode: string): Observable<IStartedQuizParticipants> {
    return this.http.get<IStartedQuizParticipants>(`${this.baseApi}/quiz-session/${joinCode}/participants`)
  }

  public gotToLobby(joinCode: string, username: string): Observable<IStartedQuizParticipants> {
    let body = {
      username: username,
      joinCode: joinCode,
    }
    console.log(body)
    return this.http.post<IStartedQuizParticipants>(`${this.baseApi}/quiz-session/join`, body)
  }

}
