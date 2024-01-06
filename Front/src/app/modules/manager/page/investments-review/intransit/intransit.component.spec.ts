import { ComponentFixture, TestBed } from '@angular/core/testing';

import { IntransitComponent } from './intransit.component';

describe('IntransitComponent', () => {
  let component: IntransitComponent;
  let fixture: ComponentFixture<IntransitComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ IntransitComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(IntransitComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
