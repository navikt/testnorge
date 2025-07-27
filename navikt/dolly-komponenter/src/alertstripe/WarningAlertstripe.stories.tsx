import * as React from 'react';
import { Story, Meta } from '@storybook/react-vite';
import Comp, { WarningAlertstripeProps } from './WarningAlertstripe';

export default {
  title: 'Alertstripe/WarningAlertstripe',
  component: Comp,
} as Meta;

const Template: Story<WarningAlertstripeProps> = (args) => <Comp {...args} />;
export const WarningAlertstripe = Template.bind({});
WarningAlertstripe.args = {
  label: 'Success text',
};
