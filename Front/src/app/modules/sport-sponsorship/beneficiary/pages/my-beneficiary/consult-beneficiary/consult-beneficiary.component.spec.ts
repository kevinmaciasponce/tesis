import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConsultBeneficiaryComponent } from './consult-beneficiary.component';

describe('ConsultBeneficiaryComponent', () => {
  let component: ConsultBeneficiaryComponent;
  let fixture: ComponentFixture<ConsultBeneficiaryComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConsultBeneficiaryComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConsultBeneficiaryComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
