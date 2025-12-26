import { registerWebModule, NativeModule } from 'expo';

import { ChangeEventPayload } from './cctv.types';

type cctvModuleEvents = {
  onChange: (params: ChangeEventPayload) => void;
}

class cctvModule extends NativeModule<cctvModuleEvents> {
  PI = Math.PI;
  async setValueAsync(value: string): Promise<void> {
    this.emit('onChange', { value });
  }
  hello() {
    return 'Hello world! 👋';
  }
};

export default registerWebModule(cctvModule, 'cctvModule');
