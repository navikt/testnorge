import React from "react";
import './Page.css'

export const Page = ({title, paragraph, children}) => (
    <div className="page">
        <h2>{title}</h2>
        <p>{paragraph}</p>
        {children}
    </div>
)