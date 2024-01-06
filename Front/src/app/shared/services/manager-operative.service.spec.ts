import { TestBed } from '@angular/core/testing';

import { ManagerOperativeService } from './manager-operative.service';

describe('ManagerOperativeService', () => {
  let service: ManagerOperativeService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ManagerOperativeService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
