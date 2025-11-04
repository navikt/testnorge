import React, { useCallback, useEffect, useRef, useState } from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { DollyApi } from '@/service/Api'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'

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

export const ArenaVisning = ({
	data,
	ident,
	bestillingIdListe,
	loading,
	tilgjengeligMiljoe,
	harArenaBestilling,
}) => {
	const [harArenasyntTag, setHarArenasyntTag] = useState(false)
	const [tagsloading, setTagsloading] = useState(false)
	const mountedRef = useRef(true)

	const { bestilteMiljoer: bestilteMiljoerNye } = useBestilteMiljoer(
		bestillingIdListe,
		'ARENA_BRUKER',
	)
	const { bestilteMiljoer: bestilteMiljoerGamle } = useBestilteMiljoer(bestillingIdListe, 'ARENA')
	const bestilteMiljoer = bestilteMiljoerNye?.concat(bestilteMiljoerGamle)

	const execute = useCallback(() => {
		const getTags = async () => {
			setTagsloading(true)
			const resp = await DollyApi.getTagsForIdent(ident.ident)
				.then((response) => {
					setTagsloading(false)
					return response.data
				})
				.catch((_e) => {
					setTagsloading(false)
					return []
				})
			if (mountedRef.current) {
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
	if (!data) {
		return null
	}

	const miljoerMedData = data?.filter((arb) => arb.data)?.map((arb) => arb.miljo)

	if ((!miljoerMedData || miljoerMedData?.length < 1) && !harArenaBestilling) {
		return null
	}

	const errorMiljoer = bestilteMiljoer?.filter((m) => !miljoerMedData?.includes(m))

	const visningData = data?.map((miljoData) => {
		const info = miljoData.miljo === SYNT_MILJOE && harArenasyntTag ? SYNT_INFO : null
		return { ...miljoData, info: info }
	})

	const filteredData =
		tilgjengeligMiljoe && data?.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	const forsteMiljo = data?.find((miljoData) => miljoData?.data)?.miljo

	return (
		<div>
			<SubOverskrift
				label="Arbeidsytelser"
				iconKind="arena"
				isWarning={!miljoerMedData || miljoerMedData?.length < 1}
			/>
			{!miljoerMedData || miljoerMedData?.length < 1 ? (
				<StyledAlert variant={'warning'} size={'small'} inline>
					Fant ikke Arena-data på person
				</StyledAlert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					forsteMiljo={forsteMiljo ? forsteMiljo : SYNT_MILJOE}
					errorMiljoer={errorMiljoer}
					data={filteredData || visningData}
				>
					<Visning />
				</MiljoTabs>
			)}
		</div>
	)
}
