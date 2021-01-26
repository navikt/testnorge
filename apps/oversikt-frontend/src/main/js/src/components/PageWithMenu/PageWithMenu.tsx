import React, {useState} from 'react';

import './PageWithMenu.less'
import Navigation from "@/components/Navigation";

type Props = {
    title: string,
    children: React.ReactNode,
    navigations: Navigation[],
    menuTitle: string
}

const PageWithMenu = ({title, children, navigations, menuTitle}: Props) => {

    const [search, setSearch] = useState("")

    return (
        <div className="page-with-menu">
            <div className="container--left">
                <h4>Søk etter applikasjon</h4>
                <input type="text" className="search" onChange={event => setSearch(event.target.value)}/>

                <h3>{menuTitle}</h3>

                <ul>
                    {navigations
                        .filter(name => name.label.includes(search))
                        .map(navigation => <li key={navigation.label}><Navigation navigation={navigation}/></li>)}
                </ul>
            </div>
            <div className="container--right">
                <h1>{title}</h1>
                {children}
            </div>
        </div>
    );
}

export default PageWithMenu;