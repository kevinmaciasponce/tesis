import { ComponentFixture, TestBed } from '@angular/core/testing';

import { LoadfilesModalComponent } from './loadfiles-modal.component';

describe('LoadfilesModalComponent', () => {
  let component: LoadfilesModalComponent;
  let fixture: ComponentFixture<LoadfilesModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ LoadfilesModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(LoadfilesModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
