import Loading from '@/components/ui/loading/Loading'
import React from 'react'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { formatDate } from '@/utils/DataFormatter'
import { Alert } from '@navikt/ds-react'

const SummertSkattegrunnlagVisning = ({ summertSkattegrunnlag, idx }) => {
	if (!summertSkattegrunnlag) {
		return null
	}

	return Object.entries(summertSkattegrunnlag)?.map(([key, value]) => {
		const label = kodeverkKeyToLabel(key)
		const erDato = !isNaN(Date.parse(value))

		if (erDato && (key.includes('Dato') || key.includes('dato'))) {
			return <TitleValue title={label} value={formatDate(value)} key={key} />
		}
		return <TitleValue title={label} value={value} key={key} />
	})
}

export const SigrunstubSummertSkattegrunnlagVisning = ({ data, loading }) => {
	if (loading) return <Loading label="Laster sigrunstub-data" />
	if (!data) {
		return null
	}
	const manglerFagsystemdata = data?.length < 1

	return (
		<>
			<SubOverskrift label="Summert skattegrunnlag (Sigrun)" iconKind="sigrun" />
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke data for summert skattegrunnlag på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content" style={{ marginTop: '-15px' }}>
						{data.map((skattegrunnlag, idx) => {
							return (
								<React.Fragment key={idx}>
									<SummertSkattegrunnlagVisning summertSkattegrunnlag={skattegrunnlag} idx={idx} />
								</React.Fragment>
							)
						})}
					</div>
				</ErrorBoundary>
			)}
		</>
	)
}
