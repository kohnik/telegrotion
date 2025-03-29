import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {IAddQuizBody, IQuizConfig} from '../app/pages/create-quiz/interfaces';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class DataService {

  private readonly baseApi: 'http://localhost:8080/'

  constructor(private readonly http: HttpClient) { }

  public getQuiz(): void {
    this.http.get(`/api/full-quiz/1`).subscribe(el=> console.log(el))
  }

  public addQuiz(body: IAddQuizBody): Observable<IQuizConfig> {
    return this.http.post<IQuizConfig>(`/api/full-quiz`, body)
  }
}
