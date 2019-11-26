import React from 'react'
import HjelpeTekst from 'nav-frontend-hjelpetekst'
import { Vis } from '~/components/bestillingsveileder/VisAttributt'

import './kategori.less'

export const Kategori = ({ title = 'Kategori', vis, hjelpetekst, children }) => {
	const render = (
		<div className="form-kategori">
			<h4>
				{title} {hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
			</h4>
			<div className="flexbox flexbox--wrap">{children}</div>
		</div>
	)

	return vis ? <Vis attributt={vis}>{render}</Vis> : render
}
