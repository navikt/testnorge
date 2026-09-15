import React from 'react';
import './index.css';
import App from './App';
import { createRoot } from 'react-dom/client';

const container = document.getElementById('root');

if (!container) {
  throw new Error('Fant ikke root-elementet');
}

const root = createRoot(container);

root.render(<App />);
