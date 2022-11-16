import React from 'react'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Formatters from '~/utils/DataFormatter'
import Loading from '~/components/ui/loading/Loading'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { sjekkManglerTpData } from '~/components/fagsystem/tjenestepensjon/visning/TpVisning'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'

const getSortedData = (data) => {
	return Array.isArray(data)
		? data.slice().sort(function (a, b) {
				const datoA = new Date(a.startdato)
				const datoB = new Date(b.startdato)

				return datoA < datoB ? 1 : datoA > datoB ? -1 : 0
		  })
		: data
}

export const sjekkManglerInstData = (instData) => {
	return instData?.length < 1 || instData?.every((miljoData) => miljoData.data?.length < 1)
}

export const InstVisning = ({ data, loading, bestilteMiljoer }) => {
	if (loading) {
		return <Loading label="Laster inst data" />
	}
	if (!data) {
		return null
	}

	// const sortedData = getSortedData(data)
	// const manglerFagsystemdata = sortedData?.length < 1

	const manglerFagsystemdata = sjekkManglerInstData(data)
	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Institusjonsopphold"
				iconKind="institusjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Kunne ikke hente institusjonsopphold-data på person
				</Alert>
			) : (
				<MiljoTabs bestilteMiljoer={bestilteMiljoer} forsteMiljo={forsteMiljo} data={data}>
					<DollyFieldArray nested>
						{(opphold, idx) => (
							<div className="person-visning_content" key={idx}>
								<TitleValue
									title="Institusjonstype"
									value={Formatters.showLabel('institusjonstype', opphold.institusjonstype)}
								/>
								<TitleValue
									title="Startdato"
									value={Formatters.formatStringDates(opphold.startdato)}
								/>
								<TitleValue
									title="Sluttdato"
									value={Formatters.formatStringDates(opphold.sluttdato)}
								/>
							</div>
						)}
					</DollyFieldArray>
				</MiljoTabs>
			)}
		</ErrorBoundary>
	)
}
