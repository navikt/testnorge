import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import { runningTestcafe } from '~/service/services/Request'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '~/utils/hooks/useBestilling'

export const sjekkManglerPensjonData = (pensjonData) => {
	return pensjonData?.length < 1 || pensjonData?.every((miljoData) => miljoData?.data?.length < 1)
}

const getTittel = (data) => {
	const inntektsaar = data?.map((inntekt) => inntekt.inntektAar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return `Pensjonsgivende inntekter (${foerste} - ${siste})`
}

const PensjonInntekt = ({ data }) => {
	if (!data) return null

	return (
		<Panel startOpen={runningTestcafe()} heading={getTittel(data)}>
			<DollyFieldArray data={data} nested>
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

export const PensjonVisning = ({ data, loading, bestillingIdListe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'pensjonforvalter.inntekt')

	if (loading) {
		return <Loading label="Laster pensjonforvalter-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerPensjonData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo

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
					data={data}
				>
					<PensjonInntekt />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
