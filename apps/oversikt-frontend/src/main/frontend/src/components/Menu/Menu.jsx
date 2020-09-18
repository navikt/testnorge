import React from 'react';
import './Menu.css'

export const Menu = ({items}) => (
    <ul className="menu-list">
        {items.map(item => <li key={item} ><a href={`/app/${item}`}>{item}</a></li>)}
    </ul>
)

