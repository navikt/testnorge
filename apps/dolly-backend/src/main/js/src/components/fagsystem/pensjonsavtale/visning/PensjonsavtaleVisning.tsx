import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import React from 'react'
import { formatDate } from '@/utils/DataFormatter'
import Loading from '@/components/ui/loading/Loading'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { usePensjonVedtak } from '@/utils/hooks/usePensjon'

export const sjekkManglerPensjonsavtaleData = (pensjonsavtaleData) => {
	return pensjonsavtaleData?.length < 1 || pensjonsavtaleData?.every((miljoData) => !miljoData.data)
}

const DataVisning = ({ data, miljo }) => {
	const { navEnheter } = useNavEnheter()
	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === data?.navEnhetId?.toString(),
	)?.label

	const { vedtakData } = usePensjonVedtak(data?.fnr, miljo)

	return (
		<>
			<div className="person-visning_content">
				<TitleValue title="Vedtaksstatus" value={vedtakData?.[0]?.vedtakStatus} />
				<TitleValue title="Krav fremsatt dato" value={formatDate(data?.kravFremsattDato)} />
				<TitleValue title="Iverksettelsesdato" value={formatDate(data?.iverksettelsesdato)} />
				<TitleValue title="Saksbehandler" value={data?.saksbehandler} />
				<TitleValue title="Attesterer" value={data?.attesterer} />
				<TitleValue title="Uttaksgrad" value={`${data?.uttaksgrad}%`} />
				<TitleValue title="NAV-kontor" value={navEnhetLabel || data?.navEnhetId} />

				<TitleValue
					title="Ektefelle/partners inntekt"
					value={data?.relasjonListe?.[0]?.sumAvForventetArbeidKapitalPensjonInntekt}
				/>
			</div>
		</>
	)
}

export const PensjonsavtaleVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_PENSJONSAVTALE')

	if (loading) {
		return <Loading label="Laster pensjonsavtale-data" />
	}

	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerPensjonsavtaleData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer?.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift label="Pensjonsavtale" iconKind="pensjon" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke pensjonsavtale-data på person
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
