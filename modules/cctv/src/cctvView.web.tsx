import * as React from 'react';

import { cctvViewProps } from './cctv.types';

export default function cctvView(props: cctvViewProps) {
  return (
    <div>
      <iframe
        style={{ flex: 1 }}
        src={props.url}
        onLoad={() => props.onLoad({ nativeEvent: { url: props.url } })}
      />
    </div>
  );
}
