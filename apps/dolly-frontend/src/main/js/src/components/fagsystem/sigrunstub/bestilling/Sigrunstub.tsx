import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import React from 'react'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { formatDate, showLabel } from '@/utils/DataFormatter'
import { SigrunKodeverk } from '@/config/kodeverk'
import { Grunnlag, SigrunstubData } from '@/components/fagsystem/sigrunstub/SigrunstubTypes'

type SigrunstubTypes = {
	sigrunstubListe: Array<SigrunstubData>
}

export const Sigrunstub = ({ sigrunstubListe }: SigrunstubTypes) => {
	if (!sigrunstubListe || sigrunstubListe.length === 0) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Lignet inntekt (Sigrun)</BestillingTitle>
				<DollyFieldArray header="Inntekt" data={sigrunstubListe}>
					{(inntekt: SigrunstubData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="År" value={inntekt?.inntektsaar} />
								<TitleValue title="Tjeneste" value={showLabel('tjeneste', inntekt?.tjeneste)} />
								{inntekt?.grunnlag?.length > 0 && (
									<DollyFieldArray
										header="Grunnlag fra Fastlands-Norge"
										data={inntekt?.grunnlag}
										nested
									>
										{(grunnlag: Grunnlag, idy: number) => (
											<React.Fragment key={`${idx}_${idy}`}>
												<TitleValue
													title="Type inntekt"
													value={grunnlag?.tekniskNavn}
													kodeverk={SigrunKodeverk[inntekt.tjeneste]}
												/>
												<TitleValue
													title={
														grunnlag?.tekniskNavn === 'skatteoppgjoersdato'
															? 'Oppgjørsdato'
															: 'Verdi'
													}
													value={
														grunnlag?.tekniskNavn === 'skatteoppgjoersdato'
															? formatDate(grunnlag?.verdi)
															: grunnlag?.verdi
													}
												/>
											</React.Fragment>
										)}
									</DollyFieldArray>
								)}
								{inntekt?.svalbardGrunnlag?.length > 0 && (
									<DollyFieldArray
										header="Grunnlag fra Svalbard"
										data={inntekt?.svalbardGrunnlag}
										nested
									>
										{(svalbardGrunnlag: Grunnlag, idy: number) => (
											<React.Fragment key={`${idx}_${idy}`}>
												<TitleValue
													title="Type inntekt"
													value={svalbardGrunnlag?.tekniskNavn}
													kodeverk={SigrunKodeverk[inntekt.tjeneste]}
												/>
												<TitleValue
													title={
														svalbardGrunnlag?.tekniskNavn === 'skatteoppgjoersdato'
															? 'Oppgjørsdato'
															: 'Verdi'
													}
													value={svalbardGrunnlag?.verdi}
												/>
											</React.Fragment>
										)}
									</DollyFieldArray>
								)}
							</React.Fragment>
						)
					}}
				</DollyFieldArray>
			</ErrorBoundary>
		</div>
	)
}
