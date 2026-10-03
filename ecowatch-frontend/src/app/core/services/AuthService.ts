
import {Injectable} from '@angular/core';
import Keycloak from 'keycloak-js';
@Injectable({providedIn:"root"})
export class AuthService{
  constructor(private keycloak:Keycloak) {
  }
  // Récuperer le token JWT
  getToken(): string| undefined {
    return this.keycloak.token
  }
  // Forcer le rafraîchissement du token s'il est expiré (< 30s restantes)
  async getValidToken():Promise<string>{
    await this.keycloak.updateToken(30);
    return this.keycloak.token!;
  }
  // Récuperer les infos de l'utilisateur connecté
  getUser(){
    return this.keycloak.loadUserProfile();
  }

  // Vérifie si l'utilisateur a un rôle (realm ou client)
  hasRole(role:string):boolean{
    return this.keycloak.hasRealmRole(role)||this.keycloak.hasResourceRole(role)
  }
  // Vérifie si l'utilisateur est authentifié
  isLoggIn():boolean{
    return this.keycloak.authenticated ?? false;
  }
  // Déconnexion
  logOut(){
    return this.keycloak.logout({redirectUri:'http://localhost:4200'})
  }
}
