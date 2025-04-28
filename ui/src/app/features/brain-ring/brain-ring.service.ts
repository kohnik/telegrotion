import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  IBrainRingCreateTeamResponse, IBrainRingEndSessionBody,
  IBrainRingJoinBody, IBrainRingPlayerExistsBody, IBrainRingPlayerExistsResponse,
  IBrainRingRoomConfig,
  IBrainRingRoomInfo,
  IBrainRingTeam
} from './interfaces';
import {Observable} from 'rxjs';

@Injectable()
export class BrainRingService {

  private readonly baseApi = 'https://fizzly-7dba31943cb3.herokuapp.com/brain-ring'

  constructor(private readonly http: HttpClient) { }

  startBrainRing(): Observable<IBrainRingRoomConfig> {
    return this.http.post<IBrainRingRoomConfig>(`${this.baseApi}/create-room`, null)
  }

  roomInfo(roomId: string): Observable<IBrainRingRoomInfo> {
    return this.http.get<IBrainRingRoomInfo>(`${this.baseApi}/rooms/${roomId}`)
  }

  goToRoom(body: IBrainRingJoinBody): Observable<IBrainRingCreateTeamResponse> {
    return this.http.post<IBrainRingCreateTeamResponse>(`${this.baseApi}/join-room`, body)
  }

  endSessionRoom(body: IBrainRingEndSessionBody): Observable<unknown> {
    return this.http.post<unknown>(`${this.baseApi}/rooms/finish`, body)
  }

  checkPlayerExists(body: IBrainRingPlayerExistsBody): Observable<IBrainRingPlayerExistsResponse> {
    return this.http.post<IBrainRingPlayerExistsResponse>(`${this.baseApi}/player-exists`, body)
  }
}
