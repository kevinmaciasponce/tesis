import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SportSponsorComponent } from './sport-sponsor.component';

describe('SportSponsorComponent', () => {
  let component: SportSponsorComponent;
  let fixture: ComponentFixture<SportSponsorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SportSponsorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SportSponsorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
