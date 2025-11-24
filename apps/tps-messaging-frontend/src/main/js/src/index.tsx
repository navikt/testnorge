import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import '@navikt/ds-css';
import App from './App';

async function main() {
  if (process.env.NODE_ENV === 'development') {
    const { worker } = await import('../__tests__/mocks/browser');
    await worker.start({
      onUnhandledRequest: 'bypass',
    });
    console.log('MSW initialized');
  }
}

main().then(() => {
  const root = createRoot(document.getElementById('root'));

  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>,
  );
});
