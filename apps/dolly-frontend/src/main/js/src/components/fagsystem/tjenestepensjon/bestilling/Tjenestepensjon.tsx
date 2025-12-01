import { formatDate, showLabel } from '@/utils/DataFormatter'
import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsvisning'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SelectOptionsManager as Options } from '@/service/SelectOptions'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { TpTypes, Ytelse } from '@/components/fagsystem/tjenestepensjon/TpTypes'

type TjenestepensjonProps = {
	pensjon: Array<TpTypes>
}

export const Tjenestepensjon = ({ pensjon }: TjenestepensjonProps) => {
	if (!pensjon || pensjon?.length < 1) {
		return null
	}

	const hentTpOrdningNavn = (tpnr: string) => {
		if (Options('tpOrdninger')?.length) {
			return Options('tpOrdninger').find((ordning: any) => ordning.value === tpnr)?.label
		}
		return tpnr
	}

	return (
		<div className="bestilling-visning">
			<ErrorBoundary>
				<BestillingTitle>Tjenestepensjon (TP)</BestillingTitle>
				<DollyFieldArray header="Ordning" data={pensjon}>
					{(ordning: TpTypes, idx: number) => (
						<React.Fragment key={idx}>
							<TitleValue title="TP-nr" value={hentTpOrdningNavn(ordning?.ordning)} size="xlarge" />
							{ordning?.ytelser?.length > 0 && (
								<DollyFieldArray header="Ytelser" data={ordning?.ytelser} nested>
									{(ytelse: Ytelse, idy: number) => (
										<React.Fragment key={idy}>
											<TitleValue
												title="Ytelse"
												value={showLabel('tjenestepensjonYtelseType', ytelse?.type)}
											/>
											<TitleValue
												title="Medlemskap f.o.m."
												value={formatDate(ytelse?.datoInnmeldtYtelseFom)}
											/>
											<TitleValue
												title="Ytelse f.o.m."
												value={formatDate(ytelse?.datoYtelseIverksattFom)}
											/>
											<TitleValue
												title="Ytelse t.o.m."
												value={formatDate(ytelse?.datoYtelseIverksattTom)}
											/>
										</React.Fragment>
									)}
								</DollyFieldArray>
							)}
						</React.Fragment>
					)}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
