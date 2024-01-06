import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ActivationCuentaComponent } from './activation-cuenta.component';

describe('ActivationCuentaComponent', () => {
  let component: ActivationCuentaComponent;
  let fixture: ComponentFixture<ActivationCuentaComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ActivationCuentaComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ActivationCuentaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
