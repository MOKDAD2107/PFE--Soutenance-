import {Component, Input, OnInit} from '@angular/core';
import {AiAgentService} from '../../core/services/AiAgentService';
import {ChatMessage} from '../../core/models/models';
import {NgClass} from '@angular/common';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-chatbot',
  imports: [
    NgClass,
    FormsModule
  ],
  templateUrl: './chatbot.html',
  styleUrl: './chatbot.css',
})
export class Chatbot implements OnInit{
  @Input()locationId?:number;
  @Input()cityName?:string;

  isOpen=false;
  loading=false;
  input='';
  message:ChatMessage[]=[];// historique des messages
  suggestions = [
    'Comment est la météo aujourd\'hui ?',
    'Y a-t-il des alertes actives ?',
    'Quelle est la qualité de l\'air ?',
    'État des barrages ?',
  ];
  constructor(private aiService:AiAgentService) {
  }

  ngOnInit(): void {
        this.message=[{
          role : 'assistant',
          content :`Bonjour ! Je suis EcoWatch AI \n
          Je peux analyser la météo, la qualité de l'air, les ressources d'eau et les alertes environnementales.\n
          Comment puis-je vous aider ?`
        }]
    }
  sendSuggestion(text: string) {
    this.input = text;
    this.send();
  }
  send(){
    const msg=this.input.trim();
    if (!msg||this.loading)return;
    this.message.push({role:"user",content:msg});
    this.input='';
    this.loading=true;
    // Historique sans le message système initial
    const history = this.message.slice(1, -1).map(m => ({
      role: m.role,
      content: m.content
    }));

    this.aiService.chat({
      message: msg,
      cityName: this.cityName,
      locationId: this.locationId,
      history
    }).subscribe({
      next: (res) => {
        this.message.push({ role: 'assistant', content: res.reply });
        this.loading = false;
      },
      error: () => {
        this.message.push({ role: 'assistant', content: 'Désolé, une erreur est survenue. Réessayez.' });
        this.loading = false;
      }
    });
  }
}
