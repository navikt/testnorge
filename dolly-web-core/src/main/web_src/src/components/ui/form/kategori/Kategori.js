import React from 'react'
import HjelpeTekst from 'nav-frontend-hjelpetekst'

import './kategori.less'

export const Kategori = ({ title = 'Kategori', hjelpetekst, children }) => {
	return (
		<div className="form-kategori">
			<h4>
				{title} {hjelpetekst && <HjelpeTekst>{hjelpetekst}</HjelpeTekst>}
			</h4>
			<div className="flexbox flexbox--wrap">{children}</div>
		</div>
	)
}
