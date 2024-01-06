import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RetartedStateReportComponent } from './retarted-state-report.component';

describe('RetartedStateReportComponent', () => {
  let component: RetartedStateReportComponent;
  let fixture: ComponentFixture<RetartedStateReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RetartedStateReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RetartedStateReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
