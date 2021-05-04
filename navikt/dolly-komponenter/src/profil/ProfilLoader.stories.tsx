import * as React from 'react';
import { Story, Meta } from '@storybook/react';
import Comp, { ProfilLoaderProps } from './ProfilLoader';

export default {
  title: 'Profil/ProfilLoader',
  component: Comp,
} as Meta;

const Template: Story<ProfilLoaderProps> = (args) => <Comp {...args} />;
export const ProfilLoader = Template.bind({});
ProfilLoader.args = {
  fetchProfil: () =>
    new Promise((resolve) => {
      setTimeout(() => {
        resolve({ visningsNavn: 'Jon Smith' });
      }, 1000);
    }),
  fetchBilde: () =>
    new Promise((resolve, reject) => {
      setTimeout(() => {
        reject();
      }, 1000);
    }),
};
ProfilLoader.parameters = {
  backgrounds: { default: 'dark' },
};
