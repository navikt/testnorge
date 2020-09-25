import React from 'react';

import './PageWithMenu.less'
import Navigation from "@/components/Navigation";

type Props = {
    title: string,
    children: React.ReactNode,
    navigations: Navigation[],
    menuTitle: string
}

const PageWithMenu = ({title, children, navigations, menuTitle}: Props) => (
    <div className="page-with-menu">
        <div className="container--left">
            <h3>{menuTitle}</h3>

            <ul>
                {navigations.map(navigation =>  <li key={navigation.label} ><Navigation navigation={navigation}/></li>)}
            </ul>
        </div>
        <div className="container--right">
            <h1>{title}</h1>
            {children}
        </div>
    </div>
)

export default PageWithMenu;