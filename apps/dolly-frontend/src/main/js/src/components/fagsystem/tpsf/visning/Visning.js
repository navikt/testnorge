import React from 'react'
import _has from 'lodash/has'
import {
	TpsfBoadresse,
	Fullmakt,
	TpsfIdenthistorikk,
	MidlertidigAdresse,
	TpsfNasjonalitet,
	NorskBankkonto,
	Postadresse,
	Relasjoner,
	TpsfPersoninfo,
	UtenlandskBankkonto,
	TpsfVergemaal,
} from './partials'
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'
import { TpsMessagingData } from '~/components/fagsystem/tpsmessaging/form/TpsMessagingData'

export const TpsfVisning = ({ data, pdlData, environments }) => {
	if (!data) return null

	const ident = data?.ident ? data.ident : pdlData?.ident
	const tpsMessaging = TpsMessagingData(ident, environments)

	const harPdlAdresse =
		_has(pdlData, 'bostedsadresse') ||
		_has(pdlData, 'oppholdsadresse') ||
		_has(pdlData, 'kontaktadresse')

	const harPdlFullmakt = pdlData && _has(pdlData, 'fullmakt')

	const hasTpsfData = data.ident

	return (
		<div>
			<>
				<TpsfPersoninfo data={data} tpsMessagingData={tpsMessaging?.tpsMessagingData} />
				{hasTpsfData && <TpsfNasjonalitet data={data} pdlData={pdlData} />}
				{hasTpsfData && <TpsfVergemaal data={data.vergemaal} />}
				{!harPdlFullmakt && <Fullmakt data={data.fullmakt} relasjoner={data.relasjoner} />}
				{!harPdlAdresse && (
					<>
						<TpsfBoadresse boadresse={data.boadresse} />
						<Postadresse postadresse={data.postadresse} />
						<MidlertidigAdresse midlertidigAdresse={data.midlertidigAdresse} />
					</>
				)}
				<Telefonnummer data={hasTpsfData ? data.telefonnumre : pdlData?.telefonnummer} />
				{!hasTpsfData && <PdlSikkerhetstiltak data={pdlData?.sikkerhetstiltak} />}
			</>
			<UtenlandskBankkonto
				data={
					tpsMessaging?.tpsMessagingData?.bankkontonrUtland
						? tpsMessaging?.tpsMessagingData.bankkontonrUtland
						: data.bankkontonrUtland
				}
			/>
			<NorskBankkonto
				data={
					tpsMessaging?.tpsMessagingData?.bankkontonrNorsk
						? tpsMessaging?.tpsMessagingData.bankkontonrNorsk
						: data.bankkontonrNorsk
				}
			/>
			<TpsfIdenthistorikk identhistorikk={data.identHistorikk} />
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
