import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AcountModalComponent } from './acount-modal.component';

describe('AcountModalComponent', () => {
  let component: AcountModalComponent;
  let fixture: ComponentFixture<AcountModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AcountModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AcountModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
