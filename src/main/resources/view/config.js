import {GraphicEntityModule} from './entity-module/GraphicEntityModule.js';
import {AnimatedEventModule} from './assets/AnimatedEventModule.js';
import {TooltipModule} from './tooltip-module/TooltipModule.js';
import { EndScreenModule } from './endscreen-module/EndScreenModule.js';

export const modules = [
    GraphicEntityModule,
    AnimatedEventModule,
    TooltipModule,
    EndScreenModule
];
export const playerColors = [
    '#6ac371', // mantis green
    '#3ac5ca', // scooter blue
    '#9975e2', // medium purple
    '#de6ddf', // lavender pink
    '#ff1d5c', // radical red
    '#22a1e4', // curious blue
    '#ff8f16', // west side orange
    '#ff0000'  // solid red
];