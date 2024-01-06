import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterSwitchPasswordComponent } from './register-switch-password.component';

describe('RegisterSwitchPasswordComponent', () => {
  let component: RegisterSwitchPasswordComponent;
  let fixture: ComponentFixture<RegisterSwitchPasswordComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterSwitchPasswordComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterSwitchPasswordComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
