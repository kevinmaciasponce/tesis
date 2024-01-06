import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterCrearCuentaPageComponent } from './register-crear-cuenta-page.component';

describe('RegisterCrearCuentaPageComponent', () => {
  let component: RegisterCrearCuentaPageComponent;
  let fixture: ComponentFixture<RegisterCrearCuentaPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterCrearCuentaPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterCrearCuentaPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
