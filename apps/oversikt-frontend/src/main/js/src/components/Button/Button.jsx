import React from 'react';
import {Button as MUButton} from "@material-ui/core";
import './Button.css'

export const Button = props => (
    <div className="button">
        <MUButton variant="contained" {...props}/>
    </div>
)