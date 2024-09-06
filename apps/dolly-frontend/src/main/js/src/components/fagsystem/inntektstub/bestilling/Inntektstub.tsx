import { ErrorBoundary } from '@/components/ui/appError/ErrorBoundary'
import { BestillingTitle } from '@/components/bestilling/sammendrag/Bestillingsdata'
import { DollyFieldArray } from '@/components/ui/form/fieldArray/DollyFieldArray'
import { SigrunstubPensjonsgivendeData } from '@/components/fagsystem/sigrunstubPensjonsgivende/SigrunstubPensjonsgivendeTypes'
import React from 'react'
import { TitleValue } from '@/components/ui/titleValue/TitleValue'
import { Grunnlag } from '@/components/fagsystem/sigrunstub/SigrunstubTypes'
import { kodeverkKeyToLabel } from '@/components/fagsystem/sigrunstubPensjonsgivende/utils'
import { formatDate } from '@/utils/DataFormatter'
import genererTitleValueFelter from '@/components/bestilling/utils/genererTitleValueFelter'

export const Inntektstub = ({ inntektstub }: SigrunstubPensjonsgivendeTypes) => {
	if (!inntektstub || inntektstub?.inntektsinformasjon?.length === 0) {
		return null
	}

	return (
		<div className="person-visning">
			<ErrorBoundary>
				<BestillingTitle>A-ordningen (Inntektstub)</BestillingTitle>
				<DollyFieldArray header="Inntektsinformasjon" data={inntektstub.inntektsinformasjon}>
					{(inntektsinfo: SigrunstubPensjonsgivendeData, idx: number) => {
						return (
							<React.Fragment key={idx}>
								<TitleValue title="År/måned" value={inntektsinfo?.sisteAarMaaned} />
								<TitleValue title="Generer antall måneder" value={inntektsinfo?.antallMaaneder} />
								<TitleValue
									title="Rapporteringstidspunkt"
									value={inntektsinfo?.rapporteringsdato}
								/>
								<TitleValue title="Virksomhet (orgnr/id)" value={inntektsinfo?.virksomhet} />
								<TitleValue
									title="Opplysningspliktig (orgnr/id)"
									value={inntektsinfo?.opplysningspliktig}
								/>
								{inntektsinfo?.inntektsliste?.length > 0 && (
									<DollyFieldArray
										header="Inntekter per måned"
										data={inntektsinfo?.inntektsliste}
										nested
									>
										{(inntekt: Grunnlag, idy: number) => (
											<React.Fragment key={`${idx}_${idy}`}>
												{genererTitleValueFelter(inntekt)}
												{/*{Object.entries(inntekt)?.map(([key, value]) => {*/}
												{/*    const erDato = !isNaN(Date.parse(value))*/}
												{/*    if (erDato && (key.includes('Dato') || key.includes('dato'))) {*/}
												{/*        return (*/}
												{/*            <TitleValue*/}
												{/*                title={kodeverkKeyToLabel(key)}*/}
												{/*                value={formatDate(value)}*/}
												{/*            />*/}
												{/*        )*/}
												{/*    }*/}
												{/*    return (*/}
												{/*        <TitleValue title={kodeverkKeyToLabel(key)} value={value?.toString()} />*/}
												{/*    )*/}
												{/*})}*/}
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
