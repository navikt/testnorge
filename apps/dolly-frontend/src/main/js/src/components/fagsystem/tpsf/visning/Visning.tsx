import * as _ from 'lodash-es'
import {
	MidlertidigAdresse,
	Postadresse,
	Relasjoner,
	TpsfBoadresse,
	TpsfIdenthistorikk,
	TpsfNasjonalitet,
	TpsfPersoninfo,
	TpsfVergemaal,
} from './partials'
import { NorskBankkonto, UtenlandskBankkonto } from '@/components/fagsystem/bankkonto/visning'

export const TpsfVisning = ({ data }) => {
	if (!data) {
		return null
	}

	return (
		<div>
			<TpsfPersoninfo data={data} />
			<TpsfNasjonalitet data={data} />
			<TpsfVergemaal data={data?.vergemaal} />
			<TpsfBoadresse boadresse={data?.boadresse} />
			<Postadresse postadresse={data?.postadresse} />
			<MidlertidigAdresse midlertidigAdresse={data?.midlertidigAdresse} />
			<UtenlandskBankkonto data={data?.bankkontonrUtland} />
			<NorskBankkonto data={data?.bankkontonrNorsk} />
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
	const harFoedselsinnvandring = !_.has(foersteBestilling, 'data.tpsf.innvandretFraLand')
	//Voksne personer i Dolly "fødes" ved hjelp av en innvandringsmelding. Vil ikke vise den
	if (harFoedselsinnvandring)
		data = {
			...data,
			innvandretUtvandret: data?.innvandretUtvandret?.filter(
				(element, idx) =>
					idx !== data.innvandretUtvandret.length - 1 || element?.innutvandret === 'UTVANDRET',
			),
		}
	return data
}
