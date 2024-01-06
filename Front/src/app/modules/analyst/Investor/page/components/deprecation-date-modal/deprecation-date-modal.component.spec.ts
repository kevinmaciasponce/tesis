import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DeprecationDateModalComponent } from './deprecation-date-modal.component';

describe('DeprecationDateModalComponent', () => {
  let component: DeprecationDateModalComponent;
  let fixture: ComponentFixture<DeprecationDateModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DeprecationDateModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DeprecationDateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
