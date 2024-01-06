import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterPaidInvoiceComponent } from './register-paid-invoice.component';

describe('RegisterPaidInvoiceComponent', () => {
  let component: RegisterPaidInvoiceComponent;
  let fixture: ComponentFixture<RegisterPaidInvoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterPaidInvoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterPaidInvoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
