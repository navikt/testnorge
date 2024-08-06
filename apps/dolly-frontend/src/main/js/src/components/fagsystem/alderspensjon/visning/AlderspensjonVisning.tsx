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

export const sjekkManglerApData = (apData) => {
	return apData?.length < 1 || apData?.every((miljoData) => !miljoData.data)
}

const DataVisning = ({ data, miljo }) => {
	const { navEnheter } = useNavEnheter()
	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === data?.navEnhetId?.toString(),
	)?.label

	const { vedtakData } = usePensjonVedtak(data?.fnr, miljo)
	console.log('vedtakData', vedtakData)

	return (
		<>
			<div className="person-visning_content">
				<TitleValue
					title="Vedtaksstatus"
					value={
						vedtakData?.[0]?.vedtakStatus !== 'FEILET'
							? vedtakData?.[0]?.vedtakStatus
							: vedtakData?.[0]?.sisteOppdatering
					}
				/>
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

export const AlderspensjonVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
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
					<DataVisning />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
