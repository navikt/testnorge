import React from 'react';
import SidebarLink from './SidebarLink'
import './sidebar.css';


export default class Sidebar extends React.Component {

    render() {

        return (
            <div id="sidebar">
                <h2>This is a sidebar</h2>
                <SidebarLink path="/">
                    This is a sidebar-link
                </SidebarLink>
                <div>
                    This is element2
                </div>
                <div>
                    This is element3
                </div>

            </div>
        )
    }
}

