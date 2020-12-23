import React from "react";
import { Tilbakeknapp, Nesteknapp } from "nav-frontend-ikonknapper";
import SyntaxHighlighter from "react-syntax-highlighter";
import { a11yDark as customStyle } from "react-syntax-highlighter/dist/esm/styles/hljs";

import "./CodeView.less";

type Props = {
  code?: string;
  onNext?: () => void;
  onPrevious?: () => void;
  language?: string;
};

export const CodeView = ({
  code,
  onNext,
  onPrevious,
  language = "xml",
}: Props) => {
  return (
    <div className="code-view">
      <Tilbakeknapp
        className="code-view__button"
        disabled={onPrevious == null}
        onClick={onPrevious}
      />
      <div className="code-view__code">
        <SyntaxHighlighter language={language} style={customStyle}>
          {code ? code : "Ingent resultat. Prøv å oppdater filteret."}
        </SyntaxHighlighter>
      </div>
      <Nesteknapp
        className="code-view__button"
        disabled={onNext == null}
        onClick={onNext}
      />
    </div>
  );
};
