import React from 'react';
import { Tilbakeknapp, Nesteknapp } from 'nav-frontend-ikonknapper';
import "./CodeView.less"

type Props = {
    code?: string,
    onNext?: () => void,
    onPrevious?: () => void
}

export const CodeView = ({ code, onNext, onPrevious } : Props) => {
    return (
        <div className="code-view">
            <Tilbakeknapp disabled={onPrevious == null} onClick={onPrevious}/>
            <div className="code-view__code">
                {code ? code : "No content."}
            </div>
            <Nesteknapp disabled={onNext == null} onClick={onNext}/>
        </div>
    )
}