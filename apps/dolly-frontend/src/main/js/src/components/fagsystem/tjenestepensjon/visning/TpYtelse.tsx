import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import Loading from '@/components/ui/loading/Loading'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { MiljoTabs } from '@/components/ui/miljoTabs/MiljoTabs'
import { useBestilteMiljoer } from '@/utils/hooks/useBestilling'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { sjekkManglerTpData } from '@/components/fagsystem/tjenestepensjon/visning/TpVisning'

const TpYtelseVisning = ({ data }) => {
	if (!data) return null

	return (
		<DollyFieldArray data={data} nested>
			{(ytelse, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue title="Type" value={showLabel('tjenestepensjonYtelseType', ytelse?.type)} />
					<TitleValue title="Dato innmeldt" value={formatDate(ytelse?.datoInnmeldtYtelseFom)} />
					<TitleValue
						title="Dato iverksatt f.o.m"
						value={formatDate(ytelse?.datoYtelseIverksattFom)}
					/>
					<TitleValue
						title="Dato iverksatt t.o.m"
						value={formatDate(ytelse?.datoYtelseIverksattTom)}
					/>
				</div>
			)}
		</DollyFieldArray>
	)
}

export const TpYtelse = ({ data, loading, bestillingIdListe, tilgjengeligMiljoe }) => {
	const { bestilteMiljoer } = useBestilteMiljoer(bestillingIdListe, 'TP_FORVALTER')

	if (loading) {
		return <Loading label="Laster pensjonsytelser" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerTpData(data)

	const miljoerMedData = data?.map((miljoData) => miljoData.data?.length > 0 && miljoData.miljo)
	const errorMiljoer = bestilteMiljoer.filter((miljo) => !miljoerMedData?.includes(miljo))

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	const filteredData =
		tilgjengeligMiljoe && data.filter((item) => tilgjengeligMiljoe.includes(item.miljo))

	return (
		<ErrorBoundary>
			<SubOverskrift label="Pensjonsytelser" iconKind="pensjon" isWarning={manglerFagsystemdata} />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke tjenestepensjon-ytelser på person
				</Alert>
			) : (
				<MiljoTabs
					bestilteMiljoer={bestilteMiljoer}
					errorMiljoer={errorMiljoer}
					forsteMiljo={forsteMiljo}
					data={filteredData || data}
				>
					<TpYtelseVisning />
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
