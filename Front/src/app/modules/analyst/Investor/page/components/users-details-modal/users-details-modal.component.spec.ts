import { ComponentFixture, TestBed } from '@angular/core/testing';

import { UsersDetailsModalComponent } from './users-details-modal.component';

describe('UsersDetailsModalComponent', () => {
  let component: UsersDetailsModalComponent;
  let fixture: ComponentFixture<UsersDetailsModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ UsersDetailsModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(UsersDetailsModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
