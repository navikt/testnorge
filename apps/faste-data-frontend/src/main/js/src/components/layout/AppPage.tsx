import type { ReactNode } from 'react';
import { Page } from '@navikt/ds-react';
import styled from 'styled-components';

const Content = styled.div`
  width: 100%;
  min-width: 0;
  margin-inline: auto;
  padding-block: var(--ax-space-48) var(--ax-space-56);

  @media (min-width: 1280px) {
    max-width: 50vw;
  }
`;

export const SectionStack = styled.div`
  display: flex;
  flex-direction: column;
  gap: var(--ax-space-32);
  width: 100%;
  min-width: 0;
`;

export const FormStack = styled.div`
  display: flex;
  flex-direction: column;
  gap: var(--ax-space-24);
  width: 100%;
`;

export const FormRow = styled.div`
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: var(--ax-space-12);
  align-items: end;

  @media (max-width: 1023px) {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  @media (max-width: 767px) {
    grid-template-columns: 1fr;
  }
`;

export const QuarterField = styled.div`
  min-width: 0;
`;

export const HalfField = styled.div`
  min-width: 0;
  grid-column: span 2;

  @media (max-width: 767px) {
    grid-column: auto;
  }
`;

export const ButtonField = styled.div`
  display: flex;
  align-items: end;

  @media (max-width: 767px) {
    width: 100%;

    > * {
      width: 100%;
    }
  }
`;

export const SearchRow = styled.div`
  display: grid;
  grid-template-columns: minmax(0, 3fr) minmax(9rem, 1fr);
  gap: var(--ax-space-12);
  align-items: end;

  @media (max-width: 767px) {
    grid-template-columns: 1fr;
  }
`;

export const ResultsStack = styled.div`
  display: flex;
  flex-direction: column;
  gap: var(--ax-space-16);
  width: 100%;
  min-width: 0;
`;

export const PaginationRow = styled.div`
  display: flex;
  justify-content: flex-start;
`;

export const ResponsiveSplit = styled.div`
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: var(--ax-space-16);
  align-items: start;
  width: 100%;
  min-width: 0;

  @media (max-width: 767px) {
    grid-template-columns: 1fr;
  }
`;

export const ScrollArea = styled.div`
  width: 100%;
  min-width: 0;
  overflow-x: auto;
`;

type Props = {
  children: ReactNode;
};

const AppPage = ({ children }: Props) => (
  <Page contentBlockPadding="none">
    <Page.Block as="main" gutters>
      <Content>{children}</Content>
    </Page.Block>
  </Page>
);

export default AppPage;
