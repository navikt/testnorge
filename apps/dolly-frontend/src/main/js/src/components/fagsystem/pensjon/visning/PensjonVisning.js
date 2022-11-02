import React from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import Panel from '~/components/ui/panel/Panel'
import { runningTestcafe } from '~/service/services/Request'
import { Alert } from '@navikt/ds-react'
import { PoppMiljoeinfo } from '~/components/fagsystem/pensjon/visning/PoppMiljoeinfo'

export const sjekkManglerPensjonData = (pensjonData) => {
	return !pensjonData?.inntekter || pensjonData?.inntekter?.length === 0
}

const PensjonInntekt = ({ data }) => {
	if (!data) return null

	return (
		<DollyFieldArray data={data.inntekter} nested>
			{(inntekt, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Inntektsår" value={inntekt?.inntektAar} />
					<TitleValue title="Beløp" value={inntekt?.belop} />
				</div>
			)}
		</DollyFieldArray>
	)
}

export const PensjonVisning = ({ data, loading, bestilteMiljoer }) => {
	if (loading) return <Loading label="Laster pensjonforvalter-data" />
	if (!data) return null

	const manglerFagsystemdata = sjekkManglerPensjonData(data)

	const inntektsaar = data?.inntekter?.map((inntekt) => inntekt.inntektAar)
	const foerste = Math.min(...inntektsaar)
	const siste = Math.max(...inntektsaar)
	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Pensjonsgivende inntekt (POPP)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Kunne ikke hente pensjon-data på person
				</Alert>
			) : (
				<Panel
					startOpen={runningTestcafe()}
					heading={`Pensjonsgivende inntekter (${foerste} - ${siste})`}
				>
					<PensjonInntekt data={data} />
				</Panel>
			)}
			<PoppMiljoeinfo ident={data?.fnr} bestilteMiljoer={bestilteMiljoer} />
		</ErrorBoundary>
	)
}

export const PensjonvisningMiljo = ({ data, miljoe }) => {
	const pensjonMiljoeInfo = data.find((m) => m.miljo == miljoe.toString())
	// TODO sett tittel her
	// TODO sett info for tomt miljø
	console.log('pensjonMiljoeInfo: ', pensjonMiljoeInfo) //TODO - SLETT MEG
	return (
		<div className="boks">
			<PensjonInntekt data={pensjonMiljoeInfo} />
		</div>
	)
}
