import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProcesspageComponent } from './processpage.component';

describe('ProcesspageComponent', () => {
  let component: ProcesspageComponent;
  let fixture: ComponentFixture<ProcesspageComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProcesspageComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProcesspageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
