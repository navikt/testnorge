import React, { useEffect, useState } from 'react'
import SubOverskrift from '~/components/ui/subOverskrift/SubOverskrift'
import { TitleValue } from '~/components/ui/titleValue/TitleValue'
import Loading from '~/components/ui/loading/Loading'
import { DollyFieldArray } from '~/components/ui/form/fieldArray/DollyFieldArray'
import { ErrorBoundary } from '~/components/ui/appError/ErrorBoundary'
import { Alert, Tabs } from '@navikt/ds-react'
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
				<Tabs size="small" defaultValue="q1">
					<Tabs.List>
						{/*<Tabs.Tab value="q1" label="Q1" icon={<Icon kind={'feedback-check-circle'} />} />*/}
						<Tabs.Tab value="q1" label="Q1" style={{ color: '#06893a', fontWeight: 'bold' }} />
						<Tabs.Tab value="q2" label="Q2" />
						<Tabs.Tab value="q3" label="Q3" />
					</Tabs.List>
					<Tabs.Panel value="q1" style={{ marginTop: '10px' }}>
						<DollyFieldArray data={data} nested>
							{(ordning, idx) => (
								<div className="person-visning_content" key={idx}>
									<TitleValue size="large" title="Ordning" value={ordningNavn(ordning.ordning)} />
								</div>
							)}
						</DollyFieldArray>
					</Tabs.Panel>
					<Tabs.Panel value="q2" style={{ marginTop: '10px', marginBottom: '15px' }}>
						<Alert variant="info" size="small" inline>
							Fant ingen pensjon-data i dette miljøet
						</Alert>
					</Tabs.Panel>
					<Tabs.Panel value="q3" style={{ marginTop: '10px', marginBottom: '15px' }}>
						<Alert variant="info" size="small" inline>
							Fant ingen pensjon-data i dette miljøet
						</Alert>
					</Tabs.Panel>
				</Tabs>
			)}
		</ErrorBoundary>
	)
}
