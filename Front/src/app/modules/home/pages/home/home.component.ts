import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent implements OnInit {
  loading:boolean=false;

  shwolets:boolean =false
  showq:boolean =true

  shwoRe:boolean =false
  constructor(private router: Router ,private dataApiPublics: ConsultasPublicasService) {
    this.loading=true;
   }

   mostrar(){
    this.shwolets=true
    this.showq=false
   }
   mostrarR(){
    this.shwoRe=true
    this.showq=false
   }
  regresar(){
    this.shwolets=false
    this.shwoRe=false
    this.showq=true
   }

  ngOnInit(): void {
  }
  goto_register(){
    this.router.navigateByUrl('register');
  }


  financiamiento(){
    this.set_data('financiamiento','financiamiento');
    this.router.navigateByUrl('register/crear-cuenta/juridica');
}

set_data(key:string,data:string){
  try{
    localStorage.setItem(key,data);
  } catch(e){
    console.log(e);
  }
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
