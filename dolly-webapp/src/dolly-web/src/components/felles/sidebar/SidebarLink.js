import React from 'react';
import {NavLink} from 'react-router-dom';

const SidebarLink = ({path, children}) => {
    return (
        <NavLink exact to={path} activeClassName="">
            {children}
        </NavLink>
    )
};

export default SidebarLink;
