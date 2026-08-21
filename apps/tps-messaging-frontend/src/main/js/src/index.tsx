import React from 'react';
import { createRoot } from 'react-dom/client';
import './index.css';
import '@navikt/ds-css';
import App from './App';

async function main() {
  if (import.meta.env.DEV) {
    const { worker } = await import('../__tests__/mocks/browser');
    await worker.start({
      onUnhandledRequest: 'bypass',
    });
    console.log('MSW initialized');
  }
}

main().then(() => {
  const rootElement = document.getElementById('root');

  if (!rootElement) {
    throw new Error('Fant ikke root-elementet');
  }

  const root = createRoot(rootElement);

  root.render(
    <React.StrictMode>
      <App />
    </React.StrictMode>,
  );
});
