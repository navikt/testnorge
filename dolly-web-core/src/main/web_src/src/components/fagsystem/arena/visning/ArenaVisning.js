import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const ArenaVisning = ({ data }) => {
	return (
		<>
			<TitleValue title="Brukertype" value={data.brukertype} />
			<TitleValue title="Servicebehov" value={data.servicebehov} />
			<TitleValue title="Inaktiv fra dato" value={data.inaktiveringDato} />
			<TitleValue title="Har 11-5-vedtak" value={data.harAap115} />
			<TitleValue title="Fra dato" value={data.aap115FraDato} />
			<TitleValue title="Har AAP vedtak UA - positivt utfall" value={data.harAap} />
			<TitleValue title="Fra dato" value={data.aapFraDato} />
			<TitleValue title="Til dato" value={data.aapTilDato} />
		</>
	)
}

export const Arena = ({ data, bestData, loading }) => {
	if (loading) return <Loading label="Laster arena-data" />
	if (!data) return false

	const sortedData = Array.isArray(data.arbeidsokerList)
		? data.arbeidsokerList.slice().reverse()
		: data.arbeidsokerList

	console.log('data :', data)
	console.log('bestData :', bestData)
	const visningData = []

	// Areneforvalternen returnerer veldig lite informasjon, bruker derfor data fra bestillingen i tillegg
	sortedData.forEach((info, idx) => {
		const { kvalifiseringsgruppe, inaktiveringDato, aap115, aap } = bestData[
			idx
		].bestilling.arenaforvalter
		visningData.push({
			brukertype: info.servicebehov ? 'Med servicebehov' : 'Uten servicebehov',
			servicebehov: servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe),
			inaktiveringDato: Formatters.formatDate(inaktiveringDato),
			harAap115: aap115 && 'Ja',
			aap115FraDato: aap115 && Formatters.formatDate(aap115[0].fraDato),
			harAap: aap && 'Ja',
			aapFraDato: aap && Formatters.formatDate(aap[0].fraDato),
			aapTilDato: aap && Formatters.formatDate(aap[0].tilDato)
		})
	})

	return (
		<div>
			<SubOverskrift label="Arena" />
			<div className="person-visning_content">
				<Historikk component={ArenaVisning} data={visningData} />
			</div>
		</div>
	)
}

function servicebehovKodeTilBeskrivelse(value) {
	if (!value) return null
	switch (value) {
		case 'IKVAL':
			return 'IKVAL - Standardinnsats'
		case 'BFORM':
			return 'BFORM - Situasjonsbestemt innsats'
		case 'BATT':
			return 'BATT - Spesielt tilpasset innsats'
		case 'VARIG':
			return 'VARIG - Varig tilpasset innsats'
		default:
			return value
	}
}
