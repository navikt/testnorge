import React from 'react';
import type { Meta, StoryObj } from '@storybook/react-vite';
import { NotFoundError } from '@navikt/dolly-lib';
import LoadableComponent from './LoadableComponent';

type ExampleData = {
  message: string;
};

type LoadableExampleProps = {
  state: 'loading' | 'success' | 'notFound' | 'error';
  successMessage: string;
};

const fetchData = ({ state, successMessage }: LoadableExampleProps) => {
  if (state === 'loading') {
    return new Promise<ExampleData>(() => undefined);
  }
  if (state === 'notFound') {
    return Promise.reject(new NotFoundError());
  }
  if (state === 'error') {
    return Promise.reject(new Error('Kunne ikke hente data'));
  }
  return Promise.resolve({ message: successMessage });
};

const LoadableExample = (props: LoadableExampleProps) => (
  <LoadableComponent<ExampleData>
    key={`${props.state}-${props.successMessage}`}
    onFetch={() => fetchData(props)}
    render={(data) => <p>{data.message}</p>}
  />
);

const meta = {
  title: 'Feedback/LoadableComponent',
  component: LoadableExample,
  parameters: {
    docs: {
      description: {
        component: 'Viser lasting, innhold, ikke funnet eller feil for et asynkront kall.',
      },
    },
  },
  args: {
    state: 'success',
    successMessage: 'Dataene er lastet',
  },
  argTypes: {
    state: {
      control: 'select',
      options: ['loading', 'success', 'notFound', 'error'],
      description: 'Tilstanden det simulerte asynkrone kallet skal ende i.',
    },
    successMessage: {
      control: 'text',
      description: 'Innholdet som vises når kallet lykkes.',
    },
  },
} satisfies Meta<typeof LoadableExample>;

export default meta;

type Story = StoryObj<typeof meta>;

export const Success: Story = {};

export const Loading: Story = {
  args: {
    state: 'loading',
  },
};

export const NotFound: Story = {
  args: {
    state: 'notFound',
  },
};

export const FetchError: Story = {
  name: 'Error',
  args: {
    state: 'error',
  },
};
