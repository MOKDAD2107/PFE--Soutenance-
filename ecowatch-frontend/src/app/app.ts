import {Component, OnInit, signal} from '@angular/core';
import {Router, RouterLink, RouterOutlet} from '@angular/router';
import Keycloak from 'keycloak-js';
import {AggregationService} from './core/services/AggregationService';
import {Chatbot} from './pages/chatbot/chatbot';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink, Chatbot, FormsModule],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App implements OnInit{
  protected readonly title = signal('ecowatch-frontend');
  public profile : any=null ;

  searchText = '';
  searchError = '';
  userMenuOpen = false;

  constructor(public  keycloakservice:Keycloak,
              private aggregation: AggregationService,
              private router: Router) {
  }

  ngOnInit(): void {
    if (this.keycloakservice.authenticated){
      this.profile=this.keycloakservice.tokenParsed
    }
    }
  get username(): string {
    return this.keycloakservice.tokenParsed?.['preferred_username'] ?? '';
  }
  get userRoles(): string[] {
    return this.keycloakservice.tokenParsed?.['realm_access']?.['roles'] ?? [];
  }

  get isLoggedIn(): boolean {
    return this.keycloakservice.authenticated ?? false;
  }
  async handlelogin() {
    await this.keycloakservice.login({
      redirectUri: document.location.origin})
  }

  handlelogout() {
    this.keycloakservice.logout()
    {
      document.location.origin
    }
  }
  toggleUserMenu(): void {
    this.userMenuOpen = !this.userMenuOpen;
  }

  doSearch(): void {
    const q = this.searchText.trim();
    if (!q) return;
    this.searchError = '';
    this.aggregation.searchCity(q).subscribe({
      next: (res: any) => {
        const id = res?.locationId ?? res?.id;
        if (id) {
          this.router.navigate(['/dashboard', id]);
          this.searchText = '';
        } else {
          this.searchError = 'Ville introuvable.';
        }
      },
      error: () => {
        this.searchError = 'Ville introuvable.';
      }
    });
  }
}
