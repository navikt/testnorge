import React from 'react';
import { createRoot } from 'react-dom/client';
import { Provider } from '@navikt/ds-react';
import { nb } from '@navikt/ds-react/locales';
import './index.css';
import App from './App';

const rootElement = document.getElementById('root');

if (!rootElement) {
  throw new Error('Fant ikke root-elementet');
}

const root = createRoot(rootElement);

root.render(
  <React.StrictMode>
    <Provider locale={nb}>
      <App />
    </Provider>
  </React.StrictMode>
);
