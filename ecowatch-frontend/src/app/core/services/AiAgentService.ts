import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ChatRequest, ChatResponse} from '../models/models';
import {Observable} from 'rxjs';

@Injectable({providedIn:"root"})
export class AiAgentService{
  private apiUrl= 'http://localhost:8888/AI-AGENT-SERVICE/api/ai';
  constructor(private http:HttpClient) {
  }
  // @ts-ignore
  chat(request:ChatRequest):Observable<ChatResponse>{
    return this.http.post<ChatResponse>(`${this.apiUrl}/chat`, request);
  }
  getSummary(locationId:number):Observable<{summary:string}>{
    return this.http.get<{summary:string}>(`${this.apiUrl}/summary/${locationId}`);
  }
  explainAlert(alertType: string, message: string, severity: string): Observable<{ explanation: string }> {
    return this.http.post<{ explanation: string }>(`${this.apiUrl}/explain-alert`, {alertType, message, severity
    });
  }
  generateReport(locationId: number, cityName: string, reportType = 'DAILY'): Observable<{ report: string }> {
    return this.http.post<{ report: string }>(`${this.apiUrl}/report`, {locationId, cityName, reportType
    });
  }

  compareCity(city1: number, city2: number): Observable<{ comparison: string }> {
    return this.http.get<{ comparison: string }>(`${this.apiUrl}/compare?city1=${city1}&city2=${city2}`);
  }

}

