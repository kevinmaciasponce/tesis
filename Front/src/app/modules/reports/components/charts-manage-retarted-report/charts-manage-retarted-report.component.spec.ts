import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ChartsManageRetartedReportComponent } from './charts-manage-retarted-report.component';

describe('ChartsManageRetartedReportComponent', () => {
  let component: ChartsManageRetartedReportComponent;
  let fixture: ComponentFixture<ChartsManageRetartedReportComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ChartsManageRetartedReportComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ChartsManageRetartedReportComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
