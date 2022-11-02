import React, { useCallback, useEffect, useRef, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Panel from '~/components/ui/panel/Panel'
import _orderBy from 'lodash/orderBy'
import { DollyApi } from '~/service/Api'
import { Alert } from '@navikt/ds-react'

const Visning = ({ data }) => {
	if (!data) {
		return null
	}
	return (
		<>
			<TitleValue title="Brukertype" value={data.brukertype} />
			<TitleValue title="Servicebehov" value={data.servicebehov} />
			<TitleValue title="Inaktiv fra dato" value={data.inaktiveringDato} />
			<TitleValue
				title="Automatisk innsending av meldekort"
				value={data.automatiskInnsendingAvMeldekort}
			/>

			{data.aap115?.[0] && (
				<DollyFieldArray header="11.5 vedtak" data={data.aap115} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Fra dato" value={Formatters.formatDate(vedtak.fraDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{data.aap?.[0] && (
				<DollyFieldArray header="AAP-UA vedtak" data={data.aap} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Fra dato" value={Formatters.formatDate(vedtak.fraDato)} />
							<TitleValue title="Til dato" value={Formatters.formatDate(vedtak.tilDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{data.dagpenger?.[0] && (
				<DollyFieldArray header="Dagpenger vedtak" data={data.dagpenger} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Rettighet kode" value={vedtak.rettighetKode} />
							<TitleValue title="Fra dato" value={Formatters.formatDate(vedtak.fraDato)} />
							<TitleValue title="Til dato" value={Formatters.formatDate(vedtak.tilDato)} />
							<TitleValue title="Mottatt dato" value={Formatters.formatDate(vedtak.mottattDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}
		</>
	)
}

const ARENASYNT = 'ARENASYNT'

export const ArenaVisning = ({ data, ident, bestillinger, loading, useStandard = true }) => {
	const [harArenasyntTag, setHarArenasyntTag] = useState(false)
	const [tagsloading, setTagsLoading] = useState(false)
	const mountedRef = useRef(true)

	const execute = useCallback(() => {
		const getTags = async () => {
			setTagsLoading(true)
			const resp = await DollyApi.getTagsForIdent(ident.ident)
				.then((response) => {
					return response.data
				})
				.catch((_e) => {
					return []
				})
			if (mountedRef.current) {
				setTagsLoading(false)
				setHarArenasyntTag(resp && resp.includes(ARENASYNT))
			}
		}
		return getTags()
	}, [])

	useEffect(() => {
		if (ident.master === 'PDL') {
			execute()
		}
		return () => {
			mountedRef.current = false
		}
	}, [])
	if (loading || tagsloading) {
		return <Loading label="Laster arena-data" />
	}
	if (!data && !harArenasyntTag) {
		return null
	}

	const arenaBestillinger = bestillinger.filter((bestilling) =>
		bestilling.data.hasOwnProperty('arenaforvalter')
	)

	const sortedBestillinger =
		arenaBestillinger?.length > 0 ? _orderBy(arenaBestillinger, ['id'], ['desc']) : []
	const sisteArenaBestilling = sortedBestillinger?.[0]

	const visningData = {
		brukertype: undefined,
		servicebehov: undefined,
		inaktiveringDato: undefined,
		automatiskInnsendingAvMeldekort: undefined,
		aap115: [],
		aap: [],
		dagpenger: [],
	}

	// Arenaforvalternen returnerer veldig lite informasjon, bruker derfor data fra bestillingen i tillegg

	fyllVisningData(sisteArenaBestilling, visningData)

	const TagAlert = () => (
		<Alert variant={'info'} style={{ marginBottom: '20px' }}>
			Denne identen kan allerede være registrert i Arena Q2 med eller uten ytelser.
		</Alert>
	)

	return (
		<div>
			{useStandard ? (
				<div>
					<SubOverskrift label="Arbeidsytelser" iconKind="arena" />
					{harArenasyntTag && !data && <TagAlert />}
					<div className="person-visning_content">
						<Visning data={visningData} />
					</div>
				</div>
			) : (
				<Panel heading="Registrerte arbeidsytelser" iconType="arena">
					<div className="person-visning">
						{harArenasyntTag && !data && <TagAlert />}
						<div className="person-visning_content">
							<Visning data={visningData} />
						</div>
					</div>
				</Panel>
			)}
		</div>
	)
}

function fyllVisningData(bestilling, visningData) {
	if (!bestilling) {
		return null
	}
	const {
		arenaBrukertype,
		kvalifiseringsgruppe,
		inaktiveringDato,
		automatiskInnsendingAvMeldekort,
		aap115,
		aap,
		dagpenger,
	} = bestilling.data.arenaforvalter
	if (!visningData.brukertype) {
		visningData.brukertype =
			arenaBrukertype === 'MED_SERVICEBEHOV' ? 'Med servicebehov' : 'Uten servicebehov'
	}
	if (!visningData.servicebehov) {
		visningData.servicebehov = servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe)
	}
	if (!visningData.inaktiveringDato) {
		visningData.inaktiveringDato = Formatters.formatDate(inaktiveringDato)
	}
	if (!visningData.automatiskInnsendingAvMeldekort) {
		visningData.automatiskInnsendingAvMeldekort = Formatters.oversettBoolean(
			automatiskInnsendingAvMeldekort
		)
	}

	if (aap115) visningData.aap115 = visningData.aap115.concat(aap115)
	if (aap) visningData.aap = visningData.aap.concat(aap)
	if (dagpenger) visningData.dagpenger = visningData.dagpenger.concat(dagpenger)
}

function servicebehovKodeTilBeskrivelse(value) {
	if (!value) {
		return null
	}
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
