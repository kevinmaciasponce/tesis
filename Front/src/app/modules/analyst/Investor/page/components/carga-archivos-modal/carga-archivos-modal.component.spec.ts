import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CargaArchivosModalComponent } from './carga-archivos-modal.component';

describe('CargaArchivosModalComponent', () => {
  let component: CargaArchivosModalComponent;
  let fixture: ComponentFixture<CargaArchivosModalComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ CargaArchivosModalComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(CargaArchivosModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
