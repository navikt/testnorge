import React, { useCallback, useEffect, useRef, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import Panel from '~/components/ui/panel/Panel'
import _orderBy from 'lodash/orderBy'
import { DollyApi } from '~/service/Api'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'

const Visning = ({ data }) => {
	if (!data || data.length === 0) {
		return null
	}
	const arenaData = data[0]
	return (
		<div className="person-visning_content">
			<TitleValue title="Brukertype" value={arenaData.brukertype} />
			<TitleValue title="Servicebehov" value={arenaData.servicebehov} />
			<TitleValue title="Inaktiv fra dato" value={arenaData.inaktiveringDato} />
			<TitleValue
				title="Automatisk innsending av meldekort"
				value={arenaData.automatiskInnsendingAvMeldekort}
			/>

			{arenaData.aap115?.[0] && (
				<DollyFieldArray header="11.5 vedtak" data={arenaData.aap115} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Fra dato" value={Formatters.formatDate(vedtak.fraDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{arenaData.aap?.[0] && (
				<DollyFieldArray header="AAP-UA vedtak" data={arenaData.aap} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Fra dato" value={Formatters.formatDate(vedtak.fraDato)} />
							<TitleValue title="Til dato" value={Formatters.formatDate(vedtak.tilDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{arenaData.dagpenger?.[0] && (
				<DollyFieldArray header="Dagpenger vedtak" data={arenaData.dagpenger} nested>
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
		</div>
	)
}

const ARENASYNT = 'ARENASYNT'
const ARENA_MILJOE = ['q1', 'q2', 'q4']
const SYNT_MILJOE = 'q2'

const initialVisningData = {
	brukertype: undefined,
	servicebehov: undefined,
	inaktiveringDato: undefined,
	automatiskInnsendingAvMeldekort: undefined,
	aap115: [],
	aap: [],
	dagpenger: [],
}

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
	const arenaMiljoer = data?.arbeidsokerList?.map((arb) => arb.miljoe)
	const visningData = mapTilVisingData(data, arenaMiljoer, arenaBestillinger, harArenasyntTag)
	const forsteMiljo = visningData.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	return (
		<div>
			{useStandard ? (
				<div>
					<SubOverskrift label="Arbeidsytelser" iconKind="arena" />
					<MiljoTabs
						bestilteMiljoer={arenaMiljoer?.length > 0 ? arenaMiljoer : [SYNT_MILJOE]}
						forsteMiljo={forsteMiljo ? forsteMiljo : SYNT_MILJOE}
						data={visningData}
					>
						<Visning />
					</MiljoTabs>
				</div>
			) : (
				<Panel heading="Registrerte arbeidsytelser" iconType="arena">
					<div className="person-visning">
						<MiljoTabs
							bestilteMiljoer={arenaMiljoer?.length > 0 ? arenaMiljoer : [SYNT_MILJOE]}
							forsteMiljo={forsteMiljo ? forsteMiljo : SYNT_MILJOE}
							data={visningData}
						>
							<Visning />
						</MiljoTabs>
					</div>
				</Panel>
			)}
		</div>
	)
}

const mapTilVisingData = (arenaData, bestilteMiljoer, bestillinger, harArenaSyntTag) => {
	const miljoeData = []

	const getMiljoe = (bestilling) => {
		return bestilling?.status
			?.filter((status) => status.id === 'ARENA')?.[0]
			?.statuser?.filter((status) => status.melding === 'OK')?.[0]
			?.detaljert?.map((detalj) => detalj.miljo)
	}

	for (const miljoe of ARENA_MILJOE) {
		const data = []
		if (bestilteMiljoer?.includes(miljoe)) {
			for (const bestilling of bestillinger) {
				if (getMiljoe(bestilling)?.includes(miljoe)) {
					data.push(bestilling)
				}
			}
		}

		const info =
			miljoe === SYNT_MILJOE && harArenaSyntTag
				? 'Denne identen kan allerede være registrert i Arena Q2 med eller uten ytelser'
				: null

		let visningData = []
		if (data.length > 0) {
			const sortedBestillinger = data.length > 0 ? _orderBy(data, ['id'], ['desc']) : []
			const sisteArenaBestilling = sortedBestillinger?.[0]
			let mappedData = { ...initialVisningData }
			const arena = arenaData?.arbeidsokerList?.filter((arbs) => arbs.miljoe === miljoe)?.[0]
			fyllVisningData(arena, sisteArenaBestilling, mappedData)
			visningData.push(mappedData)
		}

		miljoeData.push({ miljo: miljoe, data: visningData, info: info })
	}

	return miljoeData
}

function fyllVisningData(data, bestilling, visningData) {
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
	visningData.brukertype =
		arenaBrukertype === data?.servicebehov ? 'Med servicebehov' : 'Uten servicebehov'
	if (data?.servicebehov) {
		visningData.servicebehov = servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe)
	}
	visningData.inaktiveringDato = Formatters.formatDate(inaktiveringDato)
	visningData.automatiskInnsendingAvMeldekort = Formatters.oversettBoolean(
		automatiskInnsendingAvMeldekort
	)

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
