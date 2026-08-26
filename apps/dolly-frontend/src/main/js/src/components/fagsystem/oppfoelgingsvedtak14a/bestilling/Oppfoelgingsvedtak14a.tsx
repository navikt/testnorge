import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { formatDate } from '@/utils/DataFormatter'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Oppfoelgingsvedtak14aTypes } from '@/components/fagsystem/oppfoelgingsvedtak14a/initialValues'
import { useNavEnheter } from '@/utils/hooks/useNorg2'
import { useKodeverkOppfoelgingsvedtak14a } from '@/utils/hooks/useOppfoelgingsvedtak14a'
import React from 'react'
import { getOppfoelgingsvedtak14aLabel } from '@/components/fagsystem/oppfoelgingsvedtak14a/visning/Oppfoelgingsvedtak14aVisning'

export const Oppfoelgingsvedtak14a = ({ vedtak }: { vedtak: Oppfoelgingsvedtak14aTypes }) => {
	const { options: innsatsgruppeOptions } = useKodeverkOppfoelgingsvedtak14a('innsatsgruppe')
	const { options: hovedmalOptions } = useKodeverkOppfoelgingsvedtak14a('hovedmal')
	const { navEnheter } = useNavEnheter()

	if (!vedtak) {
		return null
	}

	const getNavEnhetLabel = (navEnhetId?: string) => {
		return navEnheter?.find((enhet: any) => enhet.value === navEnhetId)?.label ?? navEnhetId
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Oppfølgingsvedtak § 14 a</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue
							title="Innsatsgruppe"
							value={getOppfoelgingsvedtak14aLabel(innsatsgruppeOptions, vedtak.innsatsgruppe)}
						/>
						<TitleValue
							title="Hovedmål"
							value={getOppfoelgingsvedtak14aLabel(hovedmalOptions, vedtak.hovedmal)}
						/>
						<TitleValue title="Vedtak fattet" value={formatDate(vedtak.vedtakFattet)} />
						<TitleValue
							title="Oppfølgingsenhet"
							value={getNavEnhetLabel(vedtak.oppfolgingsEnhet)}
						/>
						<TitleValue title="Begrunnelse" value={vedtak.begrunnelse} />
						<TitleValue title="Veileder ident" value={vedtak.veilederIdent} />
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
