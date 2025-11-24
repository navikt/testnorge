import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { codeToNorskLabel, formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { usePensjonVedtak } from '@/utils/hooks/usePensjon'
import StyledAlert from '@/components/ui/alert/StyledAlert'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { isSameDay } from 'date-fns'
import { mergeMiljoData } from '@/components/ui/miljoTabs/utils'
import { isNumber } from 'lodash-es'

export const sjekkManglerApData = (apData) => {
	return apData?.length < 1 || apData?.every((miljoData) => !miljoData.data)
}

const getNavEnhetLabel = (navEnheter, navEnhetId) => {
	return navEnheter?.find((enhet) => enhet.value === navEnhetId?.toString())?.label ?? navEnhetId
}

const kravtyper = {
	PEN_AP: 'Førstegangsbehandling',
	PEN_AP_NY_UTTAKSGRAD: 'Uttaksgradsendring',
	PEN_AP_REVURDERING: 'Revurdering',
}

const DataVisning = ({ data, miljo, ident }) => {
	const { navEnheter } = useNavEnheter()

	const { vedtakData, loading } = usePensjonVedtak(ident, miljo)
	const vedtakAP = vedtakData?.find(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'FORSTEGANGSBEHANDLING',
	)
	const vedtakNyUttaksgrad = vedtakData?.filter(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'UTTAKSGRADSENDRING',
	)
	const vedtakRevurdering = vedtakData?.filter(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'SIVILSTANDENDRING',
	)

	const getVedtakStatus = (system, transaksjonId) => {
		if (system === 'PEN_AP') {
			return vedtakAP
		} else if (system === 'PEN_AP_NY_UTTAKSGRAD') {
			return vedtakNyUttaksgrad?.find((v) =>
				isSameDay(new Date(v?.fom), new Date(transaksjonId?.fom)),
			)
		} else if (system === 'PEN_AP_REVURDERING') {
			return vedtakRevurdering?.find((r) =>
				isSameDay(new Date(r?.fom), new Date(transaksjonId?.fom)),
			)
		}
		return null
	}

	return (
		<>
			{!vedtakAP && !loading && (
				<StyledAlert variant={'warning'} size={'small'}>
					Kunne ikke hente vedtaksstatus for person. Dette skyldes enten at vedtaksstatus ikke er
					klar ennå, eller at opprettelse av alderspensjon har feilet.
				</StyledAlert>
			)}
			<DollyFieldArray data={data} header={null} nested>
				{(item, idx) => {
					const transaksjonId = item?.transaksjonId
					const vedtakStatus = getVedtakStatus(item?.system, transaksjonId)
					const vedtakSisteOppdatering = vedtakStatus?.sisteOppdatering?.includes('opprettet')
						? 'Iverksatt'
						: vedtakStatus?.sisteOppdatering
					return (
						<div className="person-visning_content" style={{ marginBottom: 0 }} key={idx}>
							<TitleValue title="Vedtaksstatus" value={vedtakSisteOppdatering} />
							<TitleValue title="Kravtype" value={kravtyper[item?.system]} />
							<TitleValue
								title="Iverksettelsesdato"
								value={formatDate(transaksjonId?.iverksettelsesdato)}
							/>
							<TitleValue title="Dato f.o.m." value={formatDate(transaksjonId?.fom)} />
							<TitleValue
								title="Uttaksgrad"
								value={isNumber(transaksjonId?.uttaksgrad) ? `${transaksjonId?.uttaksgrad}%` : null}
							/>
							<TitleValue
								title="Ny uttaksgrad"
								value={
									isNumber(transaksjonId?.nyUttaksgrad) ? `${transaksjonId?.nyUttaksgrad}%` : null
								}
							/>
							<TitleValue
								title="Revurderingsårsak"
								value={codeToNorskLabel(transaksjonId?.revurderingArsakType)}
							/>
							<TitleValue title="Saksbehandler" value={transaksjonId?.saksbehandler} />
							<TitleValue title="Attesterer" value={transaksjonId?.attesterer} />
							<TitleValue
								title="Nav-kontor"
								value={getNavEnhetLabel(navEnheter, transaksjonId?.navEnhetId)}
							/>
							<TitleValue
								title="Ektefelle/partners inntekt"
								value={transaksjonId?.relasjonListe?.[0]?.sumAvForventetArbeidKapitalPensjonInntekt}
							/>
							<TitleValue
								title="Inkluder AFP privat"
								value={oversettBoolean(transaksjonId?.inkluderAfpPrivat)}
							/>
							<TitleValue
								title="AFP privat resultat"
								value={showLabel('afpPrivatResultat', transaksjonId?.afpPrivatResultat)}
							/>
						</div>
					)
				}}
			</DollyFieldArray>
		</>
	)
}

export const AlderspensjonVisning = ({
	data,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	ident,
}) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_AP')
	bestilteMiljoer?.sort()

	if (loading) {
		return <Loading label="Laster alderspensjon-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerApData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const mergetData = mergeMiljoData(data, bestilteMiljoer)

	const filteredData =
		tilgjengeligMiljoe && mergetData?.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift label="Alderspensjon" iconKind="pensjon" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke alderspensjon-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ?? mergetData}
				>
					<DataVisning ident={ident} />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
