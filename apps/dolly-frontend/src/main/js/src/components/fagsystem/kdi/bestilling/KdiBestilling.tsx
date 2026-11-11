import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { KdiMelding } from '@/components/fagsystem/kdi/visning/KdiVisning'
import { KdiMeldingProps, KdiProps } from '@/components/fagsystem/kdi/form/partials/types'
import {
	getMeldingTittel,
	grupperKdiHendelser,
	KdiHendelse,
} from '@/components/fagsystem/kdi/utils'
import { codeToNorskLabel } from '@/utils/DataFormatter'

type KdiBestillingProps = {
	kdi: KdiProps
	visMeldingTittel?: boolean
}

export const KdiBestilling = ({ kdi, visMeldingTittel = false }: KdiBestillingProps) => {
	if (!kdi) {
		return null
	}

	const annullering = kdi.annullering
	const hendelser = grupperKdiHendelser(kdi)

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>KDI-meldinger</BestillingTitle>
				<DollyFieldArray
					data={hendelser}
					getHeader={(hendelse: KdiHendelse) => codeToNorskLabel(hendelse.type)}
				>
					{(hendelse: KdiHendelse) => (
						<DollyFieldArray data={hendelse.meldinger} nested>
							{(melding: KdiMeldingProps, idx: number) => (
								<KdiMelding
									melding={melding}
									id={idx}
									annulleringListe={annullering}
									tittel={visMeldingTittel ? getMeldingTittel(melding) : undefined}
								/>
							)}
						</DollyFieldArray>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
