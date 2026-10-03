import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {WaterRessource, WaterRessourceRequest, WaterRessourcesSeuilRequest} from '../models/models';
import {Observable} from 'rxjs';

@Injectable({providedIn:'root'})
export class WaterAdminService{
  private apiUrl='http://localhost:8888/IOT-SERVICE/api/waters';
  constructor(private http:HttpClient) {
  }
  create(request:WaterRessourceRequest):Observable<WaterRessource>{
    return this.http.post<WaterRessource>(`${this.apiUrl}/save`,request)
  }
  update(id:number,request:WaterRessourceRequest):Observable<WaterRessource>{
    return this.http.put<WaterRessource>(`${this.apiUrl}/${id}`,request)
  }
  updateSeuil(id:number,request:WaterRessourcesSeuilRequest):Observable<WaterRessource>{
    return this.http.put<WaterRessource>(`${this.apiUrl}/${id}/seuil`,request)
  }
  delete(id:number):Observable<void>{
    return this.http.delete<void>(`${this.apiUrl}/${id}`)
  }
}
