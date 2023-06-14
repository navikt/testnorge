import React, { useCallback, useEffect, useRef, useState } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import * as _ from 'lodash-es'
import { DollyApi } from '@/service/Api'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useArenaEnvironments } from '@/utils/hooks/useEnvironments'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const Visning = ({ data }) => {
	if (!data || data.length === 0) {
		return null
	}
	const arenaData = data[0]
	if (arenaData.error) {
		return (
			<StyledAlert variant={'info'} size={'small'}>
				Fant ingen data i dette miljøet. Forsøk å gjenopprette personen for å fikse dette, og ta
				eventuelt kontakt med Team Dolly dersom problemet vedvarer.
			</StyledAlert>
		)
	}

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
							<TitleValue title="Fra dato" value={formatDate(vedtak.fraDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{arenaData.aap?.[0] && (
				<DollyFieldArray header="AAP-UA vedtak" data={arenaData.aap} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Fra dato" value={formatDate(vedtak.fraDato)} />
							<TitleValue title="Til dato" value={formatDate(vedtak.tilDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}

			{arenaData.dagpenger?.[0] && (
				<DollyFieldArray header="Dagpenger vedtak" data={arenaData.dagpenger} nested>
					{(vedtak, idx) => (
						<React.Fragment key={idx}>
							<TitleValue title="Rettighet kode" value={vedtak.rettighetKode} />
							<TitleValue title="Fra dato" value={formatDate(vedtak.fraDato)} />
							<TitleValue title="Til dato" value={formatDate(vedtak.tilDato)} />
							<TitleValue title="Mottatt dato" value={formatDate(vedtak.mottattDato)} />
						</React.Fragment>
					)}
				</DollyFieldArray>
			)}
		</div>
	)
}

const ARENASYNT = 'ARENASYNT'
const SYNT_MILJOE = 'q2'
const SYNT_INFO = 'Denne identen kan allerede være registrert i Arena Q2 med eller uten ytelser'

const initialVisningData = {
	brukertype: undefined,
	servicebehov: undefined,
	inaktiveringDato: undefined,
	automatiskInnsendingAvMeldekort: undefined,
	aap115: [],
	aap: [],
	dagpenger: [],
}

export const ArenaVisning = ({ data, ident, bestillinger, loading, tilgjengeligMiljoe }) => {
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
	const { arenaEnvironments, loading: loadingArena } = useArenaEnvironments()

	if (loading || tagsloading || loadingArena) {
		return <Loading label="Laster arena-data" />
	}
	if (!data && !harArenasyntTag) {
		return null
	}

	const arenaBestillinger = bestillinger.filter((bestilling) =>
		bestilling.data.hasOwnProperty('arenaforvalter')
	)
	let visningData = mapTilVisningData(arenaBestillinger, harArenasyntTag, arenaEnvironments)
	const bestilteMiljoer = visningData
		.filter((best) => best.data?.length > 0)
		.map((best) => best.miljo)

	const miljoerMedData = data?.arbeidsokerList?.map((arb) => arb.miljoe)
	const errorMiljoer = bestilteMiljoer.filter((m) => !miljoerMedData?.includes(m))
	visningData = visningData.map((vData) => {
		if (vData.data?.length > 0) {
			vData.data[0].error = !miljoerMedData?.includes(vData.miljo)
		}
		return vData
	})

	const filteredData =
		tilgjengeligMiljoe && visningData.filter((item) => item.miljo === tilgjengeligMiljoe)

	const forsteMiljo = visningData.find((miljoData) => miljoData?.data?.length > 0)?.miljo
	return (
		<div>
			<SubOverskrift label="Arbeidsytelser" iconKind="arena" />
			<MiljoTabs
				bestilteMiljoer={bestilteMiljoer}
				forsteMiljo={forsteMiljo ? forsteMiljo : SYNT_MILJOE}
				errorMiljoer={errorMiljoer}
				data={filteredData || visningData}
			>
				<Visning />
			</MiljoTabs>
		</div>
	)
}

const mapTilVisningData = (bestillinger, harArenaSyntTag, arenaMiljoer) => {
	const miljoeData = []

	const getMiljoe = (bestilling) => {
		return bestilling?.status
			?.filter((status) => status.id === 'ARENA')?.[0]
			?.statuser?.filter((status) => status.melding === 'OK')?.[0]
			?.detaljert?.map((detalj) => detalj.miljo)
	}

	for (const miljoe of arenaMiljoer) {
		const data = []
		for (const bestilling of bestillinger) {
			if (getMiljoe(bestilling)?.includes(miljoe)) {
				data.push(bestilling)
			}
		}

		const info = miljoe === SYNT_MILJOE && harArenaSyntTag ? SYNT_INFO : null

		let visningData = []
		if (data.length > 0) {
			const sortedBestillinger = data.length > 0 ? _.orderBy(data, ['id'], ['desc']) : []
			const sisteArenaBestilling = sortedBestillinger?.[0]
			let mappedData = { ...initialVisningData }
			fyllVisningData(sisteArenaBestilling, mappedData)
			visningData.push(mappedData)
		}

		miljoeData.push({ miljo: miljoe, data: visningData, info: info })
	}

	return miljoeData
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
	visningData.brukertype =
		arenaBrukertype === 'MED_SERVICEBEHOV' ? 'Med servicebehov' : 'Uten servicebehov'
	visningData.servicebehov = servicebehovKodeTilBeskrivelse(kvalifiseringsgruppe)
	visningData.inaktiveringDato = formatDate(inaktiveringDato)
	visningData.automatiskInnsendingAvMeldekort = oversettBoolean(automatiskInnsendingAvMeldekort)

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
