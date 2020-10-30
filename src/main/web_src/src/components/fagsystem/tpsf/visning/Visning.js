import React from 'react'
import _has from 'lodash/has'
import {
	Personinfo,
	Nasjonalitet,
	Boadresse,
	Postadresse,
	MidlertidigAdresse,
	Identhistorikk,
	Relasjoner,
	Vergemaal
} from './partials'

export const TpsfVisning = ({ data }) => {
	if (!data) return null

	return (
		<div>
			<Personinfo data={data} />
			<Nasjonalitet data={data} />
			<Vergemaal data={data.vergemaal} />
			<Boadresse boadresse={data.boadresse} />
			<Postadresse postadresse={data.postadresse} />
			<MidlertidigAdresse midlertidigAdresse={data.midlertidigAdresse} />
			<Identhistorikk identhistorikk={data.identHistorikk} />
			<Relasjoner relasjoner={data.relasjoner} />
		</div>
	)
}

/**
 * Denne funksjonen brukes til å fjerne verdier vi ikke ønsker vise, men som automatisk er med
 * på objektet vi får fra API. Kan feks være verdier som ikke er bestilt, men som får default
 * verdier som er uinteressante for bruker
 */
TpsfVisning.filterValues = (data, bestillingsListe) => {
	// Innvandret/ Utvandret
	const foersteBestilling = bestillingsListe[bestillingsListe.length - 1]
	const harFoedselsinnvandring = !_has(foersteBestilling, 'data.tpsf.innvandretFraLand')
	//Voksne personer i Dolly "fødes" ved hjelp av en innvandringsmelding. Vil ikke vise den
	if (harFoedselsinnvandring)
		data = {
			...data,
			innvandretUtvandret: data.innvandretUtvandret.filter(
				(i, idx) => idx !== data.innvandretUtvandret.length - 1
			)
		}
	return data
}
