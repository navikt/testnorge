import React, { useEffect, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { fetchTpOrdninger } from '~/components/fagsystem/tjenestepensjon/form/Form'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'

export const sjekkManglerTpData = (tpData) => {
	return tpData?.length < 1 || tpData?.every((miljoData) => miljoData.data?.length < 1)
}

const TpOrdning = ({ data, ordninger }) => {
	if (!data) return null

	const ordningNavn = (nr) => {
		const ordning = ordninger.find((o) => o.value === nr)
		return ordning ? ordning.label : nr
	}

	return (
		<DollyFieldArray data={data} nested>
			{(ordning, idx) => (
				<div className="person-visning_content" key={idx}>
					<TitleValue size="large" title="Ordning" value={ordningNavn(ordning.ordning)} />
				</div>
			)}
		</DollyFieldArray>
	)
}

export const TpVisning = ({ data, loading, bestilteMiljoer }) => {
	const [ordninger, setOrdninger] = useState(Options('tpOrdninger'))

	useEffect(() => {
		if (!ordninger.length) {
			fetchTpOrdninger()
		}
	}, [])

	useEffect(() => {
		setOrdninger(Options('tpOrdninger'))
	}, [Options('tpOrdninger')])

	if (loading) {
		return <Loading label="Laster tjenestepensjon-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerTpData(data)

	const forsteMiljo = data.find((miljoData) => miljoData?.data?.length > 0)?.miljo

	return (
		<ErrorBoundary>
			<SubOverskrift
				label="Tjenestepensjon (TP)"
				iconKind="pensjon"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Kunne ikke hente tjenestepensjon-data på person
				</Alert>
			) : (
				<>
					<MiljoTabs bestilteMiljoer={bestilteMiljoer} forsteMiljo={forsteMiljo} data={data}>
						<TpOrdning ordninger={ordninger} />
					</MiljoTabs>
				</>
			)}
		</ErrorBoundary>
	)
}
