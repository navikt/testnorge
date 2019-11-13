import React from 'react'
import { getAttributterSortert } from '~/service/attributter/Attributter'
import { AttributtPaneler } from './AttributtPaneler'
import { Utvalg } from './utvalg/Utvalg'

import './AttributtVelger.less'

export const AttributtVelger = ({ valgteAttributter, checkAttributter }) => {
	const attributter = getAttributterSortert()
	return (
		<div className="attributt-velger">
			<div className="flexbox">
				<AttributtPaneler
					attributter={attributter}
					valgteAttributter={valgteAttributter}
					checkAttributter={checkAttributter}
				/>
				<Utvalg valgteAttributter={valgteAttributter} checkAttributter={checkAttributter} />
			</div>
		</div>
	)
}
