import {KeycloakService} from 'keycloak-angular';
import {environment} from '../../environments/environment';

export function initializer(keycloak: KeycloakService): () => Promise<any> {

  return (): Promise<any> => keycloak.init({
    config: {
      url: environment.keyCloakUrl,
      realm: environment.keyCloakRealm,
      clientId: environment.keyCloakClientId
    },
    initOptions: {
      onLoad: 'login-required',
      checkLoginIframe: false
    },
    enableBearerInterceptor: true,
    bearerExcludedUrls: [
      '/assets',
    ],

  });


}

