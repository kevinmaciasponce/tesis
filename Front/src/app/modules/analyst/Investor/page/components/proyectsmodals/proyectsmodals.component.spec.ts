import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ProyectsmodalsComponent } from './proyectsmodals.component';

describe('ProyectsmodalsComponent', () => {
  let component: ProyectsmodalsComponent;
  let fixture: ComponentFixture<ProyectsmodalsComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ProyectsmodalsComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ProyectsmodalsComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
