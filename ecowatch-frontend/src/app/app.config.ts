import {
  ApplicationConfig, provideBrowserGlobalErrorListeners,
  provideZoneChangeDetection
} from '@angular/core';
import { provideRouter } from '@angular/router';

import { routes } from './app.routes';
import {provideHttpClient, withInterceptors} from '@angular/common/http';
import {
  INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
  includeBearerTokenInterceptor,
  provideKeycloak
} from 'keycloak-angular';


export const appConfig: ApplicationConfig = {
  providers: [
    provideRouter(routes),
    provideBrowserGlobalErrorListeners(),
    provideZoneChangeDetection({eventCoalescing:true}),
    provideHttpClient(withInterceptors([includeBearerTokenInterceptor])),
    {
      provide: INCLUDE_BEARER_TOKEN_INTERCEPTOR_CONFIG,
      useValue:[
        {
          urlPattern: /^(http:\/\/localhost:8888)(\/.*)?$/,
          httpMethods: ['GET','POST','PUT','DELETE']
        },
      ]
    },
    provideKeycloak({
      config:{
        url:'http://localhost:8080',
        realm: 'ecowatch',
        clientId: 'client-test',
      },
      initOptions:{
        onLoad: 'login-required',
        checkLoginIframe:false,
        pkceMethod: 'S256',
      }
    })


  ]
};
