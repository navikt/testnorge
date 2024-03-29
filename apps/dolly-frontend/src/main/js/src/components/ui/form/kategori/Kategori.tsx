import cn from 'classnames'
import { Vis } from '@/components/bestillingsveileder/VisAttributt'

import './kategori.less'
import { Hjelpetekst } from '@/components/hjelpetekst/Hjelpetekst'

export const Kategori = ({
	title = 'Kategori',
	flex = true,
	vis = null,
	hjelpetekst = null,
	flexRow = null,
	children,
}) => {
	const css = flexRow
		? cn({
				'form-kategori-flex-row': flex,
			})
		: cn({
				'form-kategori-flex': flex,
			})

	const render = (
		<div className="form-kategori">
			{title && (
				<h4>
					{title} {hjelpetekst && <Hjelpetekst>{hjelpetekst}</Hjelpetekst>}
				</h4>
			)}
			<div className={css}>{children}</div>
		</div>
	)

	return vis ? <Vis attributt={vis}>{render}</Vis> : render
}
