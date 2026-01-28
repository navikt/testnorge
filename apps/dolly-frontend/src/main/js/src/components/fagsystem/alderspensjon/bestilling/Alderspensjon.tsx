import { isEmpty } from '@/components/fagsystem/pdlf/form/partials/utils'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import {
	BestillingData,
	BestillingTitle,
} from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate, oversettBoolean, showLabel } from '@/utils/DataFormatter'
import React from 'react'
import { AlderspensjonTypes } from '@/components/fagsystem/alderspensjon/AlderspensjonTypes'
import { useNavEnheter } from '@/utils/hooks/useNorg2'

type AlderspensjonProps = {
	pensjon?: AlderspensjonTypes
}

export const Alderspensjon = ({ pensjon }: AlderspensjonProps) => {
	const { navEnheter } = useNavEnheter()

	if (!pensjon || isEmpty(pensjon)) {
		return null
	}

	const navEnhetLabel = navEnheter?.find(
		(enhet) => enhet.value === pensjon.navEnhetId?.toString(),
	)?.label

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>
					{'Alderspensjon: ' + (pensjon?.soknad ? 'Søknad' : 'Vedtak')}
				</BestillingTitle>
				<div className="bestilling-blokk">
					<BestillingData>
						<TitleValue
							title="Iverksettelsesdato"
							value={formatDate(pensjon?.iverksettelsesdato)}
						/>
						<TitleValue title="Saksbehandler" value={pensjon?.saksbehandler} />
						<TitleValue title="Attesterer" value={pensjon?.attesterer} />
						<TitleValue title="Uttaksgrad" value={`${pensjon?.uttaksgrad}%`} />
						<TitleValue title="NAV-kontor" value={navEnhetLabel || pensjon?.navEnhetId} />
						<TitleValue
							title="Ektefelle/partners inntekt"
							value={pensjon?.relasjoner?.[0]?.sumAvForvArbKapPenInntekt}
						/>
						<TitleValue
							title="Inkluder AFP privat"
							value={oversettBoolean(pensjon?.inkluderAfpPrivat)}
						/>
						<TitleValue
							title="AFP privat resultat"
							value={showLabel('afpPrivatResultat', pensjon?.afpPrivatResultat)}
						/>
					</BestillingData>
				</div>
			</ErrorBoundary>
		</div>
	)
}
