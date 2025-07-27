import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import Comp, { ErrorAlertstripeProps } from './ErrorAlertstripe';

export default {
  title: 'Alertstripe/ErrorAlertstripe',
  component: Comp,
} as Meta;

const Template: Story<ErrorAlertstripeProps> = (args) => <Comp {...args} />;
export const ErrorAlertstripe = Template.bind({});
ErrorAlertstripe.args = {
  label: 'Error text',
};
