import React, { useCallback, useEffect, useRef, useState } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollyApi } from '@/service/Api'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useArenaEnvironments } from '@/utils/hooks/useEnvironments'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const Visning = ({ data }) => {
	if (!data) {
		return null
	}

	const arenaData = data

	if (arenaData.feilmelding) {
		return (
			<StyledAlert variant={'warning'} size={'small'}>
				{arenaData.feilmelding}
			</StyledAlert>
		)
	}

	if (arenaData.error) {
		return (
			<StyledAlert variant={'info'} size={'small'}>
				Fant ingen data i dette miljøet. Forsøk å gjenopprette personen for å fikse dette, og ta
				eventuelt kontakt med Team Dolly dersom problemet vedvarer.
			</StyledAlert>
		)
	}

	const vedtakListe = arenaData.vedtakListe

	return (
		<div className="person-visning_content">
			<TitleValue title="Aktiveringsdato" value={formatDate(arenaData.registrertDato)} />
			<TitleValue title="Sist inaktiv" value={formatDate(arenaData.sistInaktivDato)} />
			<TitleValue title="Hovedmål" value={arenaData.hovedmaal} />
			<TitleValue title="Formidlingsgruppe" value={arenaData.formidlingsgruppe?.navn} />
			<TitleValue title="Servicegruppe" value={arenaData.servicegruppe?.navn} />
			<TitleValue title="Rettighetsgruppe" value={arenaData.rettighetsgruppe?.navn} />
			{vedtakListe && vedtakListe.length > 0 && (
				<DollyFieldArray data={vedtakListe} header="Vedtak" nested>
					{(vedtak) => (
						<>
							<TitleValue title="Sakstype" value={vedtak.sak?.navn} />
							<TitleValue title="Status på sak" value={vedtak.sak?.status} />
							<TitleValue title="Saksnummer" value={vedtak.sak?.sakNr} />
							<TitleValue title="Vedtaksnummer" value={vedtak.vedtakNr} />
							<TitleValue title="Vedtaksrettighet" value={vedtak.rettighet?.navn} />
							<TitleValue title="Aktivitetsfase" value={vedtak.aktivitetfase?.navn} />
							<TitleValue title="Vedtakstype" value={vedtak.type?.navn} />
							<TitleValue title="Vedtakstatus" value={vedtak.status?.navn} />
							<TitleValue title="Utfall" value={vedtak.utfall} />
							<TitleValue title="Vedtak fra dato" value={formatDate(vedtak.fraDato)} />
							<TitleValue title="Vedtak til dato" value={formatDate(vedtak.tilDato)} />
						</>
					)}
				</DollyFieldArray>
			)}
		</div>
	)
}

const ARENASYNT = 'ARENASYNT'
const SYNT_MILJOE = 'q2'
const SYNT_INFO = 'Denne identen kan allerede være registrert i Arena Q2 med eller uten ytelser'

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
		bestilling.data.hasOwnProperty('arenaforvalter'),
	)
	const bestilteMiljoer = getBestilteMiljoer(arenaBestillinger, arenaEnvironments)
	const miljoerMedData = data?.map((arb) => arb.miljo)
	const errorMiljoer = bestilteMiljoer.filter((m) => !miljoerMedData?.includes(m))

	const visningData = data?.map((miljoData) => {
		const info = miljoData.miljo === SYNT_MILJOE && harArenasyntTag ? SYNT_INFO : null
		return { ...miljoData, info: info }
	})

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => item.miljo === tilgjengeligMiljoe)

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

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

const getBestilteMiljoer = (bestillinger, arenaMiljoer) => {
	const bestilteMiljoer = []

	const getMiljoe = (bestilling) => {
		return bestilling?.status
			?.filter((status) => status.id === 'ARENA_BRUKER' || status.id === 'ARENA')?.[0]
			?.statuser?.filter((status) => status.melding === 'OK')?.[0]
			?.detaljert?.map((detalj) => detalj.miljo)
	}

	arenaMiljoer.forEach((miljoe) => {
		bestillinger.forEach((bestilling) => {
			const bestMiljoe = getMiljoe(bestilling)
			if (bestMiljoe?.includes(miljoe)) {
				bestilteMiljoer.push(miljoe)
			}
		})
	})
	return bestilteMiljoer
}
