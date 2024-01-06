import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ShowSponsorComponent } from './show-sponsor.component';

describe('ShowSponsorComponent', () => {
  let component: ShowSponsorComponent;
  let fixture: ComponentFixture<ShowSponsorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ShowSponsorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ShowSponsorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
