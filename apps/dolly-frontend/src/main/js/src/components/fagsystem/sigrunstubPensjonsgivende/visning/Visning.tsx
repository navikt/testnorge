import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { formatDate } from '@/utils/DataFormatter'

const InntektVisning = ({ pensjonsgivendeInntekt, id }) => {
	if (!pensjonsgivendeInntekt) {
		return null
	}

	return Object.entries(pensjonsgivendeInntekt)?.map(([key, value]) => {
		const label = kodeverkKeyToLabel(key)
		const erDato = !isNaN(Date.parse(value))

		if (erDato && (key.includes('Dato') || key.includes('dato'))) {
			return <TitleValue title={label} value={formatDate(value)} key={key + id} />
		}
		return <TitleValue title={label} value={value} key={key + id} />
	})
}

export const SigrunstubPensjonsgivendeVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster sigrunstub-data" />
	if (!data) {
		return null
	}
	const manglerFagsystemdata = data?.length < 1

	return (
		<>
			<SubOverskrift
				label="Pensjonsgivende inntekt (Sigrun)"
				iconKind="sigrun"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke data for pensjonsgivende inntekt på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content" style={{ marginTop: '-15px' }}>
						{data.map((inntekt, idx) => {
							return (
								<React.Fragment key={idx}>
									<DollyFieldArray
										data={inntekt.pensjonsgivendeInntekt}
										header={`Inntekter ${inntekt.inntektsaar}`}
										nested
									>
										{(pensjonsgivendeInntekt, idy) => (
											<React.Fragment key={idy}>
												<InntektVisning pensjonsgivendeInntekt={pensjonsgivendeInntekt} id={idy} />
											</React.Fragment>
										)}
									</DollyFieldArray>
								</React.Fragment>
							)
						})}
					</div>
				</ErrorBoundary>
			)}
		</>
	)
}
