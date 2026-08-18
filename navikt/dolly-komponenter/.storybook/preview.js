import '@navikt/ds-css';

/** @type { import('@storybook/react-vite').Preview } */
const preview = {
  tags: ['autodocs'],
  parameters: {
    controls: {
      matchers: {
        color: /(background|color)$/i,
        date: /Date$/,
      },
    },
  },
};

export default preview;
