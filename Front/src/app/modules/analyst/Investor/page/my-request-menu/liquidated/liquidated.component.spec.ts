import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LiquidatedComponent } from './liquidated.component';

describe('LiquidatedComponent', () => {
  let component: LiquidatedComponent;
  let fixture: ComponentFixture<LiquidatedComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LiquidatedComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LiquidatedComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
