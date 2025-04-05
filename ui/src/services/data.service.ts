import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  IAddQuizBody,
  IQuizConfig,
  IStartedQuizConfig,
  IStartedQuizParticipants,
  IStartQuizBody
} from '../app/pages/create-quiz/interfaces';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private readonly baseApi: 'http://localhost:8080/'

  constructor(private readonly http: HttpClient) { }

  public getQuiz(quizId: number): Observable<IAddQuizBody> {
    return this.http.get<IAddQuizBody>(`/api/full-quiz/${quizId}`)
  }

  public addQuiz(body: IAddQuizBody): Observable<IQuizConfig> {
    return this.http.post<IQuizConfig>(`/api/full-quiz`, body)
  }

  public startQuiz(body: IStartQuizBody): Observable<IStartedQuizConfig> {
    return this.http.post<IStartedQuizConfig>(`/api/quiz-session/start`, body)
  }

  public getAllQuizzesByUserId(userId: number): Observable<IQuizConfig[]> {
    return this.http.get<IQuizConfig[]>(`/api/quizzes/users/${userId}`)
  }

  public getParticipantsByCurrentSession(joinCode: string): Observable<IStartedQuizParticipants> {
    return this.http.get<IStartedQuizParticipants>(`/api/quiz-session/${joinCode}/participants`)
  }

  public gotToLobby(joinCode: string, username: string): Observable<IStartedQuizParticipants> {
    let body = {
      username: username,
      joinCode: joinCode,
    }
    console.log(body)
    return this.http.post<IStartedQuizParticipants>(`/api/quiz-session/join`, body)
  }

}
