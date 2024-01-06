import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterSponsorComponent } from './register-sponsor.component';

describe('RegisterSponsorComponent', () => {
  let component: RegisterSponsorComponent;
  let fixture: ComponentFixture<RegisterSponsorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterSponsorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterSponsorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
