import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuConteinerComponent } from './menu-conteiner.component';

describe('MenuConteinerComponent', () => {
  let component: MenuConteinerComponent;
  let fixture: ComponentFixture<MenuConteinerComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuConteinerComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuConteinerComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
