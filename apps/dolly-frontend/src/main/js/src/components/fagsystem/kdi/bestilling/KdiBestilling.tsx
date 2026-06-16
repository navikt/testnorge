import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { KdiMelding } from '@/components/fagsystem/kdi/visning/KdiVisning'
import { publiseringstidspunktTid } from '@/components/fagsystem/kdi/form/Form'
import { KdiMeldingProps, KdiProps } from '@/components/fagsystem/kdi/form/partials/types'

export const KdiBestilling = ({ kdi }: { kdi: KdiProps }) => {
	if (!kdi) {
		return null
	}

	const annullering = kdi.annullering

	const meldinger = Object.entries(kdi)
		.flatMap(([type, values]) => values?.map((melding) => ({ ...melding, type })))
		?.filter((melding) => melding && melding.type !== 'annullering')
		?.sort(
			(a, b) =>
				publiseringstidspunktTid(a.publiseringstidspunkt) -
				publiseringstidspunktTid(b.publiseringstidspunkt),
		)

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>KDI-meldinger</BestillingTitle>
				<DollyFieldArray data={meldinger} nested>
					{(melding: KdiMeldingProps, idx: number) => (
						<KdiMelding melding={melding} id={idx} annulleringListe={annullering} />
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
