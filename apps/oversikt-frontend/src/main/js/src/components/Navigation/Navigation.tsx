import React from 'react'

import './Navigation.less'
import { Link } from 'react-router-dom'

type Props<T> = {
	navigation: Navigation<T>
	className?: string
}

const Navigation = <T extends object>({ navigation, className }: Props<T>) => {
	const value = className ? className : ''

	return (
		<Link className={'navigation__button ' + value} to={navigation.href}>
			{navigation.label}
		</Link>
	)
}

export default Navigation
