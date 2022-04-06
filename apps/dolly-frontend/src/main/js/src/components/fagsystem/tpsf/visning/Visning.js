import React, { useEffect, useState, useRef, useCallback } from 'react'
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
import { PdlSikkerhetstiltak } from '~/components/fagsystem/pdl/visning/partials/PdlSikkerhetstiltak'
import { PdlPersonInfo } from '~/components/fagsystem/pdl/visning/partials/PdlPersonInfo'
import { PdlNasjonalitet } from '~/components/fagsystem/tpsf/visning/partials/PdlNasjonalitet'
import { Telefonnummer } from '~/components/fagsystem/pdlf/visning/partials/Telefonnummer'

export const TpsfVisning = ({ data, pdlData, environments }) => {
	if (!data) return null
	const [tpsMessagingData, setTpsMessagingData] = useState(null)
	const [tpsMessagingLoading, setTpsMessagingLoading] = useState(false)
	const mountedRef = useRef(true)

	const execute = useCallback(() => {
		const tpsMessaging = async () => {
			setTpsMessagingLoading(true)
			const ident = data?.ident ? data.ident : pdlData?.ident
			const resp = await TpsMessagingApi.getTpsPersonInfo(ident, environments[0])
				.then((response) => {
					return response?.data[0]?.person
				})
				.catch((e) => {
					return null
				})
			if (mountedRef.current) {
				setTpsMessagingData(resp)
				setTpsMessagingLoading(false)
			}
		}
		return tpsMessaging()
	}, [environments])

	useEffect(() => {
		if (environments && environments.length > 0) {
			execute()
		}
		return () => {
			mountedRef.current = false
		}
	}, [])

	const harPdlAdresse =
		_has(pdlData, 'person.bostedsadresse') ||
		_has(pdlData, 'person.oppholdsadresse') ||
		_has(pdlData, 'person.kontaktadresse')

	const harPdlFullmakt = pdlData && _has(pdlData, 'person.fullmakt')

	const hasTpsfData = data.ident

	return (
		<div>
			<>
				{hasTpsfData ? (
					<Personinfo data={data} tpsMessagingData={tpsMessagingData} pdlData={pdlData} />
				) : (
					<PdlPersonInfo
						data={pdlData}
						tpsMessagingData={tpsMessagingData}
						tpsMessagingLoading={tpsMessagingLoading}
					/>
				)}
				{hasTpsfData ? (
					<Nasjonalitet data={data} pdlData={pdlData} />
				) : (
					<PdlNasjonalitet
						data={pdlData}
						tpsMessagingData={tpsMessagingData}
						tpsMessagingLoading={tpsMessagingLoading}
					/>
				)}
				{hasTpsfData && <Vergemaal data={data.vergemaal} />}
				{!harPdlFullmakt && <Fullmakt data={data.fullmakt} relasjoner={data.relasjoner} />}
				{!harPdlAdresse && (
					<>
						<Boadresse boadresse={data.boadresse} />
						<Postadresse postadresse={data.postadresse} />
						<MidlertidigAdresse midlertidigAdresse={data.midlertidigAdresse} />
					</>
				)}
				<Telefonnummer data={hasTpsfData ? data.telefonnumre : pdlData?.telefonnummer} />
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
