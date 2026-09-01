import React, { useState } from 'react'
import { TextField } from '@navikt/ds-react'

import Navigation from '@/components/Navigation'
import type { AppNavigationItem } from '@/components/Navigation'
import './PageWithMenu.less'
import { Page } from '@/pages/Page'

type Props = {
	children: React.ReactNode
	navigations: AppNavigationItem<unknown>[]
	menuTitle: string
}

export default ({ children, navigations, menuTitle }: Props) => {
	const [search, setSearch] = useState('')

	return (
		<Page>
			<div className="page-with-menu">
				<aside className="page-with-menu__menu">
					<TextField
						autoFocus
						className="page-with-menu__search"
						label="Søk etter applikasjon"
						size="small"
						value={search}
						onChange={(event) => setSearch(event.target.value)}
					/>
					<h2 className="page-with-menu__title">{menuTitle}</h2>
					<ul className="page-with-menu__list">
						{navigations
							.filter((name) => name.label.includes(search))
							.sort((first, second) => first.label.localeCompare(second.label))
							.map((navigation) => (
								<li className="page-with-menu__item" key={navigation.label}>
									<Navigation navigation={navigation} />
								</li>
							))}
					</ul>
				</aside>
				<section className="page-with-menu__content">{children}</section>
			</div>
		</Page>
	)
}
