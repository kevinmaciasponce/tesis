import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TablesaddComponent } from './tablesadd.component';

describe('TablesaddComponent', () => {
  let component: TablesaddComponent;
  let fixture: ComponentFixture<TablesaddComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TablesaddComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TablesaddComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
