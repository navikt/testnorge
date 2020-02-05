import React from 'react'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import cn from 'classnames'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'

import './kategori.less'

export const Kategori = ({ title = 'Kategori', flex = true, vis, hjelpetekst, children }) => {
	const css = cn({
		'form-kategori-flex': flex
	})
	const render = (
		<div className="form-kategori">
			<h4>
				{title} {hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
			</h4>
			<div className={css}>{children}</div>
		</div>
	)

	return vis ? <Vis attributt={vis}>{render}</Vis> : render
}
