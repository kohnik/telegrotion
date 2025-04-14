import { Injectable } from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {
  IBrainRingCreateTeamResponse,
  IBrainRingJoinBody,
  IBrainRingRoomConfig,
  IBrainRingRoomInfo,
  IBrainRingTeam
} from './interfaces';
import {Observable} from 'rxjs';

@Injectable()
export class BrainRingService {

  private readonly baseApi = 'https://206.189.1.17:8090'

  constructor(private readonly http: HttpClient) { }

  startBrainRing(): Observable<IBrainRingRoomConfig> {
    return this.http.post<IBrainRingRoomConfig>(`${this.baseApi}/brain-ring/create-room`, null)
  }

  roomInfo(roomId: string): Observable<IBrainRingRoomInfo> {
    return this.http.get<IBrainRingRoomInfo>(`${this.baseApi}/brain-ring/rooms/${roomId}`)
  }

  goToRoom(body: IBrainRingJoinBody): Observable<IBrainRingCreateTeamResponse> {
    return this.http.post<IBrainRingCreateTeamResponse>(`${this.baseApi}/brain-ring/join-room`, body)
  }
}
