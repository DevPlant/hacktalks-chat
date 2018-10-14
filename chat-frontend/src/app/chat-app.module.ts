import {BrowserModule} from '@angular/platform-browser';
import {APP_INITIALIZER, NgModule} from '@angular/core';

import {HttpClientModule} from '@angular/common/http';
import {FormsModule} from '@angular/forms';
import {ChatComponent} from './chat/chat.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {KeycloakAngularModule, KeycloakService} from 'keycloak-angular';
import {initializer} from './utils/keycloak-initializer';
import {ChatMessageService} from './chat/service/chat-message.service';
import {MatButtonModule, MatIconModule, MatInputModule, MatToolbarModule, MatTooltipModule} from '@angular/material';

@NgModule({
  declarations: [
    ChatComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    FormsModule,
    HttpClientModule,
    KeycloakAngularModule,
    MatToolbarModule,
    MatInputModule,
    MatButtonModule,
    MatTooltipModule,
    MatIconModule
  ],
  providers: [ChatMessageService,
    {
      provide: APP_INITIALIZER,
      useFactory: initializer,
      multi: true,
      deps: [KeycloakService]
    }],
  bootstrap: [ChatComponent]
})
export class ChatAppModule {
}
