import React from 'react';
import { useCallback } from 'react';

export const ChildrenBlur = ({ children, onBlur, ...props }) => {
  const handleBlur = useCallback(
    (e) => {
      const currentTarget = e.currentTarget;

      // Give browser time to focus the next element
      requestAnimationFrame(() => {
        // Check if the new focused element is a child of the original container
        if (!currentTarget.contains(document.activeElement)) {
          onBlur();
        }
      });
    },
    [onBlur]
  );

  return (
    <div {...props} onBlur={handleBlur}>
      {children}
    </div>
  );
};
