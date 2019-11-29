import React from 'react'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'

export const Aliaser = ({ aliaser }) => {
	if (!aliaser || aliaser.length === 0) return null

	return (
		<div>
			<h4>Aliaser</h4>
			{aliaser.map((alias, idx) => (
				<div key={idx} className="person-visning_content">
					<TitleValue title="" value={`#${idx + 1}`} size="x-small" />
					<TitleValue title="FNR/DNR" value={alias.fnr} />
					<TitleValue title="Fornavn" value={alias.navn.fornavn} />
					<TitleValue title="Mellomnavn" value={alias.navn.mellomnavn} />
					<TitleValue title="Etternavn" value={alias.navn.etternavn} />
				</div>
			))}
		</div>
	)
}
