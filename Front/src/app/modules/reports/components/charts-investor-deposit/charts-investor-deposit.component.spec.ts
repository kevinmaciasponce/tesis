import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartsInvestorDepositComponent } from './charts-investor-deposit.component';

describe('ChartsInvestorDepositComponent', () => {
  let component: ChartsInvestorDepositComponent;
  let fixture: ComponentFixture<ChartsInvestorDepositComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartsInvestorDepositComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartsInvestorDepositComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
