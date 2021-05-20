import React, { ReactNode } from "react";
import SyntaxHighlighter from "react-syntax-highlighter";
import { a11yDark as customStyle } from "react-syntax-highlighter/dist/esm/styles/hljs";

import Alertstripe from "nav-frontend-alertstriper";

import "./CodeView.less";

type Props = {
  code?: string;
  onNext?: () => void;
  onPrevious?: () => void;
  language?: string;
  children: ReactNode;
};

export const CodeView = ({
  code,
  onNext,
  onPrevious,
  language = "xml",
  children,
}: Props) => {
  // @ts-ignore
  window.onkeydown = (e) => {
    if (e.key === "ArrowLeft") {
      onPrevious();
    } else if (e.key === "ArrowRight") {
      onNext();
    }
  };

  return (
    <div className="code-view">
      {children}
      <div className="code-view__code">
        {!code ? (
          <div className="code-view__alert">
            <Alertstripe type="advarsel">
              Fant ingen resulat. Prøv å endre filteret.
            </Alertstripe>
          </div>
        ) : (
          <SyntaxHighlighter language={language} style={customStyle}>
            {code}
          </SyntaxHighlighter>
        )}
      </div>
    </div>
  );
};
