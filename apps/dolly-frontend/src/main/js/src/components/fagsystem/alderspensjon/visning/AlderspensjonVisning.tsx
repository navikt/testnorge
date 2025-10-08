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
import { useTransaksjonIdData } from '@/utils/hooks/useFagsystemer'
import styled from 'styled-components'

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
			flex: 0 1 149px;
		}
	}
`

const DataVisning = ({ data, miljo, ident }) => {
	const { navEnheter } = useNavEnheter()

	const { vedtakData, loading } = usePensjonVedtak(ident, miljo)
	const vedtakAP = vedtakData?.find(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'FORSTEGANGSBEHANDLING',
	)
	const vedtakNyUttaksgrad = vedtakData?.find(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'UTTAKSGRADSENDRING',
	)
	const vedtakRevurdering = vedtakData?.find(
		(vedtak) => vedtak?.sakType === 'AP' && vedtak.kravtype === 'SIVILSTANDENDRING',
	)

	const getVedtakStatus = (vedtak) =>
		vedtak?.sisteOppdatering?.includes('opprettet') ? 'Iverksatt' : vedtak?.sisteOppdatering

	const { loading: loadingApRevurderingData, data: apRevurderingData } = useTransaksjonIdData(
		data.fnr,
		'PEN_AP_REVURDERING',
		true,
		[miljo],
	)
	const revurdering = apRevurderingData?.find((revurdering) => revurdering?.miljo === miljo)?.data

	const { loading: loadingApNyUttaksgradData, data: apNyUttaksgradData } = useTransaksjonIdData(
		data.fnr,
		'PEN_AP_NY_UTTAKSGRAD',
		true,
		[miljo],
	)
	const nyUttaksgrad = apNyUttaksgradData?.find((uttaksgrad) => uttaksgrad?.miljo === miljo)?.data

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
				{revurdering && (
					<div className="person-visning_content" style={{ marginBottom: 0 }}>
						<h4>Revurdering</h4>
						<div className="person-visning_content" style={{ marginBottom: 0 }}>
							<TitleValue title="Vedtaksstatus" value={getVedtakStatus(vedtakRevurdering)} />
							<TitleValue
								title="Revurderingsårsak"
								value={codeToNorskLabel(revurdering.revurderingArsakType)}
							/>
							<TitleValue title="Dato f.o.m." value={formatDate(revurdering.fom)} />
							<TitleValue title="Saksbehandler" value={revurdering.saksbehandler} />
							<TitleValue title="Attesterer" value={revurdering.attesterer} />
							<TitleValue
								title="Nav-kontor"
								value={getNavEnhetLabel(navEnheter, revurdering.navEnhetId)}
							/>
						</div>
					</div>
				)}
				{nyUttaksgrad && (
					<div className="person-visning_content" style={{ marginBottom: 0 }}>
						<h4>Ny uttaksgrad</h4>
						<div className="person-visning_content" style={{ marginBottom: 0 }}>
							<TitleValue title="Vedtaksstatus" value={getVedtakStatus(vedtakNyUttaksgrad)} />
							<TitleValue title="Ny uttaksgrad" value={nyUttaksgrad.nyUttaksgrad + '%'} />
							<TitleValue title="Dato f.o.m." value={formatDate(nyUttaksgrad.fom)} />
							<TitleValue title="Saksbehandler" value={nyUttaksgrad.saksbehandler} />
							<TitleValue title="Attesterer" value={nyUttaksgrad.attesterer} />
							<TitleValue
								title="Nav-kontor"
								value={getNavEnhetLabel(navEnheter, nyUttaksgrad.navEnhetId)}
							/>
						</div>
					</div>
				)}
			</StyledApVisning>
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
					<DataVisning ident={ident} />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
