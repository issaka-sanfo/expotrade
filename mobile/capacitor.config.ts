import type { CapacitorConfig } from '@capacitor/cli';

const config: CapacitorConfig = {
  appId: 'com.expotrade.app',
  appName: 'ExpoTrade',
  webDir: 'www/browser',
  server: {
    androidScheme: 'https'
  },
  plugins: {
    StatusBar: {
      style: 'DARK'
    },
    Keyboard: {
      resize: 'body'
    }
  }
};

export default config;
