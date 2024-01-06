import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TransferDetailsModalComponent } from './transfer-details-modal.component';

describe('TransferDetailsModalComponent', () => {
  let component: TransferDetailsModalComponent;
  let fixture: ComponentFixture<TransferDetailsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TransferDetailsModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TransferDetailsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
