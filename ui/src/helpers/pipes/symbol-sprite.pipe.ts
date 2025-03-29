import { Pipe, PipeTransform } from '@angular/core';
import {SpriteIdsType} from '../../sprites-ids.type';
import {svgSymbolSpritePath} from '../../svg-symbol-sprite-path';

@Pipe({
  name: 'symbolSprite',
  standalone: true,
})
export class SymbolSpritePipe implements PipeTransform {
  transform(id: SpriteIdsType): string {
    return `${svgSymbolSpritePath}${id}`;
  }
}
