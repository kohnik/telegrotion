import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';
import {BrainRingService} from '../brain-ring.service';
import {SymbolSpritePipe} from '../../../shared/pipes/symbol-sprite.pipe';
import {LoaderComponent} from '../../../shared/components/loader/loader.component';
import {getLocalStorageUserData, setLocalStorageUserData} from '../utils';
import {BrainRingLogoComponent} from '../brain-ring-logo/brain-ring-logo.component';
import {EGameType} from "../../../shared/interfaces";

@Component({
  selector: 'app-brain-ring-welcome-page',
  imports: [
    SymbolSpritePipe,
    LoaderComponent,
    BrainRingLogoComponent
  ],
  templateUrl: './brain-ring-welcome-page.component.html',
  styleUrl: './brain-ring-welcome-page.component.scss',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BrainRingService],
})
export class BrainRingWelcomePageComponent implements OnInit{

  public isLoading = false;

  constructor(private readonly router: Router, private readonly brainRingService: BrainRingService,private cdr: ChangeDetectorRef) { }

  ngOnInit() {
    let userLocalData = getLocalStorageUserData();
    if(userLocalData) {
      // this.brainRingService.checkPlayerExists(
      //   {
      //     playerId: userLocalData.playerId,
      //     roomId: userLocalData.roomId,
      //   }
      // ).subscribe(data => {
      //   if(data.exists) {
      //     this.router.navigate(['/brain-ring-controller'], {
      //       queryParams: {
      //         roomId: data.roomId,
      //         playerId: data.playerId
      //       }
      //     });
      //   }
      //
      //   this.cdr.markForCheck();
      // })
    }
  }

  joinToRoom(): void {
    this.router.navigate(['/brain-ring-join']);
  }

  createRoom(): void {
    this.isLoading = true
    this.brainRingService.startBrainRing().subscribe((data)=> {
      console.log(data);
      this.router.navigate(['/brain-ring-lobby'], {
        queryParams: {
          roomId: data.roomId,
          joinCode: data.joinCode,
        }
      });

      this.isLoading = false;
    })
  }

  public goToMainPage(): void {
    this.router.navigate(['/']);
  }

  protected readonly EGameType = EGameType;
}
