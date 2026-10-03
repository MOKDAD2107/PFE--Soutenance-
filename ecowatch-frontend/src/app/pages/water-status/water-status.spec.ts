import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WaterStatus } from './water-status';

describe('WaterStatus', () => {
  let component: WaterStatus;
  let fixture: ComponentFixture<WaterStatus>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [WaterStatus]
    })
    .compileComponents();

    fixture = TestBed.createComponent(WaterStatus);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
