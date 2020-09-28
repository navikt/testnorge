import React from 'react';

import './NavigationBar.less'
import Navigation from "@/components/Navigation";

type Props = {
    navigations: Navigation[]
}

console.log(window.location.href)
const NavigationBar = ({navigations}: Props) => (
    <div className="navigation-bar">
        {navigations.map(navigation => <Navigation navigation={navigation}/>)}
    </div>
)

export default NavigationBar;