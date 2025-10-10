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
import styled from 'styled-components'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { isSameDay } from 'date-fns'

export const sjekkManglerApData = (apData) => {
	return apData?.length < 1 || apData?.every((miljoData) => !miljoData.data)
}

const getNavEnhetLabel = (navEnheter, navEnhetId) => {
	return navEnheter?.find((enhet) => enhet.value === navEnhetId?.toString())?.label ?? navEnhetId
}

const StyledApVisning = styled.div`
	display: flex;
	flex-wrap: wrap;
	width: 100%;
	margin-bottom: 10px;

	&& {
		.title-value_small {
			flex: 0 1 141px;
		}
	}
`

const DataVisning = ({ data, miljo, apRevurderingData, apNyUttaksgradData, ident }) => {
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

	const getVedtakStatus = (vedtak) =>
		vedtak?.sisteOppdatering?.includes('opprettet') ? 'Iverksatt' : vedtak?.sisteOppdatering

	const revurdering = apRevurderingData?.filter((revurdering) => revurdering?.miljoe === miljo)
	const nyUttaksgrad = apNyUttaksgradData?.filter((uttaksgrad) => uttaksgrad?.miljoe === miljo)

	return (
		<>
			{!vedtakAP && !loading && (
				<StyledAlert variant={'warning'} size={'small'}>
					Kunne ikke hente vedtaksstatus for person. Dette skyldes enten at vedtaksstatus ikke er
					klar ennå, eller at opprettelse av alderspensjon har feilet.
				</StyledAlert>
			)}
			<StyledApVisning>
				<TitleValue title="Vedtaksstatus" value={getVedtakStatus(vedtakAP)} />
				<TitleValue title="Iverksettelsesdato" value={formatDate(data?.iverksettelsesdato)} />
				<TitleValue title="Saksbehandler" value={data?.saksbehandler} />
				<TitleValue title="Attesterer" value={data?.attesterer} />
				<TitleValue title="Uttaksgrad" value={`${data?.uttaksgrad}%`} />
				<TitleValue title="Nav-kontor" value={getNavEnhetLabel(navEnheter, data?.navEnhetId)} />

				<TitleValue
					title="Ektefelle/partners inntekt"
					value={data?.relasjonListe?.[0]?.sumAvForventetArbeidKapitalPensjonInntekt}
				/>
				<TitleValue title="Inkluder AFP privat" value={oversettBoolean(data?.inkluderAfpPrivat)} />
				<TitleValue
					title="AFP privat resultat"
					value={showLabel('afpPrivatResultat', data?.afpPrivatResultat)}
				/>
				{nyUttaksgrad?.length > 0 && (
					<DollyFieldArray data={nyUttaksgrad} header="Ny uttaksgrad" nested>
						{(nyUttaksgradItem, idx) => {
							const transaksjonId = nyUttaksgradItem.transaksjonId
							const vedtakNyUttaksgradItem = vedtakNyUttaksgrad?.find((v) =>
								isSameDay(new Date(v?.fom), new Date(transaksjonId?.fom)),
							)
							return (
								<div className="person-visning_content" style={{ marginBottom: 0 }} key={idx}>
									<TitleValue
										title="Vedtaksstatus"
										value={getVedtakStatus(vedtakNyUttaksgradItem)}
									/>
									<TitleValue title="Ny uttaksgrad" value={transaksjonId.nyUttaksgrad + '%'} />
									<TitleValue title="Dato f.o.m." value={formatDate(transaksjonId.fom)} />
									<TitleValue title="Saksbehandler" value={transaksjonId.saksbehandler} />
									<TitleValue title="Attesterer" value={transaksjonId.attesterer} />
									<TitleValue
										title="Nav-kontor"
										value={getNavEnhetLabel(navEnheter, transaksjonId.navEnhetId)}
									/>
								</div>
							)
						}}
					</DollyFieldArray>
				)}
				{revurdering?.length > 0 && (
					<DollyFieldArray data={revurdering} header="Revurdering" nested>
						{(revurderingItem, idx) => {
							const transaksjonId = revurderingItem.transaksjonId
							const vedtakRevurderingItem = vedtakRevurdering?.find((r) =>
								isSameDay(new Date(r?.fom), new Date(transaksjonId?.fom)),
							)
							return (
								<div className="person-visning_content" style={{ marginBottom: 0 }} key={idx}>
									<TitleValue
										title="Vedtaksstatus"
										value={getVedtakStatus(vedtakRevurderingItem)}
									/>
									<TitleValue
										title="Revurderingsårsak"
										value={codeToNorskLabel(transaksjonId.revurderingArsakType)}
									/>
									<TitleValue title="Dato f.o.m." value={formatDate(transaksjonId.fom)} />
									<TitleValue title="Saksbehandler" value={transaksjonId.saksbehandler} />
									<TitleValue title="Attesterer" value={transaksjonId.attesterer} />
									<TitleValue
										title="Nav-kontor"
										value={getNavEnhetLabel(navEnheter, transaksjonId.navEnhetId)}
									/>
								</div>
							)
						}}
					</DollyFieldArray>
				)}
			</StyledApVisning>
		</>
	)
}

export const AlderspensjonVisning = ({
	data,
	apRevurderingData,
	apNyUttaksgradData,
	loading,
	bestillingIdListe,
	tilgjengeligMiljoe,
	ident,
}) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_AP')

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

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

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
					data={filteredData ? filteredData : data}
				>
					<DataVisning
						apRevurderingData={apRevurderingData}
						apNyUttaksgradData={apNyUttaksgradData}
						ident={ident}
					/>
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
