import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {AlertActionRequest, EnvironmentAlert} from '../models/models';

@Injectable({providedIn:'root'})
export class AlertService{
  private apiUrl= 'http://localhost:8888/IOT-SERVICE/api/environnement'
  constructor(private http:HttpClient) {
  }

  getAll():Observable<EnvironmentAlert[]>{
    return this.http.get<EnvironmentAlert[]>(`${this.apiUrl}/alerts`)
  }

  resolve(id:number,note?:string):Observable<EnvironmentAlert>{
    const body:AlertActionRequest={note}
    return this.http.patch<EnvironmentAlert>(`${this.apiUrl}/${id}/resolve`,body)
  }

  ignore(id:number,note?:string):Observable<EnvironmentAlert>{
    const body:AlertActionRequest={note}
    return this.http.patch<EnvironmentAlert>(`${this.apiUrl}/${id}/ignore`,body)
  }
}
