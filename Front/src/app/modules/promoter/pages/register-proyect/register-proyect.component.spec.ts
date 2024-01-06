import { ComponentFixture, TestBed } from '@angular/core/testing';

import { RegisterProyectComponent } from './register-proyect.component';

describe('RegisterProyectComponent', () => {
  let component: RegisterProyectComponent;
  let fixture: ComponentFixture<RegisterProyectComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ RegisterProyectComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RegisterProyectComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
