import { NativeModule, requireNativeModule } from 'expo';

import { cctvModuleEvents } from './cctv.types';

declare class cctvModule extends NativeModule<cctvModuleEvents> {
  PI: number;
  hello(): string;
  setValueAsync(value: string): Promise<void>;
}

// This call loads the native module object from the JSI.
export default requireNativeModule<cctvModule>('cctv');
