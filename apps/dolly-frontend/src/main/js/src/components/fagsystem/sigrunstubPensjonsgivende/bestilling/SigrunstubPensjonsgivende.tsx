import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { Grunnlag } from '@/components/fagsystem/sigrunstub/SigrunstubTypes'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { formatDate } from '@/utils/DataFormatter'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { SigrunstubPensjonsgivendeData } from '@/components/fagsystem/sigrunstubPensjonsgivende/SigrunstubPensjonsgivendeTypes'

type SigrunstubPensjonsgivendeTypes = {
	sigrunstubPensjonsgivendeListe: Array<SigrunstubPensjonsgivendeData>
}

export const SigrunstubPensjonsgivende = ({
	sigrunstubPensjonsgivendeListe,
}: SigrunstubPensjonsgivendeTypes) => {
	if (!sigrunstubPensjonsgivendeListe || sigrunstubPensjonsgivendeListe.length === 0) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>Pensjonsgivende inntekt (Sigrun)</BestillingTitle>
				<DollyFieldArray header="Pensjonsgivende inntekt" data={sigrunstubPensjonsgivendeListe}>
					{(inntekt: SigrunstubPensjonsgivendeData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="År" value={inntekt?.inntektsaar} />
								<TitleValue title="Testdataeier" value={inntekt?.testdataEier} />
								{inntekt?.pensjonsgivendeInntekt?.length > 0 && (
									<DollyFieldArray header="Inntekter" data={inntekt?.pensjonsgivendeInntekt} nested>
										{(pensjonsgivendeInntekt: Grunnlag, idy: number) => (
											<React.Fragment key={`${idx}_${idy}`}>
												{Object.entries(pensjonsgivendeInntekt)?.map(([key, value]) => {
													const erDato = !isNaN(Date.parse(value))
													if (erDato && (key.includes('Dato') || key.includes('dato'))) {
														return (
															<TitleValue
																title={kodeverkKeyToLabel(key)}
																value={formatDate(value)}
															/>
														)
													}
													return (
														<TitleValue title={kodeverkKeyToLabel(key)} value={value?.toString()} />
													)
												})}
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
