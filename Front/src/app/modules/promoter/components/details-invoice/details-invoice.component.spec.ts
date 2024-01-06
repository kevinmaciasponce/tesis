import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetailsInvoiceComponent } from './details-invoice.component';

describe('DetailsInvoiceComponent', () => {
  let component: DetailsInvoiceComponent;
  let fixture: ComponentFixture<DetailsInvoiceComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetailsInvoiceComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetailsInvoiceComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
