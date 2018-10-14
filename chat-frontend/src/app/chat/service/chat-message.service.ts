import {EventEmitter, Injectable} from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import {Message} from '../models/message.model';
import {KeycloakService} from 'keycloak-angular';
import {Observable} from 'rxjs/internal/Observable';

declare var EventSourcePolyfill: any;


@Injectable()
export class ChatMessageService {

  private _lastTime: number;
  private _allMessages: Message[] = [];

  private _messageEventEmitter = new EventEmitter<void>();

  constructor(private httpClient: HttpClient, private keyCloakService: KeycloakService) {

  }


  initialize() {
    this._loadMessageStream();
    this.loadPreviousMessages();
  }

  addMessage(message: string) {
    this.httpClient.post('/api/chat', new Message(message)).subscribe(() => {
    })
  }

  onMessagesChanged(): Observable<void>{
    return this._messageEventEmitter;
  }

  loadPreviousMessages() {
    let params = new HttpParams();
    if (this._lastTime) {
      params = params.set('beforeTime', String(this._lastTime));
    }
    this.httpClient.get<Message[]>('/api/chat/history', {
      params: params
    }).subscribe((previousMessages) => {
      previousMessages.forEach((message) => {
        this._allMessages.unshift(message);
      });
      if (this._allMessages.length > 0) {
        this._lastTime = this._allMessages[0].messageTime;
      }
      this._messageEventEmitter.emit();
    })
  }

  private _loadMessageStream() {
    this.keyCloakService.getToken().then((token) => {
      const es = new EventSourcePolyfill('/api/chat', {
        headers: {
          'Authorization': 'Bearer ' + token
        }
      });
      es.addEventListener('message', (message) => {
        const parsedMessage = JSON.parse(message.data);
        this._allMessages.push(parsedMessage);
        this._messageEventEmitter.emit()
      });
      es.addEventListener('error', () => {
        es.close();
        setTimeout(()=>{
            console.log('attempting to reconnect ... ');
            this._loadMessageStream();
        },3000);
      });
    })
  }


  get messages(): Message[] {
    return this._allMessages;
  }
}
