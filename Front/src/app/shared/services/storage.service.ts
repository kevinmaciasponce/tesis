import {Injectable} from "@angular/core";
import { Router } from '@angular/router';
import { User } from "src/app/models/user";

@Injectable()
export class StorageService {
  private localStorageService;
  private currentSession : User;
  constructor(private router: Router) {
    this.localStorageService = localStorage;
    this.currentSession = this.loadSessionData();
  }

  setCurrentSession(session: User): void {
    this.currentSession = session;
    this.localStorageService.setItem('currentUser', JSON.stringify(session));
  }

  loadSessionData(): any{
    var sessionStr = this.localStorageService.getItem('currentUser');
    return (sessionStr) ? <User> JSON.parse(sessionStr) : null;
  }

  getCurrentSession(): any {
    return this.currentSession;
  }

  removeCurrentSession(): void {
    this.localStorageService.removeItem('currentUser');
    this.currentSession.token = "";
    this.localStorageService.clear();
  }

  getCurrentUser(): any {
    var session: User = this.getCurrentSession();
    return (session && session.user) ? session.user : null;
  };

  isAuthenticated(): boolean {
    return (this.getCurrentToken() != null) ? true : false;
  };

  getCurrentToken(): string {
    var session = this.getCurrentSession();
    return (session && session.token) ? session.token : null;
  };

  logout(): void{
    this.removeCurrentSession();
    this.router.navigate(['/login']);
  }

}