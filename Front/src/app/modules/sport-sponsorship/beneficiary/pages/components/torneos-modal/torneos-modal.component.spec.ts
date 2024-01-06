import { ComponentFixture, TestBed } from '@angular/core/testing';

import { TorneosModalComponent } from './torneos-modal.component';

describe('TorneosModalComponent', () => {
  let component: TorneosModalComponent;
  let fixture: ComponentFixture<TorneosModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ TorneosModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TorneosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
