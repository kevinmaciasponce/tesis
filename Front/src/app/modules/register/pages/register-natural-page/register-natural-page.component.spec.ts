import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterNaturalPageComponent } from './register-natural-page.component';

describe('RegisterNaturalPageComponent', () => {
  let component: RegisterNaturalPageComponent;
  let fixture: ComponentFixture<RegisterNaturalPageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterNaturalPageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterNaturalPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
