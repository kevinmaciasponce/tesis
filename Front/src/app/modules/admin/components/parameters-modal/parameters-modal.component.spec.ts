import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ParametersModalComponent } from './parameters-modal.component';

describe('ParametersModalComponent', () => {
  let component: ParametersModalComponent;
  let fixture: ComponentFixture<ParametersModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ParametersModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ParametersModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
