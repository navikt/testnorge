import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Panel from '@/components/ui/panel/Panel'
import { runningE2ETest } from '@/service/services/Request'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'

export const sjekkManglerPensjonData = (pensjonData) => {
	return (
		pensjonData?.length < 1 ||
		pensjonData?.every((miljoData) => miljoData?.data?.utbetalingsperioder?.length < 1)
	)
}

const Utbetalingsperioder = ({ utbetalingsperioder, isPanelOpen, setPanelOpen }) => {
	if (!utbetalingsperioder) return null

	return (
		<DollyFieldArray data={utbetalingsperioder} header="Utbetalingsperioder" whiteBackground nested>
			{(utbetalingsperiode, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Startalder" value={utbetalingsperiode?.startAlderAar} />
					<TitleValue title="Startmåned" value={utbetalingsperiode?.startAlderMaaneder} />
					<TitleValue title="Sluttalder" value={utbetalingsperiode?.sluttAlderAar} />
					<TitleValue title="Sluttmåned" value={utbetalingsperiode?.sluttAlderMaaneder} />
					<TitleValue
						title="Forventet årlig utbetaling"
						value={utbetalingsperiode?.aarligUtbetalingForventet}
					/>
					<TitleValue title="Grad" value={utbetalingsperiode?.grad} />
				</div>
			)}
		</DollyFieldArray>
	)
}

const Pensjonsavtale = ({ data, isPanelOpen, setPanelOpen }) => {
	if (!data) return null
	console.log('data', data)
	console.log('isPanelOpen', isPanelOpen)
	console.log('setPanelOpen', setPanelOpen)
	return (
		<Panel
			startOpen={true || runningE2ETest()}
			heading={'Pensjonsavtaler'}
			setPanelOpen={setPanelOpen}
		>
			<DollyFieldArray data={data} nested>
				{(pensjonsavtale, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Produktbetegnelse" value={pensjonsavtale?.produktbetegnelse} />
						<TitleValue title="Produktkategori" value={pensjonsavtale?.kategori} />
						<TitleValue title="Startår" value={pensjonsavtale?.startAar} />
						<TitleValue title="Sluttår" value={pensjonsavtale?.sluttAar} />
						<Utbetalingsperioder utbetalingsperioder={pensjonsavtale?.utbetalingsperioder} />
					</div>
				)}
			</DollyFieldArray>
		</Panel>
	)
}

export const PensjonsavtaleVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_INNTEKT')

	if (loading) {
		return <Loading label="Laster pensjonforvalter-data" />
	}
	if (!data) {
		return null
	}

	console.log('data', data)

	const manglerFagsystemdata = sjekkManglerPensjonData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

	console.log('bestilteMiljoer', bestilteMiljoer)

	console.log('foresteMiljo', forsteMiljo)

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Pensjonsavtaler (PEN)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke pensjonsavtale-data for person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData || data}
				>
					<Pensjonsavtale data={filteredData} isPanelOpen={true} />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
