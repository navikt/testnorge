import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import Comp, { ProfilProps } from './Profil';

export default {
  title: 'Profil/Profil',
  component: Comp,
} as Meta;

const Template: Story<ProfilProps> = (args) => <Comp {...args} />;
export const Profil = Template.bind({});
Profil.args = {
  url: null,
  visningsnavn: 'Jon Smith',
};
Profil.parameters = {
  backgrounds: { default: 'dark' },
};
