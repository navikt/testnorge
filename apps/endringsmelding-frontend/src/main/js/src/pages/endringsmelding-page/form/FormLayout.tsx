import styled from 'styled-components';

const mobileBreakpoint = '48rem';
const fieldPadding = 'calc(var(--ax-space-8) + var(--ax-space-2))';
const rowSpacing = 'calc(var(--ax-space-28) + var(--ax-space-2))';
const searchButtonMinWidth = 'calc(var(--ax-space-96) + var(--ax-space-48) + var(--ax-space-6))';
const searchStatusPaddingLeft = 'calc((var(--ax-space-6) + var(--ax-space-8)) / 2)';
const searchStatusPaddingBottom = 'calc((var(--ax-space-4) + var(--ax-space-6)) / 2)';

export const FormRoot = styled.form`
  display: flex;
  flex-direction: column;
`;

export const FormRow = styled.div<{ $reverse?: boolean }>`
  padding-top: ${rowSpacing};
  display: flex;
  flex-direction: ${({ $reverse = false }) => ($reverse ? 'row-reverse' : 'row')};
  align-items: flex-start;

  @media (max-width: ${mobileBreakpoint}) {
    flex-direction: column;
    padding-top: var(--ax-space-24);
  }
`;

const Field = styled.div<{ $width: string }>`
  width: ${({ $width }) => $width};
  min-width: 0;
  box-sizing: border-box;
  padding-inline-end: ${fieldPadding};

  @media (max-width: ${mobileBreakpoint}) {
    width: 100%;
    padding-inline-end: 0;

    & + & {
      padding-top: var(--ax-space-12);
    }
  }
`;

export const HalfField = styled(Field).attrs({ $width: '50%' })``;

export const QuarterField = styled(Field).attrs({ $width: '25%' })``;

export const SearchRow = styled.div`
  display: flex;
  flex-direction: row;
  align-items: flex-end;

  @media (max-width: ${mobileBreakpoint}) {
    flex-direction: column;
    align-items: stretch;
    row-gap: var(--ax-space-12);
  }
`;

export const SearchFieldSlot = styled.div`
  width: 50%;
  min-width: 0;
  box-sizing: border-box;

  @media (max-width: ${mobileBreakpoint}) {
    width: 100%;
  }
`;

export const SearchButtonSlot = styled.div`
  display: flex;
  align-items: flex-end;
  min-width: ${searchButtonMinWidth};
  margin-inline-start: var(--ax-space-20);

  @media (max-width: ${mobileBreakpoint}) {
    min-width: 0;
    margin-inline-start: 0;
  }
`;

export const SearchStatusSlot = styled.div`
  width: 25%;
  min-width: 0;
  box-sizing: border-box;
  display: flex;
  align-items: flex-end;
  padding-inline-start: ${searchStatusPaddingLeft};
  padding-bottom: ${searchStatusPaddingBottom};
  min-height: calc(var(--ax-space-20) + var(--ax-space-32));

  @media (max-width: ${mobileBreakpoint}) {
    width: 100%;
    min-height: 0;
    padding-inline-start: 0;
    padding-bottom: 0;
  }
`;

export const ActionRow = styled.div`
  padding-top: ${rowSpacing};
  display: flex;
  flex-direction: row-reverse;
  justify-content: flex-start;
  align-items: flex-start;
  flex-wrap: wrap;
  gap: var(--ax-space-20);

  @media (max-width: ${mobileBreakpoint}) {
    padding-top: var(--ax-space-24);
    gap: var(--ax-space-12);
  }
`;

export const AlertStack = styled.div`
  display: flex;
  flex-direction: column;
  row-gap: var(--ax-space-20);
  padding-top: var(--ax-space-20);
`;

export const AlertOffset = styled.div`
  padding-top: var(--ax-space-20);
`;
