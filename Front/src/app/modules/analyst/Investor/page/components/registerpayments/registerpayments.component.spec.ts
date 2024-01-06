import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterpaymentsComponent } from './registerpayments.component';

describe('RegisterpaymentsComponent', () => {
  let component: RegisterpaymentsComponent;
  let fixture: ComponentFixture<RegisterpaymentsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterpaymentsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterpaymentsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
