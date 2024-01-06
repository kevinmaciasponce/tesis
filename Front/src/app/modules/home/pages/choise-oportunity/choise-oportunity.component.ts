import { Component, OnDestroy, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { ConsultasPublicasService } from 'src/app/shared/service/consultas-publicas.service';
import { ProyectosService } from 'src/app/shared/service/proyectos.service';
@Component({
  selector: 'app-choise-oportunity',
  templateUrl: './choise-oportunity.component.html',
  styleUrls: ['./choise-oportunity.component.css']
})
export class ChoiseOportunityComponent implements OnInit, OnDestroy {
  loading!: boolean;

  isChatbotVisible: boolean = false;

  public projects: any;
  suscribe:any;
  constructor(private proyectosService: ProyectosService,private router:Router,private dataApiPublics: ConsultasPublicasService) {
    this.loading = true;
   }
  ngOnDestroy(): void {
    this.suscribe.unsubscribe();
  }

  ngOnInit(): void {
    this.loading=true;
    this.suscribe=this.proyectosService.getProjects().subscribe(res => {
      this.projects = res.map((element: any) => {
       
        this.loading = false;
        return element
      });
      let data = res.sort((a:any,b:any)=>{
       return  b.fechaLimiteInversion - a.fechaLimiteInversion;
      })
      console.log(data);
      console.log(this.projects)
    }
    );
  }
  goto_register(){
    this.router.navigateByUrl('register/iniciar_sesion');
  }
  set_data(key:string,data:any){
    try{
      localStorage.setItem(key,data);
    } catch(e){
      console.log(e);
    }
  }

  filtrarRutas(rutas:any){
    let ruta:any=rutas.filter(
      (element:any)=>{
        return element.name=='img_logo'
      }
    )[0].ruta;
    return ruta;
  }
  
  goto_notify(){
    this.set_data('notify','notify');
    this.router.navigateByUrl('register/iniciar_sesion');
  }

  goto_proyects(){

    this.router.navigateByUrl('statics/proyects');
  }
  obtenernumero(numero:any):string{
    var arrayDeCadenas = numero.split(",");
   return arrayDeCadenas[0];
  }
  convert(percent:any){
    percent= percent+'';
    if(percent[0]==','){
      return 0;
    }
     let num;

    try {
      num = Number(this.obtenernumero(percent));
    } catch (error) {
      console.log(error)
      return 0;
    }
    return num;
  }
 

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



  handleViewMore(project: any) {
    localStorage.setItem('project', JSON.stringify(project));
    window.open('statics/proyects', '_blank')
  }
}
