import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import Panel from '@/components/ui/panel/Panel'
import { runningCypressE2E } from '@/service/services/Request'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'

export const sjekkManglerPensjonData = (pensjonData) => {
	return pensjonData?.length < 1 || pensjonData?.every((miljoData) => miljoData?.data?.length < 1)
}

const getTittel = (data) => {
	const inntektsaar = data?.map((inntekt) => inntekt.inntektAar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return `Pensjonsgivende inntekter (${foerste} - ${siste})`
}

const PensjonInntekt = ({ data, isPanelOpen, setPanelOpen }) => {
	if (!data) return null
	const inntekter = data?.inntekter

	return (
		<Panel
			startOpen={isPanelOpen || runningCypressE2E()}
			heading={getTittel(inntekter)}
			setPanelOpen={setPanelOpen}
		>
			<DollyFieldArray data={inntekter} nested>
				{(inntekt, idx) => (
					<div className="person-visning_content" key={idx}>
						<TitleValue title="Inntektsår" value={inntekt?.inntektAar} />
						<TitleValue title="Beløp" value={inntekt?.belop} />
					</div>
				)}
			</DollyFieldArray>
		</Panel>
	)
}

export const PensjonVisning = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'PEN_INNTEKT')

	if (loading) {
		return <Loading label="Laster pensjonforvalter-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerPensjonData(data)

	const miljoerMedData = data?.map(
		(miljoData) => miljoData.data?.inntekter?.length > 0 && miljoData.miljo,
	)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.inntekter?.length > 0)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => item.miljo === tilgjengeligMiljoe)

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Pensjonsgivende inntekt (POPP)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke pensjon-data på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData || data}
				>
					<PensjonInntekt />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
