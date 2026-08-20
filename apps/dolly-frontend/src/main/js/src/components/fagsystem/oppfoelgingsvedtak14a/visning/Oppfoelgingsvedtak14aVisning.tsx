import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import React from 'react'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

export const Oppfoelgingsvedtak14aVisning = ({ data, loading, harBestilling }) => {
	const { navEnheter } = useNavEnheter()

	if (loading) {
		return <Loading label="Laster Oppfølgingsvedtak § 14 a ..." />
	}

	if (!data && !harBestilling) {
		return null
	}

	const manglerFagsystemdata = harBestilling && !data

	const getNavEnhetLabel = (navEnhetId?: string) => {
		return navEnheter?.find((enhet: any) => enhet.value === navEnhetId)?.label ?? navEnhetId
	}

	return (
		<div>
			<SubOverskrift
				label="Oppfølgingsvedtak § 14 a"
				iconKind="arena"
				isWarning={manglerFagsystemdata}
			/>
			{manglerFagsystemdata ? (
				<Alert variant={'warning'} size={'small'} inline style={{ marginBottom: '20px' }}>
					Fant ikke Oppfølgingsvedtak § 14 a på person
				</Alert>
			) : (
				<ErrorBoundary>
					<div className="person-visning_content">
						<TitleValue
							title="Innsatsgruppe"
							value={showLabel('innsatsgruppe', data.innsatsgruppe)}
						/>
						<TitleValue title="Hovedmål" value={showLabel('hovedmal', data.hovedmal)} />
						<TitleValue title="Vedtak fattet" value={formatDate(data.vedtakFattet)} />
						<TitleValue
							title="Oppfølgingsenhet"
							value={getNavEnhetLabel(data.oppfolgingsenhetId)}
						/>
						<TitleValue title="Begrunnelse" value={data.begrunnelse} />
						<TitleValue title="Veileder ident" value={data.veilederIdent} />
					</div>
				</ErrorBoundary>
			)}
		</div>
	)
}
