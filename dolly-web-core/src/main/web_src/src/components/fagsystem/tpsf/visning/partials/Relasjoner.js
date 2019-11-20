import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { relasjonTranslator } from '~/service/dataMapper/Utils'

import { Identhistorikk } from './Identhistorikk'
import { Personinfo } from './Personinfo'

export const Relasjoner = ({ relasjoner }) => {
	if (!relasjoner) return false

	return (
		<div>
			<SubOverskrift label="Familierelasjoner" />
			<div className="person-visning_content">
				{relasjoner.map((relasjon, idx) => (
					<div key={idx}>
						<div className="title-multiple">
							<h3>{relasjonTranslator(relasjon.relasjonTypeNavn)}</h3>
						</div>
						<div>
							<Personinfo data={relasjon.personRelasjonMed} visTittel={false} />
							<Identhistorikk
								identhistorikk={relasjon.personRelasjonMed.identHistorikk}
								visTittel={false}
							/>
						</div>
					</div>
				))}
			</div>
		</div>
	)
}
