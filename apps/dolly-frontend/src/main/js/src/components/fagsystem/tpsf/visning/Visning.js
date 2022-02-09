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
import { PdlPersonInfo } from '~/components/fagsystem/pdlf/visning/partials/PdlPersonInfo'
import { PdlNasjonalitet } from '~/components/fagsystem/pdlf/visning/partials/PdlNasjonalitet'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'

export const TpsfVisning = ({ data, pdlData, environments }) => {
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	useEffect(() => {
		if (environments && environments.length > 0) {
			TpsMessagingApi.getTpsPersonInfo(data.ident, environments[0]).then((response) =>
				setTpsMessagingData(response?.data[0]?.person)
			)
		}
	}, [])
	if (!data) return null
	const harPdlBoadresse = pdlData && _has(pdlData, 'person.bostedsadresse')
	const harPdlOppholdsadresse = pdlData && _has(pdlData, 'person.oppholdsadresse')
	const harPdlKontaktadresse = pdlData && _has(pdlData, 'person.kontaktadresse')
	const harPdlFullmakt = pdlData && _has(pdlData, 'person.fullmakt')

	const hasTpsfData = data.ident

	return (
		<div>
			<>
				{hasTpsfData ? (
					<Personinfo data={data} tpsMessagingData={tpsMessagingData} pdlData={pdlData} />
				) : (
					<PdlPersonInfo data={pdlData} />
				)}
				{hasTpsfData ? (
					<Nasjonalitet data={data} pdlData={pdlData} />
				) : (
					<PdlNasjonalitet data={pdlData} />
				)}
				{hasTpsfData && <Vergemaal data={data.vergemaal} />}
				{!harPdlFullmakt && <Fullmakt data={data.fullmakt} relasjoner={data.relasjoner} />}
				{!harPdlBoadresse && <Boadresse boadresse={data.boadresse} />}
				{!harPdlOppholdsadresse && <Postadresse postadresse={data.postadresse} />}
				{!harPdlKontaktadresse && (
					<MidlertidigAdresse midlertidigAdresse={data.midlertidigAdresse} />
				)}
				{!hasTpsfData && <Telefonnummer data={pdlData?.telefonnummer} />}
				{!hasTpsfData && <PdlSikkerhetstiltak data={pdlData?.sikkerhetstiltak} />}
			</>
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
			innvandretUtvandret: data?.innvandretUtvandret?.filter(
				(element, idx) =>
					idx !== data.innvandretUtvandret.length - 1 || element?.innutvandret === 'UTVANDRET'
			),
		}
	return data
}
