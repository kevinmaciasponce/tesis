import { ComponentFixture, TestBed } from '@angular/core/testing';

import { HomeAnalystComponent } from './home-analyst.component';

describe('HomeAnalystComponent', () => {
  let component: HomeAnalystComponent;
  let fixture: ComponentFixture<HomeAnalystComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ HomeAnalystComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(HomeAnalystComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
