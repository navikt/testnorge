import Loading from '@/components/ui/loading/Loading'
import SubOverskrift from '@/components/ui/subOverskrift/SubOverskrift'
import { Alert } from '@navikt/ds-react'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import React from 'react'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { useKodeverkOppfoelgingsvedtak14a } from '@/utils/hooks/useOppfoelgingsvedtak14a'
import { Oppfoelgingsvedtak14aTypes } from '@/components/fagsystem/oppfoelgingsvedtak14a/initialValues'

export const getOppfoelgingsvedtak14aLabel = (options: any, value: string) =>
	options?.find((item: any) => item.value === value || item.gammelKode === value)?.label ?? value

export const Oppfoelgingsvedtak14aVisning = ({
	data,
	loading,
	harBestilling,
}: {
	data: Oppfoelgingsvedtak14aTypes
	loading: boolean
	harBestilling: boolean
}) => {
	const { options: innsatsgruppeOptions } = useKodeverkOppfoelgingsvedtak14a('innsatsgruppe')
	const { options: hovedmalOptions } = useKodeverkOppfoelgingsvedtak14a('hovedmal')
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
							value={getOppfoelgingsvedtak14aLabel(innsatsgruppeOptions, data.innsatsgruppe)}
						/>
						<TitleValue
							title="Hovedmål"
							value={getOppfoelgingsvedtak14aLabel(hovedmalOptions, data.hovedmal)}
						/>
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
