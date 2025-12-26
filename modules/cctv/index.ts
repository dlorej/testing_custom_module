// Reexport the native module. On web, it will be resolved to cctvModule.web.ts
// and on native platforms to cctvModule.ts
export { default } from './src/cctvModule';
export { default as cctvView } from './src/cctvView';
export * from  './src/cctv.types';
