import React from 'react';
import './Frame.css';

export const Frame = ({ renderLeft, renderRight }) => (
    <div className="frame">
        <h1>Testnorge oversikt</h1>
        <div className="container">
                <div className="container--left">
                        {renderLeft()}
                </div>
                <div className="container--right">
                        {renderRight()}
                </div>
        </div>
    </div>
)