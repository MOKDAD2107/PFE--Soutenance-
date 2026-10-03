export interface LocationSummary{

  id : number;
  nameCity : string;
  country : string;
  latitude : number;
  longitude : number;
}

export interface WeatherData{
  id:number;
  dateTime:string;
  temperature:number;
  humidity:number;
  windSpeed:number;
  pressure:number;
  unIndex?:number;
  description:string;
  weatherIcon: string;
  sourceApi:string;
  location:LocationSummary;
}
export interface WeatherForecast{
  id:number;
  date:string;
  predictedTemp:number;
  predictedHumidity:number;
  predictedSpeed:number;
  precipitationProbability:number;
  description:string;
  expired:boolean;
  location:LocationSummary;
}
export enum SensorType{
  AIR_QUALITY='AIR_QUALITY',
  CO2='CO2',
  TEMPERATURE='TEMPERATURE',
  HUMIDItY_SOL='HUMIDITY_SOL',
  POUSSIERE='POUSSIERE'
}
export enum RessourceType {
  BARRAGE='BARRAGE',
  NAPPE_PHREATIQUE='NAPPE_PHREATIQUE',
  RIVIERE='RIVIERE',
  LAC='LAC'
}
export enum AlertStatus {
  ACTIVE='ACTIVE',
  RESOLVED='RESOLVED',
  IGNORED='IGNORED'
}

export type WaterRessourceStats='CRITIQUE'|'BAS'|'NORMAL'|'ELEVE';
export type AlertSeverity='LOW'|'MEDIUM'|'HIGH'|'CRITICAL';
export interface SensorResponse{
  id:number;
  name:string;
  sensorType: SensorType;
  locationId:number;
  description:string;
  unite:string;
  active:boolean;
  lastReadingDate:string;
}
export interface SensorReading{
  id:number;
  valeur:number;
  unite:string;
  sensorId:number;
  status:AlertStatus;
  readingDate:string;
}

export interface WaterRessource{
  id:number;
  name:string;
  ressourceType:RessourceType;
  capaciteMax:number;
  currentLevel:number;
  fillPercentage:number;
  fillStatus:WaterRessourceStats;
  seuilBas?:number|null;
  seuilCritique?:number|null;
  lastUpdate:string;
  location?:LocationSummary
}
export interface WaterRessourceRequest{
  name:string;
  capaciteMax:number;
  currentLevel:number;
  ressourceType:RessourceType;
  locationId:number;
}

export interface WaterRessourcesSeuilRequest{
  seuilBas:number;
  seuilCritique:number;
}
export interface EnvironmentAlert{
  id:number;
  alertType:string;
  message:string;
  severity:AlertSeverity;
  status:AlertStatus;
  location:LocationSummary|null;
  sensorId:number;
  triggerValue:number;
  seuilDepasse:number;
  triggeredAt:string;
  resolvedAt:string;
  resolutionNote?:string;
}
export interface AlertActionRequest{
  note?:string;
}

export interface DashBoardResponse{
  locationId:number;
  cityName:string;
  country:string;
  latitude:number;
  longitude:number;
  weatherData?:WeatherData;
  forecastData:WeatherForecast[];
  sensor:SensorResponse[];
  reading:SensorReading[];
  alerts:EnvironmentAlert[];
  generatedAt:string;
}
export interface CitySummary{
  locationId:number;
  city:string;
  country:string;
  latitude:number;
  longitude:number;
  temperature:number;
  humidity:number;
  windSpeed:number;
  weatherDescription:string;
  weatherIcon:string;
  activeSensorsCount:number;
  activeAlertCount:number;
  criticalWaterRessource:number;
}

export interface GlobalCitySummary{
  cities:CitySummary[];
  allActivesAlerts:EnvironmentAlert[];
  criticalWaterResources:WaterRessource[];
  activeSensor:SensorResponse[];
  totalActiveSensors:number;
  totalActiveAlerts:number;
  totalCriticalWaterResources:number;
  averageTemperature:number;
  averageAirQuality: number;
  averageDust: number;
  generatedAt:string;
}
export interface WaterRessourceStatus{
  barrages:WaterRessource[];
  riviere:WaterRessource[];
  lacs:WaterRessource[];
  nappe:WaterRessource[];
  barrageStats:Record<string, number>;
  nappeStats:Record<string, number>;
  lacsStats:Record<string, number>;
  riviereStats:Record<string, number>;
  totalBarrages:number;
  totalLacs:number;
  totalRiveries:number;
  totalNappe:number;
  generatedAt:string;
}
export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
}

export interface ChatRequest {
  message: string;
  cityName?: string;
  locationId?: number;
  history?: ChatMessage[];
}

export interface ChatResponse {
  reply: string;
  cityName: string;
  timestamp: string;
}
