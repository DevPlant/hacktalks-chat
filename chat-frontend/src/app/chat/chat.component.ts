import {Component, ElementRef, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {Message} from './models/message.model';
import {ChatMessageService} from './service/chat-message.service';
import {KeycloakService} from 'keycloak-angular';

@Component({
    selector: 'app-chat',
    templateUrl: './chat.component.html',
    styleUrls: ['./chat.component.scss']
})
export class ChatComponent implements OnDestroy, OnInit {

    @ViewChild('chatContainer') private chatContainer: ElementRef;

    currentUsername: string;
    messageValue: string;
    messages: Message[];

    private _shouldScrollToBottom: boolean = true;

    constructor(private chatService: ChatMessageService, private keycloakService: KeycloakService) {
    }

    ngOnInit() {
        this.chatService.initialize();
        this.currentUsername = this.keycloakService.getUsername();
        this.messages = this.chatService.messages;
        this.chatContainer.nativeElement.onscroll = (scrolData) => {
            if (this.chatContainer.nativeElement.scrollTop < 250) {
                this.loadPreviousMessages();
            }
            this._shouldScrollToBottom = false;
        };
        this.chatService.onMessagesChanged().subscribe((message) => {
            if (this._shouldScrollToBottom) {
                this._scrollToBottom();
            }
        })
    }


    ngOnDestroy(): void {
        this.chatService.closeSource();
    }

    messageValid() {
        return this.messageValue && this.messageValue.trim().length > 0;
    }

    sendMessage() {
        this.chatService.addMessage(this.messageValue);
        this.messageValue = null;
        this._shouldScrollToBottom = true;
        this._scrollToBottom();
    }

    isMyMessage(message: Message) {
        return message.from === this.currentUsername;
    }

    loadPreviousMessages() {
        this.chatService.loadPreviousMessages();
    }

    scrollPageToBottom() {
        setTimeout(() => {
            document.body.scrollTop = document.body.scrollHeight;
        }, 250);
        setTimeout(() => {
            document.body.scrollTop = document.body.scrollHeight;
        }, 500);
    }


    private _scrollToBottom(): void {
        if (this._shouldScrollToBottom) {
            setTimeout(() => {
                try {
                    this.chatContainer.nativeElement.scrollTop = this.chatContainer.nativeElement.scrollHeight;
                } catch (err) {
                }
            }, 50);
        }
    }


    logout() {
        this.keycloakService.logout();
    }

}
