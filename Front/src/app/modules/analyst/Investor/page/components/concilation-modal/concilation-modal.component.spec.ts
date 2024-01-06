import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ConcilationModalComponent } from './concilation-modal.component';

describe('ConcilationModalComponent', () => {
  let component: ConcilationModalComponent;
  let fixture: ComponentFixture<ConcilationModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ConcilationModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ConcilationModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
