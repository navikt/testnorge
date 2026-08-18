import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import HeaderLink from '../header-link/HeaderLink';
import HeaderLinkGroup from './HeaderLinkGroup';

type HeaderLinkGroupExampleProps = {
  firstLabel: string;
  secondLabel: string;
  activeLink: 'first' | 'second' | 'none';
};

const HeaderLinkGroupExample = ({
  firstLabel,
  secondLabel,
  activeLink,
}: HeaderLinkGroupExampleProps) => (
  <HeaderLinkGroup>
    <HeaderLink href="#oversikt" isActive={() => activeLink === 'first'}>
      {firstLabel}
    </HeaderLink>
    <HeaderLink href="#bestillinger" isActive={() => activeLink === 'second'}>
      {secondLabel}
    </HeaderLink>
  </HeaderLinkGroup>
);

const meta = {
  title: 'Header/HeaderLinkGroup',
  component: HeaderLinkGroupExample,
  parameters: {
    docs: {
      description: {
        component: 'Grupperer navigasjonslenker horisontalt i Dolly-headeren.',
      },
    },
  },
  args: {
    firstLabel: 'Oversikt',
    secondLabel: 'Bestillinger',
    activeLink: 'first',
  },
  argTypes: {
    firstLabel: {
      control: 'text',
      description: 'Teksten til den første lenken.',
    },
    secondLabel: {
      control: 'text',
      description: 'Teksten til den andre lenken.',
    },
    activeLink: {
      control: 'select',
      options: ['first', 'second', 'none'],
      description: 'Velger hvilken lenke som vises som aktiv.',
    },
  },
  decorators: [
    (Story) => (
      <div style={{ backgroundColor: '#3e3832', display: 'inline-flex' }}>
        <Story />
      </div>
    ),
  ],
} satisfies Meta<typeof HeaderLinkGroupExample>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
