import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { KdiMelding } from '@/components/fagsystem/kdi/visning/KdiVisning'

export const KdiBestilling = ({ kdi }) => {
	if (!kdi) {
		return null
	}

	const {
		innsettelse,
		loeslatelse,
		avbruddStart,
		avbruddSlutt,
		forventetLoeslatelse,
		annullering,
	} = kdi

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>KDI-meldinger</BestillingTitle>
				{innsettelse?.length > 0 && (
					<DollyFieldArray data={innsettelse} header="Innsettelse">
						{(melding, idx) => (
							<KdiMelding
								melding={melding}
								id={'innsettelse' + idx}
								tidspunktLabel="Innsettelsestidspunkt"
							/>
						)}
					</DollyFieldArray>
				)}
				{loeslatelse?.length > 0 && (
					<DollyFieldArray data={loeslatelse} header="Løslatelse">
						{(melding, idx) => (
							<KdiMelding
								melding={melding}
								id={'loeslatelse' + idx}
								tidspunktLabel="Løslatelsestidspunkt"
							/>
						)}
					</DollyFieldArray>
				)}
				{avbruddStart?.length > 0 && (
					<DollyFieldArray data={avbruddStart} header="Avbrudd start">
						{(melding, idx) => (
							<KdiMelding
								melding={melding}
								id={'avbruddStart' + idx}
								tidspunktLabel="Tidspunkt for start på straffeavbrudd"
							/>
						)}
					</DollyFieldArray>
				)}
				{avbruddSlutt?.length > 0 && (
					<DollyFieldArray data={avbruddSlutt} header="Avbrudd slutt">
						{(melding, idx) => (
							<KdiMelding
								melding={melding}
								id={'avbruddSlutt' + idx}
								tidspunktLabel="Tidspunkt for slutt på straffeavbrudd"
							/>
						)}
					</DollyFieldArray>
				)}
				{forventetLoeslatelse?.length > 0 && (
					<DollyFieldArray data={forventetLoeslatelse} header="Forventet løslatelse">
						{(melding, idx) => (
							<KdiMelding
								melding={melding}
								id={'forventetLoeslatelse' + idx}
								tidspunktLabel="Forventet løslatt tidspunkt"
							/>
						)}
					</DollyFieldArray>
				)}
			</ErrorBoundary>
		</div>
	)
}
