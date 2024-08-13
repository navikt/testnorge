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
import { showLabel } from '@/utils/DataFormatter'

export const sjekkManglerPensjonavtaleData = (pensjonData) => {
	return pensjonData?.length < 1
}

const Utbetalingsperioder = ({ utbetalingsperioder }) => {
	if (!utbetalingsperioder) return null

	return (
		<DollyFieldArray data={utbetalingsperioder} header="Utbetalingsperioder" whiteBackground nested>
			{(utbetalingsperiode, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Startalder" value={utbetalingsperiode?.startAlderAar} />
					<TitleValue
						title="Startmåned"
						value={showLabel('maanedsvelger', utbetalingsperiode?.startAlderMaaned)}
					/>
					<TitleValue title="Sluttalder" value={utbetalingsperiode?.sluttAlderAar} />
					<TitleValue
						title="Sluttmåned"
						value={showLabel('maanedsvelger', utbetalingsperiode?.sluttAlderMaaned)}
					/>
					<TitleValue
						title="Forventet årlig utbetaling"
						value={utbetalingsperiode?.aarligUtbetalingForventet}
					/>
				</div>
			)}
		</DollyFieldArray>
	)
}

const Pensjonsavtale = ({ data, setPanelOpen }) => {
	if (!data) return null

	const isPanelOpen = data?.length < 3

	return (
		<Panel
			startOpen={isPanelOpen || runningE2ETest()}
			heading={'Pensjonsavtaler'}
			setPanelOpen={setPanelOpen}
		>
			<DollyFieldArray data={data} nested>
				{(pensjonsavtale, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Produktbetegnelse" value={pensjonsavtale?.produktbetegnelse} />
						<TitleValue
							title="Avtalekategori"
							value={showLabel('avtaleKategori', pensjonsavtale?.kategori)}
						/>
						<Utbetalingsperioder utbetalingsperioder={pensjonsavtale?.utbetalingsperioder} />
					</div>
				)}
			</DollyFieldArray>
		</Panel>
	)
}

export const PensjonsavtaleVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_PENSJONSAVTALE')

	if (loading) {
		return <Loading label="Laster pensjonforvalter-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerPensjonavtaleData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data)?.miljo

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
					<Pensjonsavtale data={filteredData} />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
