import { TestBed } from '@angular/core/testing';

import { SponsorAnalystService } from './sponsor-analyst.service';

describe('SponsorAnalystService', () => {
  let service: SponsorAnalystService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(SponsorAnalystService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
