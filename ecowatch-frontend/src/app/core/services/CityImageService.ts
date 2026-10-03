import {Injectable} from '@angular/core';

interface CityPhotoConfig {
  url: string;
  position?: string;
  size?: string;
}
@Injectable({providedIn:"root"})
export class CityImageService{
  private readonly photos:Record<string, CityPhotoConfig>={
    'casablanca':{url:'/imgs/cities/casa4.jpg',position:'center 39%'},
    'rabat':{url:'/imgs/cities/rabat6.jpg',position:'center 70%',size:'90%'},
    'marrakech':  {url:'/imgs/cities/marrakech2.jpg',position:'center 50%'},
    'fès':        {url:'/imgs/cities/fes.jpg',position:'bottom 20%'},
    'fes':        {url:'/imgs/cities/fes.jpg',position:'center 70%'},
    'tanger':     {url:'/imgs/cities/tanger.jpg',position:'center 63%'},
    'agadir':     {url:'/imgs/cities/agadir.jpg',position:'center 19%'},
    'oujda':      {url:'/imgs/cities/oujda.jpg',position:'center 52%'},
    'meknès':     {url:'/imgs/cities/meknesimg.jpg',position:'center 72%'},
    'meknes':     {url:'/imgs/cities/meknesimg.jpg',position:'center 72%'},
    'tetouan':    {url:'/imgs/cities/tetouan.jpg',position:'center 76%'},
    'ouarzazate': {url:'/imgs/cities/ouarzazate2.jpg',position:'center 50%'},
  }
  // retourner l'url de la photo de la ville ou null si la ville n'existe pas
  getPhoto(cityName:string|null|undefined):CityPhotoConfig|null{
    if (!cityName)return null;
    const key=cityName.trim().toLowerCase();
    return this.photos[key] ?? null;
  }
  // Permet d'ajouter/écraser une photo à la volée si besoin. */
  setPhoto(cityName: string, url: CityPhotoConfig):void {
    this.photos[cityName.trim().toLowerCase()] = url;
  }
}
