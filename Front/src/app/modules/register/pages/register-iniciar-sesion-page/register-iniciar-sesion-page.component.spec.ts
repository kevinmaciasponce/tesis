import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterIniciarSesionPageComponent } from './register-iniciar-sesion-page.component';

describe('RegisterIniciarSesionPageComponent', () => {
  let component: RegisterIniciarSesionPageComponent;
  let fixture: ComponentFixture<RegisterIniciarSesionPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterIniciarSesionPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterIniciarSesionPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
