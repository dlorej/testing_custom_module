import { requireNativeView } from 'expo';
import * as React from 'react';

import { cctvViewProps } from './cctv.types';

const NativeView: React.ComponentType<cctvViewProps> =
  requireNativeView('cctv');

export default function cctvView(props: cctvViewProps) {
  return <NativeView {...props} />;
}
