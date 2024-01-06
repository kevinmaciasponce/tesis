import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InconfirmationComponent } from './inconfirmation.component';

describe('InconfirmationComponent', () => {
  let component: InconfirmationComponent;
  let fixture: ComponentFixture<InconfirmationComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ InconfirmationComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(InconfirmationComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
