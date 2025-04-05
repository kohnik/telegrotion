import { Injectable } from '@angular/core';
import { Client, Message } from '@stomp/stompjs';
import { BehaviorSubject, Subject } from 'rxjs';

export interface IWsResponse {
  joinCode: string,
  userCount: number
}

@Injectable({ providedIn: 'root' })
export class WebSocketService {
  private stompClient: Client;
  private subscriptions = new Map<string, any>();

  public connectionStatus = new BehaviorSubject<boolean>(false);
  public messages = new Subject<{ content: string; topic: string }>();
  public subscriptionsList = new BehaviorSubject<string[]>([]);

  constructor() {
    this.stompClient = new Client({
      brokerURL: 'ws://localhost:8080/quiz-websocket',
      reconnectDelay: 5000,
      debug: (str) => console.debug('[STOMP]', str),
    });
    
    this.configureCallbacks();
  }

  private configureCallbacks() {
    this.stompClient.onConnect = (frame) => {
      this.connectionStatus.next(true);
      // this.subscribeToDefaultTopic();
    };

    this.stompClient.onStompError = (frame) => {
      console.error('STOMP error:', frame);
      this.messages.next({
        content: `Error: ${frame.headers['message'] || 'Unknown STOMP error'}`,
        topic: 'error'
      });
    };

    this.stompClient.onWebSocketError = (error) => {
      console.error('WebSocket error:', error);
      this.messages.next({
        content: `WebSocket Error: ${error.message}`,
        topic: 'error'
      });
    };

    this.stompClient.onDisconnect = () => {
      this.connectionStatus.next(false);
      this.subscriptions.clear();
      this.updateSubscriptionsList();
    };
  }

  public connect() {
    if (!this.stompClient.active) {
      this.stompClient.activate();
    }
  }

  public disconnect() {
    if (this.stompClient.active) {
      this.unsubscribeAll();
      this.stompClient.deactivate();
    }
  }

  public subscribe(topic: string) {
    if (this.subscriptions.has(topic)) return;
    console.log(topic)
    const subscription = this.stompClient.subscribe(topic, (message: Message) => {
      console.log(message)
      this.messages.next({
        content: message.body,
        topic
      });
    });

    this.subscriptions.set(topic, subscription);
    this.updateSubscriptionsList();
  }

  public unsubscribe(topic: string) {
    const subscription = this.subscriptions.get(topic);
    if (subscription) {
      subscription.unsubscribe();
      this.subscriptions.delete(topic);
      this.updateSubscriptionsList();
    }
  }

  private unsubscribeAll() {
    this.subscriptions.forEach(sub => sub.unsubscribe());
    this.subscriptions.clear();
    this.updateSubscriptionsList();
  }

  private subscribeToDefaultTopic() {
    const defaultTopic = '/topic/topic/session';
    if (!this.subscriptions.has(defaultTopic)) {
      this.subscribe(defaultTopic);
    }
  }

  private updateSubscriptionsList() {
    this.subscriptionsList.next([...this.subscriptions.keys()]);
  }
}
