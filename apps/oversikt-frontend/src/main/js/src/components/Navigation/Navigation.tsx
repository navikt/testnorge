import React from 'react';

import './Navigation.less'
import {Link} from "react-router-dom";

type Props = {
    navigation: Navigation,
    className?: string
}

const Navigation = ({navigation, className}: Props) => {

    const value = className ? className : "";

    return (
    <Link className={"navigation__button " + value} to={navigation.href}>
        {navigation.label}
    </Link>
)}

export default Navigation;