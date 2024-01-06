import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PdfinprocessComponent } from './pdfinprocess.component';

describe('PdfinprocessComponent', () => {
  let component: PdfinprocessComponent;
  let fixture: ComponentFixture<PdfinprocessComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PdfinprocessComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PdfinprocessComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
