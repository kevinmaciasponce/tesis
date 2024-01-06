import { TestBed } from '@angular/core/testing';

import { UrlMenuService } from './url-menu.service';

describe('UrlMenuService', () => {
  let service: UrlMenuService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UrlMenuService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
