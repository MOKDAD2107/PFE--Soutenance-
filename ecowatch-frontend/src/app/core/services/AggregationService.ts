import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs';
import {DashBoardResponse, EnvironmentAlert, GlobalCitySummary, WaterRessourceStatus} from '../models/models';


@Injectable({providedIn:"root"})
export class AggregationService{
  private apiUrl="http://localhost:8888/DATA-AGGREGATION-SERVICE/api/aggregate";

  constructor(private http:HttpClient) {
  }

  getDashboard(locationId:number):Observable<DashBoardResponse>{
    return this.http.get<DashBoardResponse>(`${this.apiUrl}/dashboard/${locationId}`)
  }
  getGlobalSummary():Observable<GlobalCitySummary>{
    return this.http.get<GlobalCitySummary>(`${this.apiUrl}/global`);
  }
  getWaterStatus():Observable<WaterRessourceStatus>{
    return this.http.get<WaterRessourceStatus>(`${this.apiUrl}/waterstatus`)
  }

  getLocations():Observable<any>{
    return this.http.get<any[]>('http://localhost:8888/WEATHER-SERVICE/api/locations/location')
  }
  searchCity(cityName: string): Observable<any> {
    return this.http.get(
      `http://localhost:8888/WEATHER-SERVICE/api/weathers/weather/search/${cityName}`
    );
  }
}
