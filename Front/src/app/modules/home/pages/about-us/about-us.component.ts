import { Component, OnInit } from '@angular/core';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';

@Component({
  selector: 'app-about-us',
  templateUrl: './about-us.component.html',
  styleUrls: ['./about-us.component.css']
})
export class AboutUsComponent implements OnInit {
  showChat:Boolean = true


  constructor(private dataApiPublics: ConsultasPublicasService) { }

  ngOnInit(): void {
  }



  toggleState(dato:boolean){
    return !dato;
  }



  isChatbotVisible: boolean = false;

  toggleChatbot() {
    this.isChatbotVisible = !this.isChatbotVisible;
  }
  userInput: string = '';
  messages: { 
    text: string; 
    sender: 'user' | 'chatbot' }[] = [];
    sendter:any
response:any


  sendMessage() {
   if (this.userInput.trim() !== '') {
     // Agregar mensaje del usuario al arreglo de mensajes
     this.sendter='user';
     this.messages.push({ text: this.userInput, sender: 'user' });
     
     const formData = new FormData();
     formData.append("message", this.userInput)
     this.dataApiPublics.chatbot(formData).subscribe((data:any)=>{
       this.response=data.mensaje
       this.messages.push({ text: data.mensaje, sender: 'chatbot' });
       this.sendter='chatbot'
       console.log(data)
     }),(error:Error)=>{
  
     },()=>{
       this.messages.push({ text: this.response, sender: 'chatbot' });
     }
  
  
     // Aquí puedes enviar el mensaje al chatbot y recibir una respuesta
     // Luego, agrega la respuesta del chatbot al arreglo de mensajes
     // Ejemplo: this.messages.push({ text: response, sender: 'chatbot' });
  
     // Limpiar el campo de entrada de texto
     this.userInput = '';
   }
  }

}
