import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MenuInvestorComponent } from './menu-investor.component';

describe('MenuInvestorComponent', () => {
  let component: MenuInvestorComponent;
  let fixture: ComponentFixture<MenuInvestorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ MenuInvestorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(MenuInvestorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
