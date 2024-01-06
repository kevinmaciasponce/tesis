import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuManagerOperativeComponent } from './menu-manager-operative.component';

describe('MenuManagerOperativeComponent', () => {
  let component: MenuManagerOperativeComponent;
  let fixture: ComponentFixture<MenuManagerOperativeComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuManagerOperativeComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuManagerOperativeComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
