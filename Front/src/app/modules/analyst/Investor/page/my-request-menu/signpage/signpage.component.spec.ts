import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SignpageComponent } from './signpage.component';

describe('SignpageComponent', () => {
  let component: SignpageComponent;
  let fixture: ComponentFixture<SignpageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SignpageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SignpageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
