import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterForgotPasswordComponent } from './register-forgot-password.component';

describe('RegisterForgotPasswordComponent', () => {
  let component: RegisterForgotPasswordComponent;
  let fixture: ComponentFixture<RegisterForgotPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterForgotPasswordComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterForgotPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
