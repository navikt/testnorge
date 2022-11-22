import React from 'react'
import _has from 'lodash/has'
import {
	TpsfBoadresse,
	Fullmakt,
	TpsfIdenthistorikk,
	MidlertidigAdresse,
	TpsfNasjonalitet,
	Postadresse,
	Relasjoner,
	TpsfPersoninfo,
	TpsfVergemaal,
} from './partials'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { NorskBankkonto, UtenlandskBankkonto } from '~/components/fagsystem/bankkonto/visning'

export const TpsfVisning = ({ data, ident }) => {
	if (!data) {
		return null
	}

	return (
		<div>
			<TpsfPersoninfo data={data} />
			<TpsfNasjonalitet data={data} />
			<TpsfVergemaal data={data?.vergemaal} />
			<Fullmakt data={data?.fullmakt} relasjoner={data?.relasjoner} />
			<TpsfBoadresse boadresse={data?.boadresse} />
			<Postadresse postadresse={data?.postadresse} />
			<MidlertidigAdresse midlertidigAdresse={data?.midlertidigAdresse} />
			<Telefonnummer data={data?.telefonnumre} />
			<UtenlandskBankkonto data={data?.bankkontonrUtland} ident={ident} />
			<NorskBankkonto data={data?.bankkontonrNorsk} ident={ident} />
			<TpsfIdenthistorikk identhistorikk={data?.identHistorikk} />
			<Relasjoner relasjoner={data?.relasjoner} />
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
			innvandretUtvandret: data?.innvandretUtvandret?.filter(
				(element, idx) =>
					idx !== data.innvandretUtvandret.length - 1 || element?.innutvandret === 'UTVANDRET'
			),
		}
	return data
}
