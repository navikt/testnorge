import { Alert, AlertProps } from '@navikt/ds-react';
import React from 'react';

const AlertWithCloseButton = ({
  children,
  variant,
}: {
  children?: React.ReactNode;
  variant: AlertProps['variant'];
}) => {
  const [show, setShow] = React.useState(true);

  return show ? (
    <Alert variant={variant} closeButton onClose={() => setShow(false)}>
      {children || 'Content'}
    </Alert>
  ) : null;
};

export default AlertWithCloseButton;
