import React, { useEffect, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert } from '@navikt/ds-react'
import { fetchTpOrdninger } from '~/components/fagsystem/tjenestepensjon/form/Form'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { TpMiljoeinfo } from '~/components/fagsystem/pensjon/visning/TpMiljoeinfo'

export const sjekkManglerPensjonData = (tpData) => {
	return !tpData || !tpData.length
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

export const TpVisning = ({ ident, data, loading, bestilteMiljoer }) => {
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
				<TpOrdning data={data} ordninger={ordninger} />
			)}
			<TpMiljoeinfo ident={ident} bestilteMiljoer={bestilteMiljoer} ordninger={ordninger} />
		</ErrorBoundary>
	)
}

export const TpvisningMiljo = ({ data, miljoe, ordninger }) => {
	const tpMiljoeInfo = data.find((m) => m.miljo == miljoe.toString())

	return !tpMiljoeInfo || !tpMiljoeInfo?.ordninger || tpMiljoeInfo?.ordninger?.length < 1 ? (
		<Alert variant="info" size="small" inline>
			Fant ingen tjenestepensjon-data i dette miljøet
		</Alert>
	) : (
		<div className="boks">
			<SubOverskrift label="Tjenestepensjon (TP)" iconKind="pensjon" />
			<TpOrdning data={tpMiljoeInfo?.ordninger} ordninger={ordninger} />
		</div>
	)
}
