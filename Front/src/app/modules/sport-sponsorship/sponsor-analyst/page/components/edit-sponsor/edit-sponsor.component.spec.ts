import { ComponentFixture, TestBed } from '@angular/core/testing';

import { EditSponsorComponent } from './edit-sponsor.component';

describe('EditSponsorComponent', () => {
  let component: EditSponsorComponent;
  let fixture: ComponentFixture<EditSponsorComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ EditSponsorComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(EditSponsorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
