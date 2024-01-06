import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { User } from 'src/app/models/user';

import { DataApiClientService } from 'src/app/shared/service/data-api-client.service';
import { StorageService } from 'src/app/shared/service/storage.service';

@Component({
  selector: 'app-user-menu',
  templateUrl: './user-menu.component.html',
  styleUrls: ['./user-menu.component.css']
})
export class UserMenuComponent implements OnInit {
  
  val:string | undefined;
  user:User | undefined;
  registrationForm!: FormGroup;

  constructor(private storage:StorageService, private formBuilder: FormBuilder, private dataApiClient: DataApiClientService,) {
    this.user=this.storage.getCurrentSession();
   
  }

 
  ngOnInit(): void {
    this.registrationForm= this.formBuilder.group({
      identificacion: this.val,
    });
    this.user=this.storage.getCurrentSession();
    this.val=this.user?.user.usuarioInterno;
    console.log(this.val);
    console.log(this.user);
  }
  cerrarsession(){
    this.storage.removeCurrentSession();
  }



}
