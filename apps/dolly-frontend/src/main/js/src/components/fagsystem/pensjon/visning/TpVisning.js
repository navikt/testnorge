import React, { useEffect, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert, Tabs } from '@navikt/ds-react'
import { fetchTpOrdninger } from '~/components/fagsystem/tjenestepensjon/form/Form'
import { SelectOptionsManager as Options } from '~/service/SelectOptions'
import { TpMiljoeinfo } from '~/components/fagsystem/pensjon/visning/TpMiljoeinfo'
import { useAsync } from 'react-use'
import { DollyApi } from '~/service/Api'
import { MiljoTabs } from '~/components/ui/miljoTabs/MiljoTabs'

export const sjekkManglerPensjonData = (tpData) => {
	console.log('tpData: ', tpData) //TODO - SLETT MEG
	// return !tpData?.value || !tpData?.value?.length
	return !tpData?.value
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
	console.log('data: ', data) //TODO - SLETT MEG
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

	// const tpMiljoer = useAsync(async () => {
	// 	return DollyApi.getTpMiljoer()
	// }, [])

	const manglerFagsystemdata = sjekkManglerPensjonData(data)
	// console.log('data: ', data) //TODO - SLETT MEG
	// console.log('bestilteMiljoer: ', bestilteMiljoer) //TODO - SLETT MEG
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
					<MiljoTabs
						// miljoListe={tpMiljoer}
						miljoListe={['q1', 'q2', 'q4']}
						bestilteMiljoer={bestilteMiljoer}
						forsteMiljo="q1"
						data={data}
					>
						<TpOrdning data={data} ordninger={ordninger} />
					</MiljoTabs>
					{/*<Tabs size="small" defaultValue="q1">*/}
					{/*	<Tabs.List>*/}
					{/*		/!*<Tabs.Tab value="q1" label="Q1" icon={<Icon kind={'feedback-check-circle'} />} />*!/*/}
					{/*		<Tabs.Tab value="q1" label="Q1" style={{ color: '#06893a', fontWeight: 'bold' }} />*/}
					{/*		<Tabs.Tab value="q2" label="Q2" />*/}
					{/*		<Tabs.Tab value="q3" label="Q3" />*/}
					{/*	</Tabs.List>*/}
					{/*	<Tabs.Panel value="q1" style={{ marginTop: '10px' }}>*/}
					{/*		<TpOrdning data={data} ordninger={ordninger} />*/}
					{/*	</Tabs.Panel>*/}
					{/*	<Tabs.Panel value="q2" style={{ marginTop: '10px', marginBottom: '15px' }}>*/}
					{/*		<Alert variant="info" size="small" inline>*/}
					{/*			Fant ingen pensjon-data i dette miljøet*/}
					{/*		</Alert>*/}
					{/*	</Tabs.Panel>*/}
					{/*	<Tabs.Panel value="q3" style={{ marginTop: '10px', marginBottom: '15px' }}>*/}
					{/*		<Alert variant="info" size="small" inline>*/}
					{/*			Fant ingen pensjon-data i dette miljøet*/}
					{/*		</Alert>*/}
					{/*	</Tabs.Panel>*/}
					{/*</Tabs>*/}
				</>
			)}
			<TpMiljoeinfo ident={ident} bestilteMiljoer={bestilteMiljoer} ordninger={ordninger} />
		</ErrorBoundary>
	)
}

export const TpvisningMiljo = ({ data, miljoe, ordninger }) => {
	const tpMiljoeInfo = data.find((m) => m.miljo === miljoe.toString())

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
