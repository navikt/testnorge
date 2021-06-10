import React from 'react'
import _get from 'lodash/get'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { Historikk } from '~/components/ui/historikk/Historikk'

export const Visning = ({ data }) => {
	if (!data) return null
	return (
		<>
			<TitleValue title="Brukertype" value={data.brukertype} />
			<TitleValue title="Servicebehov" value={data.servicebehov} />
			<TitleValue title="Inaktiv fra dato" value={data.inaktiveringDato} />
			<TitleValue
				title="Automatisk innsending av meldekort"
				value={data.automatiskInnsendingAvMeldekort}
			/>
			<TitleValue title="Har 11-5-vedtak" value={data.harAap115} />
			<TitleValue title="11-5 fra dato" value={data.aap115FraDato} />
			<TitleValue title="Har AAP vedtak UA - positivt utfall" value={data.harAap} />
			<TitleValue title="Aap fra dato" value={data.aapFraDato} />
			<TitleValue title="Aap til dato" value={data.aapTilDato} />
			<TitleValue title="Har dagpengevedtak" value={data.harDagpenger} />
			<TitleValue title="RettighetKode" value={data.dagpengerRettighetKode} />
			<TitleValue title="Dagpenger fra dato" value={data.dagpengerFraDato} />
			<TitleValue title="Dagpenger til dato" value={data.dagpengerTilDato} />
			<TitleValue title="Dagpenger mottatt dato" value={data.dagpengerMottattDato} />
		</>
	)
}

export const ArenaVisning = ({ data, bestillinger, loading }) => {
	if (loading) return <Loading label="Laster arena-data" />
	if (!data) return false

	const sortedData = Array.isArray(data.arbeidsokerList)
		? data.arbeidsokerList.slice().reverse()
		: data.arbeidsokerList

	const arenaBestillinger = bestillinger.filter(bestilling =>
		bestilling.data.hasOwnProperty('arenaforvalter')
	)

	const visningData = []

	const fyllVisningData = (idx, info) => {
		const {
			kvalifiseringsgruppe,
			inaktiveringDato,
			automatiskInnsendingAvMeldekort,
			aap115,
			aap,
			dagpenger
		} = arenaBestillinger[idx].data.arenaforvalter
		visningData.push({
			brukertype: info.servicebehov ? 'Med servicebehov' : 'Uten servicebehov',
			servicebehov: servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe),
			inaktiveringDato: Formatters.formatDate(inaktiveringDato),
			automatiskInnsendingAvMeldekort: Formatters.oversettBoolean(automatiskInnsendingAvMeldekort),
			harAap115: aap115?.[0] && 'Ja',
			aap115FraDato: aap115?.[0] && Formatters.formatDate(aap115[0].fraDato),
			harAap: aap?.[0] && 'Ja',
			aapFraDato: aap?.[0] && Formatters.formatDate(aap[0].fraDato),
			aapTilDato: aap?.[0] && Formatters.formatDate(aap[0].tilDato),
			harDagpenger: dagpenger?.[0] && 'Ja',
			dagpengerFraDato: dagpenger?.[0] && Formatters.formatDate(dagpenger[0].fraDato),
			dagpengerTilDato: dagpenger?.[0] && Formatters.formatDate(dagpenger[0].tilDato),
			dagpengerMottattDato: dagpenger?.[0] && Formatters.formatDate(dagpenger[0].mottattDato),
			dagpengerRettighetKode:
				dagpenger?.[0] && Formatters.showLabel('rettighetKode', dagpenger[0].rettighetKode)
		})
	}

	// Arenaforvalternen returnerer veldig lite informasjon, bruker derfor data fra bestillingen i tillegg
	sortedData.forEach((info, idx) => {
		if (_get(arenaBestillinger, `[${idx}].data.arenaforvalter`) !== undefined) {
			fyllVisningData(idx, info)
		}
	})

	return (
		<div>
			<SubOverskrift label="Arbeidsytelser" iconKind="arena" />
			<div className="person-visning_content">
				<Historikk component={Visning} data={visningData} />
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
