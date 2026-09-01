import React from 'react'

import './Navigation.less'
import { Button } from '@navikt/ds-react'
import { Link, useLocation } from 'react-router'
import type { AppNavigationItem } from './types'

type Props<T> = {
	navigation: AppNavigationItem<T>
	className?: string
}

const Navigation = <T extends unknown>({ navigation, className }: Props<T>) => {
	const location = useLocation()
	const value = className ? className : ''
	const isActive = location.pathname === navigation.href

	return (
		<Button
			as={Link}
			aria-current={isActive ? 'page' : undefined}
			className={'navigation__button ' + value}
			data-color="neutral"
			size="small"
			to={navigation.href}
			variant="secondary"
		>
			{navigation.label}
		</Button>
	)
}

export default Navigation
