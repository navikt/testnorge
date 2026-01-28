import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { BestillingTitle } from '@/components/bestillingsveileder/stegVelger/steg/steg3/Bestillingsvisning'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { showLabel } from '@/utils/DataFormatter'
import {
	PensjonsavtaleTypes,
	Utbetalingsperiode,
} from '@/components/fagsystem/pensjonsavtale/PensjonsavtaleTypes'

type PensjonsavtaleProps = {
	pensjon: Array<PensjonsavtaleTypes>
}

export const Pensjonsavtale = ({ pensjon }: PensjonsavtaleProps) => {
	if (!pensjon || pensjon?.length < 1) {
		return null
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Pensjonsavtale (PEN)</BestillingTitle>
				<DollyFieldArray header="Pensjonsavtale" data={pensjon}>
					{(pensjonsavtale: PensjonsavtaleTypes, idx: number) => (
						<React.Fragment key={idx}>
							<TitleValue title="Produktbetegnelse" value={pensjonsavtale?.produktBetegnelse} />
							<TitleValue
								title="Avtalekategori"
								value={showLabel('avtaleKategori', pensjonsavtale.avtaleKategori)}
							/>
							<DollyFieldArray
								header="Utbetalingsperioder"
								data={pensjonsavtale?.utbetalingsperioder}
								nested
							>
								{(periode: Utbetalingsperiode, idy: number) => (
									<React.Fragment key={idy}>
										<TitleValue title="Startalder år" value={periode?.startAlderAar} />
										<TitleValue
											title="Startalder måned"
											value={showLabel('maanedsvelger', periode?.startAlderMaaned)}
										/>
										<TitleValue title="Sluttalder år" value={periode?.sluttAlderAar} />
										<TitleValue
											title="Sluttalder måned"
											value={showLabel('maanedsvelger', periode.sluttAlderMaaned)}
										/>
										<TitleValue title="Årlig utbetaling" value={periode?.aarligUtbetaling} />
									</React.Fragment>
								)}
							</DollyFieldArray>
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
