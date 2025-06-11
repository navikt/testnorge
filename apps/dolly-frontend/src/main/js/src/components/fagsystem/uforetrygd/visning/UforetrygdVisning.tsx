import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import styled from 'styled-components'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { usePensjonVedtak } from '@/utils/hooks/usePensjon'
import StyledAlert from '@/components/ui/alert/StyledAlert'

const BarnetilleggSamlet = styled.div`
	margin: 0 0 15px 0;
	width: 100%;
`
export const sjekkManglerUforetrygdData = (uforetrygdData) => {
	return (
		uforetrygdData?.length < 1 ||
		uforetrygdData?.every(
			(miljoData) => !miljoData.data || Object.keys(miljoData?.data)?.length < 1,
		)
	)
}

const BarnetilleggInntektVisning = ({ data, tittel }) => {
	if (!data || data.length === 0) {
		return null
	}

	return (
		<>
			<h4 style={{ margin: '16px 0' }}>{tittel}</h4>
			<DollyFieldArray data={data} nested>
				{(inntekt, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue
							title="Type inntekt"
							value={showLabel('inntektType', inntekt?.inntektType)}
						/>
						<TitleValue title="Beløp" value={inntekt?.belop} />
						<TitleValue title="Dato f.o.m." value={formatDate(inntekt?.datoFom)} />
						<TitleValue title="Dato t.o.m." value={formatDate(inntekt?.datoTom)} />
					</div>
				)}
			</DollyFieldArray>
		</>
	)
}

const DataVisning = ({ data, miljo }) => {
	const { navEnheter } = useNavEnheter()
	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === data?.navEnhetId?.toString(),
	)?.label

	const { vedtakData, loading } = usePensjonVedtak(data?.fnr, miljo)
	const vedtakUT = vedtakData?.find((vedtak) => vedtak?.sakType === 'UT')

	const getSisteOppdatering = (sisteOppdatering: string) => {
		if (sisteOppdatering?.includes('opprettet')) {
			return 'Iverksatt'
		} else if (sisteOppdatering?.indexOf('<') > 0) {
			return sisteOppdatering?.substring(0, sisteOppdatering?.indexOf('<'))
		} else if (sisteOppdatering?.indexOf('{') > 0) {
			return sisteOppdatering?.substring(0, sisteOppdatering?.indexOf('{'))
		} else {
			return sisteOppdatering
		}
	}

	return (
		<>
			{!vedtakUT && !loading && (
				<StyledAlert variant={'warning'} size={'small'}>
					Kunne ikke hente vedtaksstatus for person. Dette skyldes enten at vedtaksstatus ikke er
					klar ennå, eller at opprettelse av uføresak har feilet.
				</StyledAlert>
			)}
			<div className="person-visning_content">
				<TitleValue title="Vedtaksstatus" value={getSisteOppdatering(vedtakUT?.sisteOppdatering)} />
				<TitleValue title="Uføretidspunkt" value={formatDate(data?.uforetidspunkt)} />
				<TitleValue title="Krav fremsatt dato" value={formatDate(data?.kravFremsattDato)} />
				<TitleValue title="Ønsket virkningsdato" value={formatDate(data?.onsketVirkningsDato)} />
				<TitleValue title="Inntekt før uførhet" value={data?.inntektForUforhet} />
				<TitleValue title="Inntekt etter uførhet" value={data?.inntektEtterUforhet} />
				<TitleValue
					title="Type barnetillegg"
					value={showLabel('barnetilleggType', data?.barnetilleggDetaljer?.barnetilleggType)}
				/>
				<BarnetilleggSamlet>
					<BarnetilleggInntektVisning
						data={data?.barnetilleggDetaljer?.forventedeInntekterSoker}
						tittel="Forventede inntekter for søker"
					/>
					<BarnetilleggInntektVisning
						data={data?.barnetilleggDetaljer?.forventedeInntekterEP}
						tittel="Forventede inntekter for partner"
					/>
				</BarnetilleggSamlet>
				<TitleValue
					title="Sats for minimum IFU"
					value={showLabel('minimumInntektForUforhetType', data?.minimumInntektForUforhetType)}
				/>
				<TitleValue title="Uføregrad" value={data?.uforegrad ? `${data?.uforegrad}%` : null} />
				<TitleValue title="Saksbehandler" value={data?.saksbehandler} />
				<TitleValue title="Attesterer" value={data?.attesterer} />
				<TitleValue title="NAV-kontor" value={navEnhetLabel || data?.navEnhetId} />
			</div>
		</>
	)
}

export const UforetrygdVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_UT')

	if (loading) {
		return <Loading label="Laster uføretrygd-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerUforetrygdData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift label="Uføretrygd" iconKind="pensjon" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke uføretrygd-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData ? filteredData : data}
				>
					<DataVisning />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
