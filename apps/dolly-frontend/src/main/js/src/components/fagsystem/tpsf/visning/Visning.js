import React, { useEffect, useState } from 'react'
import _has from 'lodash/has'
import {
	Boadresse,
	Fullmakt,
	Identhistorikk,
	MidlertidigAdresse,
	Nasjonalitet,
	NorskBankkonto,
	Personinfo,
	Postadresse,
	Relasjoner,
	UtenlandskBankkonto,
	Vergemaal,
} from './partials'
import { TpsMessagingApi } from '~/service/Api'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdlf/visning/partials/PdlSikkerhetstiltak'

export const TpsfVisning = ({ data, environments, pdlData }) => {
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	useEffect(() => {
		if (environments && environments.length > 0) {
			TpsMessagingApi.getTpsPersonInfo(data.ident, environments[0]).then((response) => {
				setTpsMessagingData(response?.data[0]?.person)
			})
		}
	}, [])
	if (!data) return null

	return (
		<div>
			<Personinfo data={data} tpsMessagingData={tpsMessagingData} />
			<Nasjonalitet data={data} pdlData={pdlData} tpsMessagingData={tpsMessagingData} />
			<Vergemaal data={data.vergemaal} />
			<Fullmakt data={data.fullmakt} relasjoner={data.relasjoner} />
			<Boadresse boadresse={data.boadresse} />
			<Postadresse postadresse={data.postadresse} />
			<MidlertidigAdresse midlertidigAdresse={data.midlertidigAdresse} />
			{pdlData?.[0]?.person?.sikkerhetstiltak && (
				<PdlSikkerhetstiltak data={pdlData[0].person.sikkerhetstiltak} />
			)}
			<UtenlandskBankkonto
				data={
					tpsMessagingData?.bankkontonrUtland
						? tpsMessagingData.bankkontonrUtland
						: data.bankkontonrUtland
				}
			/>
			<NorskBankkonto
				data={
					tpsMessagingData?.bankkontonrNorsk
						? tpsMessagingData.bankkontonrNorsk
						: data.bankkontonrNorsk
				}
			/>
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
				(i, idx) => idx !== data.innvandretUtvandret.length - 1 || i.innutvandret === 'UTVANDRET'
			),
		}
	return data
}
