import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import Pageable from './Pageable';

type PageableExampleProps = {
  itemCount: number;
  itemsPerPage: number;
};

const PageableExample = ({ itemCount, itemsPerPage }: PageableExampleProps) => {
  const items = Array.from({ length: itemCount }, (_, index) => `Element ${index + 1}`);

  return (
    <Pageable
      key={`${itemCount}-${itemsPerPage}`}
      items={items}
      itemsPerPage={itemsPerPage}
      render={(visibleItems) => (
        <ul>
          {visibleItems.map((item) => (
            <li key={item}>{item}</li>
          ))}
        </ul>
      )}
    />
  );
};

const meta = {
  title: 'Pageable/Pageable',
  component: PageableExample,
  parameters: {
    docs: {
      description: {
        component: 'Deler en liste i sider og lar brukeren navigere mellom dem.',
      },
    },
  },
  args: {
    itemCount: 24,
    itemsPerPage: 5,
  },
  argTypes: {
    itemCount: {
      control: { type: 'number', min: 1, max: 100, step: 1 },
      description: 'Antall elementer i eksempellisten.',
    },
    itemsPerPage: {
      control: { type: 'number', min: 1, max: 20, step: 1 },
      description: 'Antall elementer som vises på hver side.',
    },
  },
} satisfies Meta<typeof PageableExample>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Default: Story = {};
