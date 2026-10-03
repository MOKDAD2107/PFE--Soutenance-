import { Routes } from '@angular/router';
import {Dashboard} from './pages/dashboard/dashboard';
import {Alerts} from './pages/alerts/alerts';
import {Map} from './pages/map/map';
import {WaterStatus} from './pages/water-status/water-status';
import {guardsGuard} from './core/guards/guards-guard';
import {Chatbot} from './pages/chatbot/chatbot';

export const routes: Routes = [
  {
    path : 'dashboard' , component : Dashboard,
    canActivate : [guardsGuard]  , data : {role :'USER'}
  },
  {
    path : 'alerts', component : Alerts,
    canActivate : [guardsGuard], data : {role:'USER'}
  },
  {
    path : 'map' , component: Map,
    canActivate : [guardsGuard], data : {role:'USER'}
  },
  {
    path : 'water-status', component : WaterStatus,
    canActivate : [guardsGuard], data : {role:'USER'}
  },
  {
    path : 'chatbot' , component : Chatbot,
    canActivate : [guardsGuard], data : {role : 'USER'}
  },
  {path : '', redirectTo :'dashboard', pathMatch : "full"},
  {path : '**', redirectTo : 'dashboard'}

];
