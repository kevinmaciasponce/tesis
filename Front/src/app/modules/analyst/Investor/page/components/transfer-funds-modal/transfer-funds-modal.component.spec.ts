import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferFundsModalComponent } from './transfer-funds-modal.component';

describe('TransferFundsModalComponent', () => {
  let component: TransferFundsModalComponent;
  let fixture: ComponentFixture<TransferFundsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransferFundsModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransferFundsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
