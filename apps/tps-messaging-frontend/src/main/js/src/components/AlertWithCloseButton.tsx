import { Alert, AlertProps } from '@navikt/ds-react';
import React from 'react';

const AlertWithCloseButton = ({
  children,
  className,
  variant,
  onClose,
}: {
  children?: React.ReactNode;
  className?: string;
  onClose?: () => void;
  variant: AlertProps['variant'];
}) => {
  const [show, setShow] = React.useState(true);
  const role = variant === 'error' ? 'alert' : variant === 'info' ? undefined : 'status';

  return show ? (
    <Alert
      className={className}
      contentMaxWidth={false}
      variant={variant}
      role={role}
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
