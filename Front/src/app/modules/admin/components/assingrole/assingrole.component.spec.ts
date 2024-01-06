import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AssingroleComponent } from './assingrole.component';

describe('AssingroleComponent', () => {
  let component: AssingroleComponent;
  let fixture: ComponentFixture<AssingroleComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ AssingroleComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(AssingroleComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
