import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ModalmasdetallesComponent } from './modalmasdetalles.component';

describe('ModalmasdetallesComponent', () => {
  let component: ModalmasdetallesComponent;
  let fixture: ComponentFixture<ModalmasdetallesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ModalmasdetallesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ModalmasdetallesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
