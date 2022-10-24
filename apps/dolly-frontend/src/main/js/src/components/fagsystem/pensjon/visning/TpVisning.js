import React, { useEffect, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { fetchTpOrdninger } from '~/components/fagsystem/tjenestepensjon/form/Form'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'

export const sjekkManglerPensjonData = (tpData) => {
	return !tpData || !tpData.length
}

export const TpVisning = ({ data, loading }) => {
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
		return <Loading label="Laster TP forvalter-data" />
	}
	if (!data) {
		return null
	}

	const manglerFagsystemdata = sjekkManglerPensjonData(data)

	const ordningNavn = (nr) => {
		const ordning = ordninger.find((o) => o.value === nr)
		return ordning ? ordning.label : nr
	}

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
				<DollyFieldArray data={data} nested>
					{(ordning, idx) => (
						<div className="person-visning_content" key={idx}>
							<TitleValue size="large" title="Ordning" value={ordningNavn(ordning.ordning)} />
						</div>
					)}
				</DollyFieldArray>
			)}
		</ErrorBoundary>
	)
}
