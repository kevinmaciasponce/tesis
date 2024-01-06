import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DepreciationTablesComponent } from './depreciation-tables.component';

describe('DepreciationTablesComponent', () => {
  let component: DepreciationTablesComponent;
  let fixture: ComponentFixture<DepreciationTablesComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DepreciationTablesComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DepreciationTablesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
