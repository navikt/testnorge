import { Alert, AlertProps } from '@navikt/ds-react';
import React from 'react';

const AlertWithCloseButton = ({
  children,
  variant,
  onClose,
}: {
  children?: React.ReactNode;
  onClose?: () => void;
  variant: AlertProps['variant'];
}) => {
  const [show, setShow] = React.useState(true);

  return show ? (
    <Alert
      variant={variant}
      closeButton
      onClose={() => {
        onClose?.();
        setShow(false);
      }}
    >
      {children || 'Content'}
    </Alert>
  ) : null;
};

export default AlertWithCloseButton;
